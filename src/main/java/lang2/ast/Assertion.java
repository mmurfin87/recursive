package lang2.ast;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Assertion
{
	@NonNull
	public final String identifier;
	@NonNull
	public final List<Binding> bindingList;
}
