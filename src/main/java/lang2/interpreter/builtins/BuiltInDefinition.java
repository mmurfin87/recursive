package lang2.interpreter.builtins;

import lang2.interpreter.Context;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
public abstract class BuiltInDefinition
{
	@NonNull
	public final List<String> identifierList;

	/**
	 * Execute the definition without forking the provided context and scoping changes to the context to values present in {@link #identifierList}
	 * @param context the not-null context to execute in
	 */
	public abstract void executeDirectly(@NonNull final Context context);
}
