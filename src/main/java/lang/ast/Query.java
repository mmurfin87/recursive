package lang.ast;

import lang.Binding;
import lang.Context;
import lang.Term;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class Query extends Expression
{
	public Query(@NonNull final Term name, @NonNull final Invocation invocation)
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
			return new Binding[] { new Binding(value.name, value.name) };
		});
		return new Expression(context.expect(value.name));
	}

	private final Invocation invocation;
}
