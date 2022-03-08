package lang2.interpreter.builtins;

import lang2.interpreter.Context;
import lang2.interpreter.ContextValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@ToString
@AllArgsConstructor
public abstract class BuiltInDefinition
{
	@NonNull
	public final List<String> identifierList;

	public void execute(@NonNull final Context context)
	{
		final Context child = context.fork();
		executeInternal(child);
		child.transfer(context, identifierList, false);
	}

	protected abstract void executeInternal(@NonNull final Context context);
}
