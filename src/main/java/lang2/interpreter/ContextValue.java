package lang2.interpreter;

import lang2.ast.Definition;
import lang2.ast.Literal;
import lang2.interpreter.builtins.BuiltInDefinition;
import lombok.NonNull;

public class ContextValue
{
	public final String literal;
	public final Definition definition;
	public final BuiltInDefinition builtinDefinition;
	public final int which;

	public static final int LITERAL = 1;
	public static final int IDENTIFIER = 2;
	public static final int DEFINITION = 3;
	public static final int BUILTIN_DEFINITION = 4;

	public ContextValue(@NonNull final Literal literal)
	{
		this.literal = literal.value;
		this.definition = null;
		this.builtinDefinition = null;
		this.which = LITERAL;
	}

	public ContextValue(@NonNull final String alias)
	{
		this.literal = alias;
		this.definition = null;
		this.builtinDefinition = null;
		this.which = IDENTIFIER;
	}

	public ContextValue(@NonNull final Definition definition)
	{
		this.literal = null;
		this.definition = definition;
		this.builtinDefinition = null;
		this.which = DEFINITION;
	}

	public ContextValue(@NonNull final BuiltInDefinition builtinDefinition)
	{
		this.literal = null;
		this.definition = null;
		this.builtinDefinition = builtinDefinition;
		this.which = BUILTIN_DEFINITION;
	}

	@Override
	public String toString()
	{
		return switch(which)
		{
			case LITERAL -> literal;
			case IDENTIFIER -> literal;
			case DEFINITION -> definition.toString();
			case BUILTIN_DEFINITION -> builtinDefinition.toString();
			default -> "[UNKNOWN]";
		};
	}
}
