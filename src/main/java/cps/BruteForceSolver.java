package cps;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BruteForceSolver implements ConstraintProblemSolver
{
	@Override
	public List<State> solve(@NonNull Problem problem)
	{
		final List<State> solutions = new ArrayList<>();
		problem.root();
		final CombinatorialMapIterator<String, String> iterator = new CombinatorialMapIterator<>(problem.variables);
		while (iterator.hasNext())
		{
			final Map<String, String> candidate = iterator.next();
			//if (!problem.reject(candidate) && problem.accept(candidate))
			//	solutions.add(candidate);
		}
		return solutions;
	}
}
