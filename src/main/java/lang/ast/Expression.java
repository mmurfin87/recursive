package lang.ast;

import lang.Context;
import lang.Term;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class Expression
{
	@NonNull
	public final Term value;

	public Expression evaluate(@NonNull Context context)
	{
		return this;
	}
}
