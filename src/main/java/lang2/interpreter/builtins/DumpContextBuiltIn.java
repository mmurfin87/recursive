package lang2.interpreter.builtins;

import lang2.interpreter.Context;
import lang2.interpreter.ContextValue;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DumpContextBuiltIn extends BuiltInDefinition
{
	public DumpContextBuiltIn()
	{
		super(List.of());
	}

	@Override
	public void executeDirectly(@NonNull Context context)
	{
		final Map<String, ContextValue> map = new HashMap<>();
		traverse(context, map);
		System.out.println("CONTEXT: " + map);
	}

	private void traverse(@NonNull Context context, @NonNull final Map<String, ContextValue> map)
	{
		context.parent()
			.ifPresent(parent -> traverse(parent, map));
		context.transferLocal(new MappingContext(map));
	}

	@AllArgsConstructor
	private static class MappingContext implements Context
	{
		private final Map<String, ContextValue> map;

		@Override
		public Context fork()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional<Context> parent()
		{
			throw new UnsupportedOperationException();
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
			throw new UnsupportedOperationException();
		}

		@Override
		public Context transferLocal(@NonNull Context target)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Context transferLocal(@NonNull Context target, @NonNull List<String> identifierList)
		{
			throw new UnsupportedOperationException();
		}
	}
}
