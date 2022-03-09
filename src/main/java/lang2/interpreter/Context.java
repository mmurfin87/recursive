package lang2.interpreter;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface Context
{
	Context fork();
	//Context splice(Context parent);
	Optional<Context> parent();
	Context put(@NonNull String identifier, @NonNull ContextValue value);
	Optional<ContextValue> get(@NonNull String identifier);
	Context transferLocal(@NonNull Context target);
	Context transferLocal(@NonNull Context target, @NonNull List<String> identifierList);
}
