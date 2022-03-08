package lang2.ast;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Definition
{
	@NonNull
	public final List<String> identifierList;
	@NonNull
	public final List<Statement> statementList;
}
