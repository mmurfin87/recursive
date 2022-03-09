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

	public abstract void executeDirectly(@NonNull final Context context);
}
