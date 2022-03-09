package lang2.interpreter;

import lang2.ast.Assertion;
import lang2.ast.Binding;
import lang2.ast.Definition;
import lang2.ast.Statement;
import lang2.interpreter.builtins.BuiltInDefinition;
import lombok.NonNull;

public class Interpreter
{
	public boolean log = true;

	public void interpret(@NonNull final Context rootContext, @NonNull final BuiltInDefinition rootDefinition)
	{
		rootDefinition.executeDirectly(rootContext);
	}

	public void executeStatement(@NonNull final Context context, @NonNull final Statement statement)
	{
		switch (statement.which)
		{
			case Statement.BINDING -> executeBinding(context, statement.binding);
			case Statement.ASSERTION -> executeAssertion(context, statement.assertion);
			case Statement.DEFINITION -> executeDefinition(context, statement.definition);//new ExecutableDefinition(statement.definition, this::executeStatement).execute(context);
			default -> throw new RuntimeException("Unrecognized statement type: " + statement.which);
		}
	}

	public void executeBinding(@NonNull final Context context, @NonNull final Binding binding)
	{
		switch (binding.which)
		{
			case Binding.UNBOUND -> log("Unbound Identifier: %s", binding.identifier);
			case Binding.LITERAL ->
				{
					log("Binding literal %s = %s", binding.identifier, binding.rightLiteral);
					context.put(binding.identifier, new ContextValue(binding.rightLiteral));
				}
			case Binding.IDENTIFIER ->
				{
					log("Binding identifier %s = %s", binding.identifier, binding.rightIdentifier);
					context.put(binding.identifier, new ContextValue(binding.rightIdentifier));
					//context.put(binding.identifier, context.get(binding.rightIdentifier).orElseThrow(() -> new RuntimeException("Expected a value for identifier " + binding.rightIdentifier)));
				}
			case Binding.DEFINITION ->
				{
					log("Binding definition %s = %s", binding.identifier, binding.rightDefinition);
					//context.put(binding.identifier, new ContextValue(new ExecutableDefinition(binding.rightDefinition, this::executeStatement)));
					context.put(binding.identifier, new ContextValue(binding.rightDefinition));
				}
			default -> throw new RuntimeException("Unrecognized binding type: " + binding.which);
		}
	}

	public void executeAssertion(@NonNull final Context context, @NonNull final Assertion assertion)
	{
		log("Asserting %s with %s", assertion.identifier, context);

		final ContextValue cv = context.get(assertion.identifier)
			.orElseThrow(() -> new RuntimeException("Context does not contain " + assertion.identifier));
		if (cv.which != ContextValue.DEFINITION && cv.which != ContextValue.BUILTIN_DEFINITION)
			throw new RuntimeException("Expected definition for " + assertion.identifier);

		// For all bindings to identifiers that are present in the context, execute the updates to the context
		assertion.bindingList.stream()
		//	.filter(b -> b.which != Binding.IDENTIFIER || context.get(b.rightIdentifier).isPresent())
			.forEach(b -> executeBinding(context, b));

		if (cv.which == ContextValue.DEFINITION)
			executeDefinition(context, cv.definition);
		else
			executeBuiltInDefinition(context, cv.builtinDefinition);

		// After executing the definition, we expect values for all bindings, so reverse the identifier mappings to update previously unbound identifiers
		assertion.bindingList.stream()
			.filter(b -> b.which == Binding.IDENTIFIER)
			.forEach(b -> executeBinding(context, new Binding(b.rightIdentifier, b.identifier)));
	}

	public void executeDefinition(@NonNull final Context context, @NonNull final Definition definition)
	{
		final Context child = context.fork();
		executeDefinitionDirectly(child, definition);
		child.transferLocal(context, definition.identifierList);
	}

	public void executeBuiltInDefinition(@NonNull final Context context, @NonNull final BuiltInDefinition definition)
	{
		final Context child = context.fork();
		definition.executeDirectly(child);
		child.transferLocal(context, definition.identifierList);
	}

	public void executeDefinitionDirectly(@NonNull final Context context, @NonNull final Definition definition)
	{
		definition.statementList.forEach(statement -> executeStatement(context, statement));
	}

	public void log(@NonNull final String format, @NonNull Object... args)
	{
		if (log)
			System.out.printf((format) + "%n", args);
	}
}
