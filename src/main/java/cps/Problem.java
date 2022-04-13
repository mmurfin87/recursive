package cps;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;

import java.util.*;
import java.util.function.Predicate;

@Builder
@AllArgsConstructor
public class Problem
{
	@Singular
	public final Map<String, Domain> variables;
	public final Predicate<Map<String, String>> reject;
	public final Predicate<Map<String, String>> accept;

	public State root()
	{
		return new State(variables, null, null);
	}
}
