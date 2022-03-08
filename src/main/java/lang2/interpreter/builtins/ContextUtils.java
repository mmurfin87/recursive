package lang2.interpreter.builtins;

import lang2.interpreter.Context;
import lang2.interpreter.ContextValue;
import lombok.NonNull;

import java.util.Optional;

public class ContextUtils
{
	public static Optional<String> expectLiteralOrNothing(@NonNull final String identifier, @NonNull final Context context)
	{
		final ContextValue cv = context.get(identifier).orElse(null);
		if (cv == null)
			return Optional.empty();
		else if (!cv.isLiteral)
			throw new RuntimeException("Expected Literal: " + identifier);
		return Optional.of(cv.literal);
	}
}
