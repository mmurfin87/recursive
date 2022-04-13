package cps;

import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CpsMain
{
	public static void main(@NonNull final String[] args)
	{
		final List<Predicate<State>> constraints = List.of(
			s -> s.eq("a", "TRUE") && s.eq("b", "TRUE") && s.eq("c", "TRUE")

		);

		final Domain booleanDomain = Domain.of("FALSE", "TRUE");
		final Problem booleanProblem = Problem.builder()
			.variable("1", booleanDomain)
			.variable("2", booleanDomain)
			.variable("3", booleanDomain)
			.variable("4", booleanDomain)
			.variable("5", booleanDomain)
			.reject(s -> s.entrySet().stream().filter(e -> "TRUE".equals(e.getValue())).mapToInt(e -> Integer.parseInt(e.getKey())).sum() > 7)//.keys().stream().anyMatch(str -> s.eq(str, "FALSE")))
			.accept(s -> s.entrySet().stream().filter(e -> "TRUE".equals(e.getValue())).mapToInt(e -> Integer.parseInt(e.getKey())).sum() == 7)
			//.accept(accepting(constraints))
			.build();

		final Backtracker backtracker = new Backtracker();
		final List<Map<String, String>> solutions = backtracker.solve(booleanProblem).stream().map(State::map).toList();

		System.out.format("Found %d solutions%n", solutions.size());
		System.out.println(solutions.stream().map(m -> m.entrySet().stream().filter(e -> "TRUE".equals(e.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet())).collect(Collectors.toSet()));
	}

	public static Predicate<State> rejecting(@NonNull final List<Predicate<State>> constraints)
	{
		return state ->
		{
			for (int i = 0; i < constraints.size(); i++)
			{
				if (!constraints.get(i).test(state))
				{
					System.out.format("Rejecting Constraint %d%n", i);
					return true;
				}
			}
			return false;
		};
	}

	public static Predicate<State> accepting(@NonNull final List<Predicate<State>> constraints)
	{
		return state -> constraints.stream().allMatch(p -> p.test(state));
	}

	@FunctionalInterface
	public interface Mapping<T>
	{
		T get(@NonNull final String key);
	}

	public static <T> Predicate<State> mapped(@NonNull final Function<String, T> mapper, @NonNull final Predicate<Mapping<T>> m)
	{
		return state -> m.test(mapped(state, mapper));
	}

	public static <T> Mapping<T> mapped (@NonNull final State state, @NonNull final Function<String, T> mapper)
	{
		return key -> mapper.apply(state.get(key));
	}
}
