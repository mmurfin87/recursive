package lang2.interpreter;

import lang2.ast.Definition;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ContextValue
{
	public final String literal;
	public final Definition definition;
	public final boolean isLiteral;

	public ContextValue(@NonNull final String literal)
	{
		this.literal = literal;
		this.definition = null;
		this.isLiteral = true;
	}

	public ContextValue(@NonNull final Definition definition)
	{
		this.literal = null;
		this.definition = definition;
		this.isLiteral = false;
	}
}
