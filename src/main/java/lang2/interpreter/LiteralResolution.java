package lang2.interpreter;

import lang2.ast.Literal;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.Optional;

@ToString
@AllArgsConstructor
public class LiteralResolution implements Resolution
{
	@Override
	public Resolution resolve(@NonNull Context2 context)
	{
		return this;
	}

	@Override
	public Optional<String> literal()
	{
		return Optional.of(literal.value);
	}

	@NonNull
	private final Literal literal;
}
