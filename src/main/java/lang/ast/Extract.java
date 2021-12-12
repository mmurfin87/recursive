package lang.ast;

import lang.Binding;
import lang.Context;
import lang.Term;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class Extract extends Expression
{
	public Extract(@NonNull final Term name, @NonNull final Invocation invocation)
	{
		super(name);
		this.invocation = invocation;
	}

	@Override
	public Expression evaluate(@NonNull Context context)
	{
		context.scoped(ctx ->
		{
			invocation.evaluate(ctx);
			return new Binding[] { new Binding(value.value, value.value) };
		});
		return new Expression(context.expect(value.value));
	}

	private final Invocation invocation;
}
