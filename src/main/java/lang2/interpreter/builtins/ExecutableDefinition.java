package lang2.interpreter.builtins;

import lang2.ast.Definition;
import lang2.ast.Statement;
import lang2.interpreter.Context;
import lombok.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class ExecutableDefinition extends BuiltInDefinition
{
	public ExecutableDefinition(@NonNull final Definition definition, @NonNull final Consumer<Statement> statementExecutor)
	{
		super(definition.identifierList);
		this.statementList = definition.statementList;
		this.statementExecutor = statementExecutor;
	}

	@Override
	protected void executeInternal(@NonNull Context context)
	{
		statementList.forEach(statementExecutor);
	}

	@NonNull
	private final List<Statement> statementList;
	@NonNull
	private final Consumer<Statement> statementExecutor;
}
