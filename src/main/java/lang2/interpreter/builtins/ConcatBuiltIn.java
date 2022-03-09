package lang2.interpreter.builtins;

import lang2.ast.Literal;
import lang2.interpreter.Context;
import lang2.interpreter.ContextValue;
import lombok.NonNull;

import java.util.List;

import static lang2.interpreter.builtins.ContextUtils.expectLiteralOrNothing;

public class ConcatBuiltIn extends BuiltInDefinition
{
	public ConcatBuiltIn()
	{
		super(List.of("base", "join", "concatenation"));
	}

	@Override
	public void executeDirectly(@NonNull Context context)
	{
		final String base = expectLiteralOrNothing("base", context).orElse(null);
		final String join = expectLiteralOrNothing("join", context).orElse(null);
		final String concatenation = expectLiteralOrNothing("concatenation", context).orElse(null);
		if (base != null && join != null && concatenation != null)
		{
			if (!concatenation.equals(base + join))
				throw new RuntimeException(String.format("%s + %s != %s", base, join, concatenation));
		}
		else if (base != null && join != null && concatenation == null)
			context.put("concatenation", new ContextValue(new Literal(base + join)));
		else if (base != null && join == null && concatenation != null)
			context.put("join", new ContextValue(new Literal(concatenation.substring(base.length()))));
		else if (base == null && join != null && concatenation != null)
			context.put("base", new ContextValue(new Literal(concatenation.substring(0, join.length()))));
		else
			throw new RuntimeException(String.format("Missing more than one parameter: \"%s\" + \"%s\" = \"%s\"", base, join, concatenation));

	}
}
