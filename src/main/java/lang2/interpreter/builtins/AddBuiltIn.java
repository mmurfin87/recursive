package lang2.interpreter.builtins;

import lang2.ast.Literal;
import lang2.interpreter.Context;
import lang2.interpreter.ContextValue;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

import static lang2.interpreter.builtins.ContextUtils.expectLiteralOrNothing;

public class AddBuiltIn extends BuiltInDefinition
{
	public AddBuiltIn()
	{
		super(List.of("augend", "addend", "sum"));
	}

	@Override
	public void executeDirectly(@NonNull Context context)
	{
		final BigDecimal augend = expectLiteralOrNothing("augend", context).map(BigDecimal::new).orElse(null);
		final BigDecimal addend = expectLiteralOrNothing("addend", context).map(BigDecimal::new).orElse(null);
		final BigDecimal sum = expectLiteralOrNothing("sum", context).map(BigDecimal::new).orElse(null);
		if (augend != null && addend != null && sum != null)
		{
			if (!augend.add(addend).equals(sum))
				throw new RuntimeException(String.format("%s + %s != %s", augend, addend, sum));
		}
		else if (augend != null && addend != null && sum == null)
			put(context, "sum", augend.add(addend));
		else if (augend != null && addend == null && sum != null)
			put(context, "addend", sum.subtract(augend));
		else if (augend == null && addend != null && sum != null)
			put(context, "augend", sum.subtract(addend));
		else
			throw new RuntimeException("Missing two parameters for addition - partial execution unsupported");
	}

	private static void put(@NonNull final Context context, @NonNull final String alias, @NonNull final BigDecimal value)
	{
		context.put(alias, new ContextValue(new Literal(value.toPlainString())));
	}
}
