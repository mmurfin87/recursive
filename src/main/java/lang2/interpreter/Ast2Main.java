package lang2.interpreter;

import lang2.ast.*;
import lang2.interpreter.builtins.*;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class Ast2Main
{
	public static void main(final String[] args)
	{
		/*
			OPEN QUESTIONS:

			How to handle nested bindings? For instance, to concat the pattern:
			concat(base = "SUBTRACTED: ", join = concatenation:concat(base = minuend, join = concatenation:concat(base = " - ", join = subtrahend)))

			Random idea: print (and other system/context assertions) needs a monad for state - like the state of the current linebuffer/screen.
			The console can then essentially just always be resolving that state monad, as an outside influence querying the program, to avoid
			weird side effects. Resolution of variables itself is a side effect.

		 	Either construct
		 	Not really if/else, but this language's equivalent.
		 	either(a, b, whichever)
			Both/Neither are sort of implicit in the language's serializability. This construct is needed to determine

			Thinking about the above and where I want this language to go let me to looking at prolog and realizing I want prolog rules.

			//add(augend=1, addend=2, sum=3) =
			//{
			//	print(message="Hello");
			//	add(augend=1, addend=2, sum=3);
			//}

			subtract(minuend = ?, subtrahend = ?, difference = ?) =
			{
				add(augend = subtrahend, addend = difference, sum = minuend);
			}

			subtract(minuend=3, subtrahend=2);

			print(message = difference) // resolve difference, see note above about this being a bad side effect
		 */
		/*

		// This is actually a binding in the new paradim, equivalent to the prolog-esqe "declaration" and rule: subtract(minuend, subtrahend, difference) {}
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

		final Rule prototypeRule = new Rule(
			assertion("subtract", unbound("minuend"), unbound("subtrahend"), unbound("difference")),
			List.of(
				assertion("add", bindId("augend", "subtrahend"), bindId("addend", "difference"), bindId("sum", "minuend"))
			)
		);

		final Assertion prototypeSubtract = assertion("subtract", bindLiteral("minuend", "3"), bindLiteral("subtrahend", "2"));

		final Assertion prototypePrint = assertion("print", bindId("message", "difference"));

		Context2 context2 = new SimpleContext2();

		try
		{
			context2 = context2.put(prototypeRule);
			context2 = context2.put(prototypeSubtract);
			context2 = context2.put(prototypePrint);

			final Resolution resolution = context2.resolve("message");
			System.out.println("----- Program Completed -----");
			System.out.format("message = %s%n", resolution.literal().orElse("NOT LITERAL"));
		}
		catch (final Exception e)
		{
			System.out.println("----- DUMPING CONTEXT -----");
			System.out.println(context2);
			System.out.println("---------------------------");
			throw e;
		}
		if (true)
			return;

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

	private static Binding unbound(@NonNull final String key)
	{
		return new Binding(key);
	}

	private static Binding bindId(@NonNull final String key, @NonNull final String identifier)
	{
		return new Binding(key, identifier);
	}

	private static Binding bindLiteral(@NonNull final String key, @NonNull final String literal)
	{
		return new Binding(key, new Literal(literal));
	}

	private static Assertion assertion(@NonNull final String identifier, @NonNull final Binding... bindings)
	{
		return new Assertion(identifier, List.of(bindings));
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
