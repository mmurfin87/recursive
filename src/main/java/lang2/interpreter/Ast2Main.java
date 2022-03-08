package lang2.interpreter;

import lang2.ast.*;
import lang2.interpreter.builtins.AddBuiltIn;
import lang2.interpreter.builtins.BuiltInDefinition;
import lang2.interpreter.builtins.PrintBuiltIn;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class Ast2Main
{
	public static void main(final String[] args)
	{
		final Context rootContext = new SimpleContext();

		/*
		(sum)
		{
			add(augend = 1, addend = 2);
			print(message = add);
		}
		 */
		final Assertion addAssertion = new Assertion("add", List.of(new Binding("augend", "1", true), new Binding("addend", "2", true)));
		final Assertion printAssertion = new Assertion("print", List.of(new Binding("message", "sum", false)));
		final Definition rootDefinition = new Definition(List.of("sum"), List.of(new Statement(addAssertion), new Statement(printAssertion)));
		final Statement rootStatement = new Statement(rootDefinition);
		execute(rootContext, rootStatement);
		log("ROOT CONTEXT: %s", rootContext);
	}

	public static void execute(@NonNull final Context context, @NonNull final Statement statement)
	{
		switch (statement.which)
		{
			case Statement.BINDING:
				executeBinding(context, statement.binding);
				break;
			case Statement.ASSERTION:
				executeAssertion(context, statement.assertion);
				break;
			case Statement.DEFINITION:
				executeDefinition(context, statement.definition);
				break;
			default:
				throw new RuntimeException("Unrecognized statement type: " + statement.which);
		}
	}

	public static void executeBinding(@NonNull final Context context, @NonNull final Binding binding)
	{
		switch (binding.which)
		{
			case Binding.UNBOUND:
				log("Unbound Identifier: %s", binding.identifier);
				break;
			case Binding.LITERAL:
				log("Binding literal %s = %s", binding.identifier, binding.rightLiteral);
				context.put(binding.identifier, new ContextValue(binding.rightLiteral));
				break;
			case Binding.IDENTIFIER:
				log("Binding identifier %s = %s", binding.identifier, binding.rightIdentifier);
				context.put(binding.identifier, context.get(binding.rightIdentifier).orElseThrow(() -> new RuntimeException("Expected a value for identifier " + binding.rightIdentifier)));
				break;
			case Binding.DEFINITION:
				log("Binding definition %s = %s", binding.identifier, binding.rightDefinition);
				context.put(binding.identifier, new ContextValue(binding.rightDefinition));
				break;
			default:
				throw new RuntimeException("Unrecognized binding type: " + binding.which);
		}
	}

	public static void executeAssertion(@NonNull final Context context, @NonNull final Assertion assertion)
	{
		log("Asserting %s", assertion.identifier);
		final ContextValue cv = context.get(assertion.identifier).orElse(null);
		if (cv == null)
		{
			final BuiltInDefinition builtInDefinition = builtInDefinitions.get(assertion.identifier);
			if (builtInDefinition == null)
				throw new RuntimeException("Context does not contain " + assertion.identifier);

			assertion.bindingList.forEach(b -> executeBinding(context, b));
			builtInDefinition.execute(context);
		}
		else
		{
			if (cv.isLiteral || cv.definition == null)
				throw new RuntimeException("Expected definition for " + assertion.identifier);
			final Definition definition = cv.definition;
			assertion.bindingList.forEach(b -> executeBinding(context, b));
			executeDefinition(context, definition);
		}
	}

	public static void executeDefinition(@NonNull final Context context, @NonNull final Definition definition)
	{
		log("Defining");
		final Context child = context.fork();
		definition.statementList.forEach(s -> execute(child, s));
		child.transfer(context, definition.identifierList, false);
	}

	private static void log(@NonNull final String format, @NonNull Object... args)
	{
		System.out.printf((format) + "%n", args);
	}

	private static final Map<String, BuiltInDefinition> builtInDefinitions = Map.of(
		"add", new AddBuiltIn(),
		"print", new PrintBuiltIn()
	);
}
