package lang;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static lang.Expression.l;

@ToString
public class Context
{
    public Context()
    {
        this.parent = null;
    }

    protected Context(@NonNull final Context parent)
    {
        this.parent = parent;
    }

    public Context scoped(Function<Context, String> scopedCallback)
    {
        final Context child = new Context(this);
        final String extract = scopedCallback.apply(child);
        final ExpressionContext ec = child.map.get(extract);
        if (ec != null)
            map.put(extract, ec);
        return this;
    }

    public Context put(@NonNull final String name, @NonNull final Expression value)
    {
        final ExpressionContext ec = new ExpressionContext(value);
        this.map.put(name, ec);
        //System.out.format("Put: %s=%s%n", name, ec);
        return this;
    }

    public Context putArray(@NonNull final String name, @NonNull final Expression... values)
    {
        final ExpressionContext ec = new ExpressionContext(values);
        this.map.put(name, ec);
        //System.out.format("Put: %s=%s%n", name, ec);
        return this;
    }

    private ExpressionContext search(@NonNull final String name)
    {
        final ExpressionContext local = map.get(name);
        if (local == null && parent != null)
            return parent.search(name);
        return local;
    }

    private ExpressionContext expectAny(@NonNull final String name)
    {
        final ExpressionContext ec = search(name);
        if (ec == null)
        {
            System.out.format("%s%n", map);
            throw new RuntimeException("Expected " + name);
        }
        return ec;
    }

    public Expression expectSingle(@NonNull final String name)
    {
        final ExpressionContext ec = expectAny(name);
        if (ec.expression == null)
            throw new RuntimeException("Expected Expression for " + name + " but found " + ec.expression);
        return ec.expression;
    }

    public Expression[] expectArray(@NonNull final String name)
    {
        final ExpressionContext ec = expectAny(name);
        if (ec.array == null)
            throw new RuntimeException("Expected Array " + name + " but found " + ec.expression);
        return ec.array;
    }

    public Expression expectLiteral(@NonNull final String name)
    {
        final Expression e = expectSingle(name);
        if (!e.isLiteral)
            throw new RuntimeException("Expected Literal for " + name + " but found " + e);
        return e;
    }

    @ToString
    @EqualsAndHashCode
    private static class ExpressionContext
    {
        public ExpressionContext(@NonNull final Expression expression)
        {
            this.expression = expression;
            this.array = null;
        }

        public ExpressionContext(@NonNull final Expression[] array)
        {
            this.expression = null;
            this.array = array;
        }

        public final Expression expression;
        public final Expression[] array;
    }

    private final Map<String, ExpressionContext> map = new HashMap<>();
    private final Context parent;
}
