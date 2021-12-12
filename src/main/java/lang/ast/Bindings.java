package lang.ast;

import lang.Context;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
public class Bindings
{
	public Bindings with(@NonNull final String name, @NonNull final Expression value)
	{
		map.put(name, value);
		return this;
	}

	public Context load(@NonNull final Context context)
	{
		map.forEach((name, exp) -> context.put(name, exp.evaluate(context).value));
		return context;
	}

	private final Map<String, Expression> map = new HashMap<>();
}
