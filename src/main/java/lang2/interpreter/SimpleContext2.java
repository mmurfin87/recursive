package lang2.interpreter;

import lang2.ast.Assertion;
import lang2.ast.Binding;
import lang2.ast.Literal;
import lang2.ast.Rule;
import lang2.interpreter.ruleplugins.Add;
import lang2.interpreter.ruleplugins.PluginRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.*;

public class SimpleContext2 implements Context2
{
	public SimpleContext2()
	{
		this.identifiers = new HashMap<>();
		this.assertions = new ArrayList<>();
		this.rules = new ArrayList<>();
	}

	public SimpleContext2(@NonNull final SimpleContext2 copy)
	{
		this();
		this.identifiers.putAll(copy.identifiers);
		this.assertions.addAll(copy.assertions);
		this.rules.addAll(copy.rules);
		this.depth = copy.depth + 1;
		if (depth > 10)
			throw new IllegalStateException(String.format("Depth Too Great: %d", depth));
	}

	@Override
	public Context2 put(@NonNull final Assertion assertion)
	{
		assertions.add(0, assertion);
		//final SimpleContext2 context2 = new SimpleContext2(this);
		//context2.executeAssertion(assertion);
		//return context2;
		executeAssertion(assertion);
		return this;
	}

	@Override
	public Context2 put(@NonNull final Rule rule)
	{
		log("Putting Rule: %s%n", rule.left);
		rules.add(0, rule);
		rule.left.bindingList.forEach(this::recordBinding);
		return this;
	}

	@Override
	public Context2 put(@NonNull Binding binding)
	{
		recordBinding(binding);
		return this;
	}

	@Override
	public Resolution resolve(@NonNull String identifier)
	{
		log("Beginning Resolution: %s%n", identifier);
		// First, if we have a matching identifier in the identifiers map, then return it
		final Resolution scopedResolution = identifiers.get(identifier);
		if (scopedResolution != null)// && !(scopedResolution instanceof UnboundResolution))
		{
			log("Resolving %s = %s%n", identifier, scopedResolution.literal().orElse("NOT LITERAL"));
			return scopedResolution;
		}

		// Otherwise we need to search the history and rule combinations
		// We find any constraints on the value of difference - these come from assertions which mention the queried identifier as a left-side binding
		//		Since we did not return above, the identifier is unbound so nothing will assign it a value

		final List<Rule> ruleSearchSpace = rules.stream()
			.filter(r ->
			{
				boolean match = r.left.bindingList.stream().map(b -> b.identifier).anyMatch(i ->
				{
					boolean imatch = identifier.equals(i);
					log("Comparing Identifiers: %s = %s%n", identifier, i);
					return imatch;
				});
				log("\tRule %s: %s%n", (match ? "INCLUDED" : "EXCLUDED"), r.left);
				return match;
			})
			.toList();
		log("Resolved %d rules: %s%n", ruleSearchSpace.size(), ruleSearchSpace);
		if (!ruleSearchSpace.isEmpty())
		{
			final Resolution result = assertions.stream()
				.filter(a -> ruleSearchSpace.stream()
						.filter(r -> Objects.equals(a.identifier, r.left.identifier))
						.anyMatch(r -> compatibleAssertions(a, r.left)))
				.findFirst()
				.flatMap(a -> ruleSearchSpace.stream()
						.map(r ->
						{
							final SimpleContext2 context2 = new SimpleContext2(this);
							context2.rules.remove(r);
							r.right.forEach(context2::put);
							return context2.resolve(identifier);
						})
						.map(Resolution::literal)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.findFirst())
				.map(literal -> (Resolution)new LiteralResolution(new Literal(literal)))
				.orElse(new UnboundResolution());

			return result;
		}
		else
		{
			final List<PluginRule> pluginRuleSearchSpace = builtinRules.stream()
				.filter(r ->
				{
					boolean match = r.declareScope().contains(identifier);
					log("\tRule %s: %s%n", (match ? "INCLUDED" : "EXCLUDED"), r.getClass().getName());
					return match;
				})
				.toList();
			log("Resolved %d Plugin Rules: %s%n", ruleSearchSpace.size(), ruleSearchSpace);
			final Resolution result = assertions.stream()
				.flatMap(a -> pluginRuleSearchSpace.stream()
					.map(r ->
					{
						final SimpleContext2 context2 = new SimpleContext2(this);
						r.executeDirectly(context2);
						return context2.resolve(identifier);
					})
					.map(Resolution::literal)
					.filter(Optional::isPresent)
					.map(Optional::get))
				.map(literal -> (Resolution)new LiteralResolution(new Literal(literal)))
				.findFirst()
				.orElseGet(UnboundResolution::new);
			return result;
		}
		//log("Resolving %s = %s%n", identifier, identifiers.getOrDefault(identifier, UNKNOWN).literal().orElse("NOT LITERAL"));
		//return identifiers.getOrDefault(identifier, UNKNOWN);
	}

	private boolean executeAssertion(@NonNull final Assertion assertion)
	{
		log("Executing Assertion: %s%n", assertion);
		depth++;
		assertion.bindingList.forEach(this::recordBinding);
		int rulesApplied = rules.stream()
			.filter(r ->
			{
				log("Evaluating Rule: %s%n", r.left);
				depth++;
				boolean match = compatibleAssertions(assertion, r.left);
				depth--;
				log("Rule %s%n", (match ? "INCLUDED" : "EXCLUDED"));
				return match;
			})
			.mapToInt(r ->
			{
				executeRule(r);
				return 1;
			})
			.sum();
		log("Program Rules Applied: %d%n", rulesApplied);
		depth--;
		if (rulesApplied > 0)
		{
			assertion.bindingList.stream()
				.filter(b -> b.which == Binding.IDENTIFIER)
				.map(b -> new Binding(b.rightIdentifier, b.identifier))
				.forEach(this::recordBinding);
			return true;
		}
		depth++;
		rulesApplied = builtinRules.stream()
			.filter(r ->
			{
				boolean match = Objects.equals(assertion.identifier, r.declareIdentifier());
				log("Plugin Rule %s %s%n", r.getClass().getSimpleName(), (match ? "INCLUDED" : "EXCLUDED"));
				return match;
			})
			.mapToInt(r ->
			{
				executePluginRule(r);
				return 1;
			})
			.sum();
		log("Plugin Rules Applied: %d%n", rulesApplied);
		depth--;
		if (rulesApplied > 0)
		{
			assertion.bindingList.stream()
				.filter(b -> b.which == Binding.IDENTIFIER)
				.map(b -> new Binding(b.rightIdentifier, b.identifier))
				.forEach(this::recordBinding);
		}
		//else
		//	throw new RuntimeException(String.format("Unknown Assertion: %s", assertion.identifier));
		log("Execution Complete%n");
		return rulesApplied > 0;
	}

	private void executeRule(@NonNull final Rule rule)
	{
		log("Executing Rule: %s%n", rule.left);
		depth++;
		rule.right.forEach(this::put);
		depth--;
	}

	private void executePluginRule(@NonNull final PluginRule rule)
	{
		log("Execting Plugin Rule: %s%n", rule.getClass().getSimpleName());
		depth++;
		rule.executeDirectly(this);
		depth--;
	}

	private void recordBinding(@NonNull final Binding binding)
	{
		switch (binding.which)
		{
			default:
			case Binding.UNBOUND:
				log("Recording UNBOUND: %s%n", binding.identifier);
				identifiers.put(binding.identifier, new UnboundResolution());
				break;
			case Binding.LITERAL:
				log("Recording LITERAL: %s = %s%n", binding.identifier, binding.rightLiteral.value);
				identifiers.put(binding.identifier, new LiteralResolution(binding.rightLiteral));
				break;
			case Binding.IDENTIFIER:
				log("Recording IDENTIFIER: %s = %s%n", binding.identifier, binding.rightIdentifier);
				if (!identifiers.containsKey(binding.rightIdentifier))
					throw new RuntimeException(String.format("Tried to bind to an unknown identifier: %s = %s", binding.identifier, binding.rightIdentifier));
				identifiers.put(binding.identifier, identifiers.get(binding.rightIdentifier));
				break;
			case Binding.DEFINITION:
				throw new UnsupportedOperationException("Deprecated, I think");
		}
	}

	private boolean compatibleAssertions(@NonNull final Assertion a, @NonNull final Assertion b)
	{
		log("Comparing Assertions: %s = %s%n", a, b);
		depth++;
		boolean result = false;
		if (!Objects.equals(a.identifier, b.identifier))
			result = false;
		else
		// None of a's bound bindings conflict with any of b's bound bindings
		// None of b's bound bindings conflict with any of a's bound bindings
			result = a.bindingList.stream().allMatch(aBinding -> b.bindingList.stream()
				.filter(bBinding -> Objects.equals(aBinding.identifier, bBinding.identifier))
				.allMatch(bBinding -> compatibleBinding(aBinding, bBinding)));
		depth--;
		log("Assertion %s%n", (result ? "INCLUDED" : "EXCLUDED"));
		return result;
	}

	private boolean compatibleBinding(@NonNull final Binding a, @NonNull final Binding b)
	{
		log("\tComparing Bindings: %s = %s%n", a, b);
		boolean result = false;
		if (!Objects.equals(a.identifier, b.identifier))
			result = false;
		else if (a.which == Binding.UNBOUND || b.which == Binding.UNBOUND)
			result = true;
		else if (a.which != b.which)
			result = false;
		else
			result = switch (a.which)
			{
				default -> false;
				case Binding.UNBOUND -> true;
				case Binding.LITERAL -> Objects.equals(a.rightLiteral, b.rightLiteral);
				case Binding.IDENTIFIER -> Objects.equals(a.rightIdentifier, b.rightIdentifier);
				case Binding.DEFINITION -> throw new UnsupportedOperationException("TO BE REMOVED");
			};

		log("\tBinding %s%n", (result ? "INCLUDED" : "EXCLUDED"));

		return result;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(SimpleContext2.class.getName()).append(System.lineSeparator());
		identifiers.forEach((k, v) -> sb.append('\t').append(String.format("%15s: %s%n", k, v.literal().orElse(v.getClass().getName()))));
		return sb.toString();
	}

	private void log(@NonNull final String format, @NonNull Object... args)
	{
		for (int i = 0; i < depth; i++)
			System.out.print('\t');
		System.out.format(format, args);
	}

	private final Map<String, Resolution> identifiers;
	private final List<Assertion> assertions;
	private final List<Rule> rules;
	private int depth = 0;

	private final static List<PluginRule> builtinRules = List.of(
		new Add()
	);
	private final static Resolution UNKNOWN = new UnboundResolution();

}
