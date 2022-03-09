package lang2.ast;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class Binding
{
	public Binding(@NonNull final String identifier)
	{
		this.identifier = identifier;
		this.rightLiteral = null;
		this.rightIdentifier = null;
		this.rightDefinition = null;
		this.which = UNBOUND;
	}

	public Binding(@NonNull final String identifier, @NonNull final Literal literal)
	{
		this.identifier = identifier;
		this.rightLiteral = literal;
		this.rightIdentifier = null;
		this.rightDefinition = null;
		this.which = LITERAL;
	}

	public Binding(@NonNull final String identifier, @NonNull final String rightIdentifier)
	{
		this.identifier = identifier;
		this.rightLiteral = null;
		this.rightIdentifier = rightIdentifier;
		this.rightDefinition = null;
		this.which = IDENTIFIER;
	}

	public Binding(@NonNull final String identifier, @NonNull final Definition definition)
	{
		this.identifier = identifier;
		this.rightLiteral = null;
		this.rightIdentifier = null;
		this.rightDefinition = definition;
		this.which = DEFINITION;
	}

	@Override
	public String toString()
	{
		return identifier + " = " + switch (which)
		{
			case UNBOUND, default -> "<UNBOUND>";
			case LITERAL -> rightLiteral;
			case IDENTIFIER -> rightIdentifier;
			case DEFINITION ->  rightDefinition.toString();
		};
	}

	@NonNull
	public final String identifier;
	public final Literal rightLiteral;
	public final String rightIdentifier;
	public final Definition rightDefinition;
	public final int which;

	public static final int UNBOUND = 0;
	public static final int LITERAL = 1;
	public static final int IDENTIFIER = 2;
	public static final int DEFINITION = 3;
}
