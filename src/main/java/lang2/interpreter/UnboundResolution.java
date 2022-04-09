package lang2.interpreter;

import lombok.NonNull;

import java.util.Optional;

public class UnboundResolution implements Resolution
{
	@Override
	public Resolution resolve(@NonNull Context2 context)
	{
		return this;
	}

	@Override
	public Optional<String> literal()
	{
		return Optional.empty();
	}
}
