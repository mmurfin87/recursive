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
		this.parent = null;
		this.map = new HashMap<>();
	}

	public SimpleContext(@NonNull final Context parent)
	{
		this.parent = parent;
		this.map = new HashMap<>();
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
	public Context put(@NonNull String identifier, @NonNull ContextValue value)
	{
		map.put(identifier, value);
		return this;
	}

	@Override
	public Optional<ContextValue> get(@NonNull String identifier)
	{
		//return getLocal(identifier)
		return Optional.ofNullable(map.get(identifier))
			.or(() -> parent != null ? parent.get(identifier) : Optional.empty());
	}
/*
	@Override
	public Optional<ContextValue> getLocal(@NonNull String identifier)
	{
		return Optional.ofNullable(map.get(identifier));
	}
*/
	@Override
	public Context transfer(@NonNull final Context target, @NonNull final List<String> identifierList, boolean global)
	{
		if (target != this)
		{
			if (global && parent != null)
				parent.transfer(target, identifierList,true);
			identifierList.forEach(i ->
			{
				final ContextValue cv = map.get(i);
				if (cv != null)
					target.put(i, cv);
			});
		}
		return this;
	}

	private final Context parent;
	@NonNull
	private final Map<String, ContextValue> map;
}
