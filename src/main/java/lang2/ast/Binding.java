package lang2.ast;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
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

	public Binding(@NonNull final String identifier, @NonNull final String right, boolean isLiteral)
	{
		this.identifier = identifier;
		this.rightLiteral = isLiteral ? right : null;
		this.rightIdentifier = isLiteral ? null : right;
		this.rightDefinition = null;
		this.which = isLiteral ? LITERAL : IDENTIFIER;
	}

	public Binding(@NonNull final String identifier, @NonNull final Definition definition)
	{
		this.identifier = identifier;
		this.rightLiteral = null;
		this.rightIdentifier = null;
		this.rightDefinition = definition;
		this.which = DEFINITION;
	}

	@NonNull
	public final String identifier;
	public final String rightLiteral;
	public final String rightIdentifier;
	public final Definition rightDefinition;
	public final int which;

	public static final int UNBOUND = 0;
	public static final int LITERAL = 1;
	public static final int IDENTIFIER = 2;
	public static final int DEFINITION = 3;
}
