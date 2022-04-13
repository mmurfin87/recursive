package cps;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Backtracker implements ConstraintProblemSolver
{
	public List<State> solve(@NonNull final Problem problem)
	{
		final List<State> solutions = new ArrayList<>();
		solve(problem, solutions, problem.root());
		return solutions;
	}

	private void solve(@NonNull final Problem problem, @NonNull final List<State> solutions, @NonNull final State candidate)
	{
		if (problem.reject.test(candidate.map()))
		{
			System.out.format("Rejecting Candidate: %s%n", candidate.map());
			return;
		}
		if (problem.accept.test(candidate.map()))
		{
			solutions.add(candidate);
			System.out.format("Accepting Candidate: %s%n", candidate.map());
		}
		else
			System.out.format("Extending Candidate: %s%n", candidate.map());
		State subCandidate = candidate.first();
		while (subCandidate != null)
		{
			solve(problem, solutions, subCandidate);
			subCandidate = subCandidate.next();
		}
	}
}
