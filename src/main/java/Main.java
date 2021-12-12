import lang.*;
import lang.ast.Bindings;
import lang.ast.Expression;
import lang.ast.Extract;
import lang.ast.Invocation;

import static lang.Term.*;

public class Main
{
	/*
	sum:add(augend = "1", addend = difference:subtract(minuend="3", subtrahend="1", difference)), sum)

	invocation - name with multiple bindings of names to sub-values
	extract - invocation appearing as the value of a single potentially unbound term
	literal
	*/
	public static void main(final String[] args)
	{
		final Context context = new Context();
		// Axioms
		context.put("add", new AdditionTerm());
		context.put("subtract", new SubtractionTerm());

		final Expression result = new Extract(l("sum"), new Invocation(t("add"), new Bindings()
			.with("augend", new Expression(l(1)))
			.with("addend", new Extract(l("difference"), new Invocation(t("subtract"), new Bindings()
				.with("minuend", new Expression(l(4)))
				.with("difference", new Expression(l(2)))
			)))
		)).evaluate(context);

		System.out.format("Result: %s%n", result);

/*
		context.scoped(ctx ->
		{
			// subtract
			ctx.put("minuend", l("3"));
			ctx.put("subtrahend", l("1"));
			final Term subtract = ctx.expect("subtract");
			subtract.resolve(ctx);

			final Term difference = ctx.expectLiteral("difference");
			System.out.format("difference = %s%n", difference);
			return new Binding[] { new Binding("difference", "addend") };
		});

		context.put("augend", l("1"));
		//context.put("addend", context.expect("difference"));
		context.expect("add").resolve(context);

		final Term sum = context.expect("sum");
		System.out.format("sum = %s%n", sum);

 */
	}
}