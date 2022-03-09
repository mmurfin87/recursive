package lang2.interpreter.builtins;

import lang2.ast.Literal;
import lang2.interpreter.ContextValue;
import lang2.interpreter.SimpleContext;
import org.junit.jupiter.api.Test;

public class DumpContextBuiltInTest
{
	@Test
	public void test()
	{
		final DumpContextBuiltIn testSubject = new DumpContextBuiltIn();

		final SimpleContext parent = new SimpleContext();
		final SimpleContext child = new SimpleContext(parent);

		parent.put("parentKey", new ContextValue(new Literal("1")));
		child.put("childKey", new ContextValue(new Literal("2")));

		testSubject.executeDirectly(child);
	}
}
