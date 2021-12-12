package lang.ast;

import lang.Context;
import lang.Term;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class Invocation extends Expression
{
	public Invocation(@NonNull final Term value, @NonNull final Bindings bindings)
	{
		super(value);
		this.bindings = bindings;
	}

	@Override
	public Expression evaluate(@NonNull Context context)
	{
		return new Expression(bindings
			.load(context)
			.expect(value.name)
			.resolve(context)
		);
	}

	private final Bindings bindings;
}
