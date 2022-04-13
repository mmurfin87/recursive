package cps;

import lombok.NonNull;

import java.util.List;

@FunctionalInterface
public interface ConstraintProblemSolver
{
	List<State> solve(@NonNull final Problem problem);
}
