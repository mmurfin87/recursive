package cps;

import lombok.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class State
{
	@AllArgsConstructor
	public static class Node
	{
		@NonNull
		public final String key;
		@NonNull
		public final String value;
		@NonNull
		public final Iterator<String> iterator;
	}
	@NonNull
	public final Map<String, Domain> domain;
	public final State parent;
	public final Node node;


	public boolean eq(@NonNull final String key, @NonNull final String expected)
	{
		return (node != null && Objects.equals(this.node.key, key)) ? Objects.equals(node.value, expected) : parent != null && parent.eq(key, expected);
	}

	public Set<String> keys()
	{
		final Set<String> keys = new HashSet<>();
		recursivelyBuildKeySet(keys);
		return keys;
	}

	protected void recursivelyBuildKeySet(@NonNull final Set<String> keys)
	{
		if (node != null)
			keys.add(node.key);
		if (parent != null)
			parent.recursivelyBuildKeySet(keys);
	}

	public String get(@NonNull final String key)
	{
		if (node != null && Objects.equals(this.node.key, key))
			return node.value;
		return parent != null ? parent.get(key) : null;
	}

	public Map<String, String> map()
	{
		return keys().stream().collect(Collectors.toMap(Function.identity(), this::get));
	}

	public State first()
	{
		final Set<String> missingKeys = new HashSet<>(domain.keySet());
		missingKeys.removeAll(keys());
		if (missingKeys.size() < 1)
			return null;
		final String key = missingKeys.iterator().next();
		final Iterator<String> iter = domain.get(key).iterator();
		return new State(domain, this, new Node(key, iter.next(), iter));
	}

	public State next()
	{
		if (node == null || !node.iterator.hasNext())
			return null;
		return new State(domain, this, new Node(node.key, node.iterator.next(), node.iterator));
	}
}
