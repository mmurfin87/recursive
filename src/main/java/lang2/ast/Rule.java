package lang2.ast;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;

@AllArgsConstructor
public class Rule
{
	@NonNull
	public final Assertion left;
	@NonNull
	public final List<Assertion> right;
}
