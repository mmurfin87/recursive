package lang2.interpreter.builtins;

import lang2.interpreter.Context;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

import static lang2.interpreter.builtins.ContextUtils.expectLiteralOrNothing;

public class PrintBuiltIn extends BuiltInDefinition
{
	public PrintBuiltIn()
	{
		super(List.of("message"));
	}

	@Override
	public void executeDirectly(@NonNull Context context)
	{
		final Optional<String> message = expectLiteralOrNothing("message", context);
		System.out.println(message.orElse(""));
	}
}
