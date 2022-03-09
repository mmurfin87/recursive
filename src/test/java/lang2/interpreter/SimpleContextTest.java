package lang2.interpreter;

import lang2.ast.Literal;
import lang2.interpreter.ContextValue;
import lang2.interpreter.SimpleContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleContextTest
{
	@Test
	public void testGetWorksWithLocalAliases()
	{
		final SimpleContext testSubject = new SimpleContext();
		final ContextValue expected = new ContextValue(new Literal("1"));
		testSubject.put("real", expected);
		testSubject.put("alias", new ContextValue("real"));

		final Optional<ContextValue> actualOpt = testSubject.get("alias");

		assertNotNull(actualOpt);
		assertTrue(actualOpt.isPresent());

		final ContextValue actual = actualOpt.get();

		assertEquals(expected, actual);
	}

	@Test
	public void testGetWorksWithParentAliases()
	{
		final SimpleContext parent = new SimpleContext();
		final ContextValue expected = new ContextValue(new Literal("1"));

		parent.put("real", expected);

		final SimpleContext testSubject = new SimpleContext(parent);

		testSubject.put("alias", new ContextValue("real"));

		final Optional<ContextValue> actualOpt = testSubject.get("alias");

		assertNotNull(actualOpt);
		assertTrue(actualOpt.isPresent());

		final ContextValue actual = actualOpt.get();

		assertEquals(expected, actual);
	}
}
