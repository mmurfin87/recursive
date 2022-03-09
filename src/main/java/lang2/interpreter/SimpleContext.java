package lang2.interpreter;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ToString
@EqualsAndHashCode
public class SimpleContext implements Context
{
	public SimpleContext()
	{
		this(null, null);
	}

	public SimpleContext(@NonNull final Context parent)
	{
		this(parent, null);
	}

	private SimpleContext(final Context parent, final Map<String, ContextValue> map)
	{
		this.parent = parent;
		this.map = map == null ? new HashMap<>() : new HashMap<>(map);
		this.aliases = new HashMap<>();
	}

	@Override
	public Context fork()
	{
		return new SimpleContext(this);
	}

	@Override
	public Optional<Context> parent()
	{
		return Optional.ofNullable(parent);
	}

	@Override
	public Context put(@NonNull final String identifier, @NonNull final ContextValue value)
	{
		if (value.which == ContextValue.IDENTIFIER)
		{
			aliases.put(identifier, value.literal);
			map.remove(identifier);
		}
		else
		{
			map.put(identifier, value);
			aliases.remove(identifier);
		}
		return this;
	}

	@Override
	public Optional<ContextValue> get(@NonNull final String identifier)
	{
		return resolveLocal(identifier)
			.or(() -> parent != null ? parent.get(identifier) : Optional.empty());
	}

	private Optional<ContextValue> resolveLocal(@NonNull final String identifier)
	{
		String alias = identifier;
		ContextValue cv = map.get(alias);
		while (cv == null && alias != null)
		{
			alias = aliases.get(alias);
			if (alias != null)
				cv = get(alias).orElse(null);//map.get(alias);
		}
		return Optional.ofNullable(cv);
	}

	@Override
	public Context transferLocal(@NonNull Context target)
	{
		map.forEach(target::put);
		aliases.forEach((a,i) -> target.put(a, new ContextValue(i)));
		return this;
	}

	@Override
	public Context transferLocal(@NonNull Context target, @NonNull List<String> identifierList)
	{
		identifierList.forEach(i -> get(i).ifPresent(cv -> target.put(i, cv)));
		return this;
	}

	private final Context parent;
	@NonNull
	private final Map<String, ContextValue> map;
	@NonNull
	private final Map<String, String> aliases;
}
