package lang2.interpreter;

import lombok.NonNull;

import java.util.Optional;

public interface Resolution
{
	Resolution resolve(@NonNull Context2 context);
	Optional<String> literal();
	//boolean isLiteral();
}
