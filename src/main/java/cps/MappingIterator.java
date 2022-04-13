package cps;

import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.function.Function;

@AllArgsConstructor
public class MappingIterator<T, R> implements Iterator<R>
{
	@Override
	public void remove()
	{
		source.remove();
	}

	@Override
	public boolean hasNext()
	{
		return source.hasNext();
	}

	@Override
	public R next()
	{
		return mapper.apply(source.next());
	}

	private final Function<T, R> mapper;
	private final Iterator<T> source;
}
