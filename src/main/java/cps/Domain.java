package cps;

import java.util.Iterator;
import java.util.stream.Stream;

public interface Domain extends Iterable<String>
{
	static Domain of(final String... domain)
	{
		return () -> Stream.of(domain);
	}

	Stream<String> enumerate();

	@Override
	default Iterator<String> iterator()
	{
		return enumerate().iterator();
	}
}
