package lang2.ast;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode
public class Statement
{
	public final Binding binding;
	public final Assertion assertion;
	public final Definition definition;
	public final int which;
	public static final int BINDING = 1;
	public static final int ASSERTION = 2;
	public static final int DEFINITION = 3;

	public Statement(@NonNull final Binding binding)
	{
		this.binding = binding;
		this.which = BINDING;
		this.assertion = null;
		this.definition = null;
	}

	public Statement(@NonNull final Assertion assertion)
	{
		this.assertion = assertion;
		this.which = ASSERTION;
		this.binding = null;
		this.definition = null;
	}

	public Statement(@NonNull final Definition definition)
	{
		this.definition = definition;
		this.which = DEFINITION;
		this.binding = null;
		this.assertion = null;
	}

	@Override
	public String toString()
	{
		return (switch(which)
		{
			case BINDING -> binding;
			case ASSERTION -> assertion;
			case DEFINITION -> definition;
			default -> "UNKNOWN";
		}).toString();
	}
}
