package lang2.interpreter;

import lang2.ast.*;
import lang2.interpreter.builtins.*;

import java.util.List;
import java.util.Map;

public class Ast2Main
{
	public static void main(final String[] args)
	{
		/*
		subtract = (minuend, subtrahend, difference)
		{
			tmp = minuend
			tmp2 = tmp
			add(augend = subtrahend, addend = difference, sum = tmp);
		}

		(difference)
		{
			subtract(minuend=3, subtrahend=1);
			// add(...)
			// minuend = sum
			print(message="DIFFERENCE");
			print(message=difference);
		}
		*/

		final Definition rootDefinition = new Definition(List.of(), List.of(
			new Statement(new Binding("subtract", new Definition(List.of("minuend", "subtrahend", "difference"), List.of(
				new Statement(new Binding("tmp", "minuend")),
				new Statement(new Binding("tmp2", "tmp")),
				new Statement(new Assertion("add", List.of(
					new Binding("augend", "subtrahend"),
					new Binding("addend", "difference"),
					//new Binding("sum", "minuend")
					new Binding("sum", "tmp2")
				)))
			)))),

			new Statement(new Assertion("print", List.of())),
			new Statement(new Definition(List.of("difference"), List.of(

				new Statement(new Assertion("subtract", List.of(
					new Binding("minuend", new Literal("3.14")),
					new Binding("subtrahend", new Literal("1.333333333333333333333333334"))
				))),

				new Statement(new Definition(List.of("message"), List.of(
					new Statement(new Definition(List.of("tmp"), List.of(
						new Statement(new Definition(List.of("tmp"), List.of(
							new Statement(new Assertion("concat", List.of(
								new Binding("base", "minuend"),
								new Binding("join", new Literal(" - "))
							))),
							new Statement(new Binding("tmp", "concatenation"))
						))),
						new Statement(new Assertion("concat", List.of(
							new Binding("base", "tmp"),
							new Binding("join", "subtrahend")
						))),
						new Statement(new Binding("tmp", "concatenation"))
					))),
					new Statement(new Assertion("concat", List.of(
						new Binding("base", new Literal("SUBTRACTED: ")),
						new Binding("join", "tmp")
					))),
					new Statement(new Binding("message", "concatenation"))
				))),
				new Statement(new Assertion("print", List.of()))
			))),

			new Statement(new Definition(List.of("message"), List.of(
				new Statement(new Assertion("concat", List.of(
					new Binding("base", new Literal("DIFFERENCE: ")),
					new Binding("join", "difference")
				))),
				new Statement(new Binding("message", "concatenation"))
			))),
			new Statement(new Assertion("print", List.of()))
		));

		final Context rootContext = builtInDefinitionContext.fork();

		// Execute the root definition
		interpreter.log = false;
		interpreter.executeDefinitionDirectly(rootContext, rootDefinition);

		// remove the builtInDefinitions for a cleaner output
		final Context finalContext = new SimpleContext();
		rootContext.transferLocal(finalContext);
		System.out.format("FINAL CONTEXT: %s%n", finalContext);
	}
	private static Interpreter interpreter = new Interpreter();

	private static final Map<String, BuiltInDefinition> builtInDefinitions = Map.of(
		"print", new PrintBuiltIn(),
		"dumpcontext", new DumpContextBuiltIn(),
		"concat", new ConcatBuiltIn(),
		"add", new AddBuiltIn()

	);
	private static final Context builtInDefinitionContext = new SimpleContext();
	static
	{
		builtInDefinitions.forEach((k, id) -> builtInDefinitionContext.put(k, new ContextValue(id)));
	}
}
