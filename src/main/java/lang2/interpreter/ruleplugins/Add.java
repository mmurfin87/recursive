package lang2.interpreter.ruleplugins;

import lang2.ast.Assertion;
import lang2.ast.Binding;
import lang2.ast.Literal;
import lang2.ast.Rule;
import lang2.interpreter.Context2;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public class Add implements PluginRule
{
	@Override
	public String declareIdentifier()
	{
		return "add";
	}

	@Override
	public List<String> declareScope()
	{
		return List.of("addend", "augend", "sum");
	}

	@Override
	public void executeDirectly(@NonNull Context2 context)
	{
		final BigDecimal addend = context.resolve("addend").literal().map(BigDecimal::new).orElse(null);
		final BigDecimal augend = context.resolve("augend").literal().map(BigDecimal::new).orElse(null);
		final BigDecimal sum = context.resolve("sum").literal().map(BigDecimal::new).orElse(null);
		//System.out.format("Resolved addend = %s, augend = %s, sum = %s%n", addend, augend, sum);
		if (augend != null && addend != null && sum != null)
		{
			if (!augend.add(addend).equals(sum))
				throw new RuntimeException(String.format("%s + %s != %s", augend, addend, sum));
		}
		else if (augend != null && addend != null && sum == null)
			context.put(new Binding("sum", new Literal(augend.add(addend).toString())));
		else if (augend != null && addend == null && sum != null)
			context.put(new Binding("addend", new Literal(sum.subtract(augend).toString())));
		else if (augend == null && addend != null && sum != null)
			context.put(new Binding("augend", new Literal(sum.subtract(addend).toString())));
		else
			throw new RuntimeException("Missing two parameters for addition - partial execution unsupported");
	}
}
