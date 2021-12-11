package lang;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.stream.Stream;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Expression
{
    public static Expression l(@NonNull final Object value)
    {
        return new Expression(value.toString(), true);
    }

    public static Expression r(@NonNull final String value)
    {
        return new Expression(value, false);
    }

    @NonNull
    public final String value;
    public final boolean isLiteral;

    public Expression resolve(@NonNull final Context context)
    {
        if (isLiteral)
            return this;
        switch (value)
        {
            case "extract":
            {
                final Expression key = context.expectSingle("key").resolve(context);
                if (!key.isLiteral)
                    return this;
                final Expression resolve = context.expectSingle("resolve").resolve(context);
                return context.expectSingle(key.value);
            }
            case "add":
            {
                final Expression[] addends = Stream.of(context.expectArray("addends"))
                    .map(e -> e.resolve(context))
                    .toArray(Expression[]::new);
                if (Stream.of(addends).allMatch(e -> e.isLiteral))
                {
                    final BigDecimal sum = Stream.of(addends)
                        .map(e -> e.value)
                        .map(BigDecimal::new)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    context.put("sum", l(sum.toString()));
                }
                return this;
            }
            case "subtract":
            {
                final Expression minuend = context.expectSingle("minuend");
                final Expression subtrahend = context.expectSingle("subtrahend");
                minuend.resolve(context);
                if (minuend.isLiteral && subtrahend.isLiteral)
                {
                    final BigDecimal m = new BigDecimal(minuend.value), s = new BigDecimal(subtrahend.value);
                    final Expression difference = l(m.subtract(s));
                    context.put("difference", difference);
                }
                return this;
            }
        }
        return this;
    }
}
