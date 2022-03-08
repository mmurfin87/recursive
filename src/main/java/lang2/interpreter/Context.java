package lang2.interpreter;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface Context
{
	Context fork();
	Optional<Context> parent();
	Context put(@NonNull String identifier, @NonNull ContextValue value);
	Optional<ContextValue> get(@NonNull String identifier);
	//Optional<ContextValue> getLocal(@NonNull String identifier);
	Context transfer(@NonNull Context target, List<String> identifierList, boolean global);
}
