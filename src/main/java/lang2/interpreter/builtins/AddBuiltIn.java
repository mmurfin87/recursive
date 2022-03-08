package lang2.interpreter.builtins;

import lang2.interpreter.Context;
import lang2.interpreter.ContextValue;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

import static lang2.interpreter.builtins.ContextUtils.expectLiteralOrNothing;

public class AddBuiltIn extends BuiltInDefinition
{
	public AddBuiltIn()
	{
		super(List.of("augend", "addend", "sum"));
	}

	@Override
	public void executeInternal(@NonNull Context context)
	{
		final Optional<String> augend = expectLiteralOrNothing("augend", context);
		final Optional<String> addend = expectLiteralOrNothing("addend", context);
		final Optional<String> sum = expectLiteralOrNothing("sum", context);
		if (augend.isPresent() && addend.isPresent() && sum.isEmpty())
		{
			final int aug = Integer.parseInt(augend.get());
			final int add = Integer.parseInt(addend.get());
			context.put("sum", new ContextValue(String.valueOf(aug + add)));
		}
	}
}
