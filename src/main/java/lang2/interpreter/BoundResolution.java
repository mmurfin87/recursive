package lang2.interpreter;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

@AllArgsConstructor
public class BoundResolution implements Resolution
{
	@Override
	public Resolution resolve(@NonNull Context2 context)
	{
		return context.resolve(identifier);
	}

	@Override
	public Optional<String> literal()
	{
		return Optional.empty();
	}

	@NonNull
	private final String identifier;
}
