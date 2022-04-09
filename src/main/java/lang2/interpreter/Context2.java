package lang2.interpreter;

import lang2.ast.Assertion;
import lang2.ast.Binding;
import lang2.ast.Rule;
import lombok.NonNull;

public interface Context2
{
	Context2 put(@NonNull final Assertion assertion);
	Context2 put(@NonNull final Rule rule);
	Context2 put(@NonNull final Binding binding);
	Resolution resolve(@NonNull final String identifier);
}
