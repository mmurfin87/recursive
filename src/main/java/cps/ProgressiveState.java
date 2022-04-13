package cps;

public interface ProgressiveState extends IState
{
	State first();

	State next();
}
