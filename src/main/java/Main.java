import lang.Context;
import lang.Expression;

import static lang.Expression.*;

public class Main
{
    /*
    add(addends=["1", difference:subtract(minuend="3", subtrahend="1", difference))], sum)
    */
    public static void main(final String[] args)
    {
        final Context context = new Context();
        // Axioms
        context.put("add", r("add"));
        context.put("subtract", r("subtract"));

        context.scoped(ctx ->
        {
            // subtract
            ctx.put("minuend", l("3"));
            ctx.put("subtrahend", l("1"));
            final Expression subtract = ctx.expectSingle("subtract");
            subtract.resolve(ctx);

            final Expression difference = ctx.expectLiteral("difference");
            System.out.format("Difference: %s%n", difference);
            return "difference";
        });

        context.putArray("addends", l("1"), context.expectSingle("difference"));
        context.expectSingle("add").resolve(context);
        System.out.format("Context: %s%n", context);
        final Expression sum = context.expectSingle("sum");
        System.out.format("%s%n", sum);
    }
}