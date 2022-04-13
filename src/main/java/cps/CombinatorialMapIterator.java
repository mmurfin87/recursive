package cps;

import lombok.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CombinatorialMapIterator<K, V> implements Iterator<Map<K, V>>
{
	public CombinatorialMapIterator(@NonNull final Map<K, ? extends Iterable<V>> map)
	{
		this.source = map;
		this.keys = new ArrayList<>(map.keySet());
		this.iters = keys.stream().collect(Collectors.toMap(Function.identity(), k -> map.get(k).iterator()));
		this.values = null;

		if (iters.values().stream().anyMatch(Predicate.not(Iterator::hasNext)))
			throw new IllegalStateException("All iterables must have at least one value");
	}

	@Override
	public boolean hasNext()
	{
		return keys.stream()
			.map(iters::get)
			.anyMatch(Iterator::hasNext);
	}

	@Override
	public Map<K, V> next()
	{
		if (!hasNext())
			throw new NoSuchElementException();

		if (values == null)
			values = keys.stream().collect(Collectors.toMap(Function.identity(), k -> iters.get(k).next()));
		else
		{
			int i = iters.size() - 1;
			K k = keys.get(i);

			// Reset spent iterators, but never the zeroeth one
			for (; i > 0 && !iters.get(keys.get(i)).hasNext(); i--, k = keys.get(i)) {
				iters.put(k, source.get(k).iterator());
				values.put(k, iters.get(k).next());
			}

			values.put(k, iters.get(k).next());
		}

		System.out.format("Generated Candidate: %s%n", values);

		return new HashMap<>(values);
	}

	private final Map<K, ? extends Iterable<V>> source;
	private final List<K> keys;
	private final Map<K, Iterator<V>> iters;
	private Map<K, V> values;
}
