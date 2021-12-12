package lang;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

	public Context scoped(Function<Context, Binding[]> scopedCallback)
	{
		final String tabs = tabs();
		System.out.format("%s----- SCOPE START%n", tabs);

		final Context child = new Context(this);
		final Binding[] extracts = scopedCallback.apply(child);
		for (final Binding extract : extracts)
		{
			final ExpressionContext ec = child.map.get(extract.name);
			if (ec != null)
			{
				System.out.format("%sPut: %s=%s%n", tabs, extract.alias, ec);
				map.put(extract.alias, ec);
			}
		}
		System.out.format("%s----- SCOPE END%n", tabs);
		return this;
	}

	public Context put(@NonNull final String name, @NonNull final Term value)
	{
		final ExpressionContext ec = new ExpressionContext(value);
		this.map.put(name, ec);
		System.out.format("%sPut: %s=%s%n", tabs(), name, ec);
		return this;
	}

	private ExpressionContext search(@NonNull final String name)
	{
		final ExpressionContext local = map.get(name);
		if (local == null && parent != null)
			return parent.search(name);
		return local;
	}

	public Term expect(@NonNull final String name)
	{
		return accept(name).orElseThrow(() -> new RuntimeException("Expected " + name));
	}

	public Optional<Term> accept(@NonNull final String name)
	{
		final ExpressionContext ec = search(name);
		return ec == null ? Optional.empty() : Optional.of(ec.expression);
	}

	public Term expectLiteral(@NonNull final String name)
	{
		final Term e = expect(name);
		if (!e.isLiteral)
			throw new RuntimeException("Expected Literal for " + name + " but found " + e);
		return e;
	}

	private String tabs()
	{
		int tabCount = 0;
		Context p = parent;
		while (p != null)
		{
			tabCount++;
			p = p.parent;
		}
		final char[] tb = new char[tabCount];
		Arrays.fill(tb, '\t');
		return new String(tb);
	}

	@ToString
	@EqualsAndHashCode
	private static class ExpressionContext
	{
		public ExpressionContext(@NonNull final Term expression)
		{
			this.expression = expression;
		}

		public final Term expression;
	}

	private final Map<String, ExpressionContext> map = new HashMap<>();
	private final Context parent;
}
