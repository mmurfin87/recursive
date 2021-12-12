package lang;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Term
{
	public static Term l(@NonNull final Object name)
	{
		return new Term(name.toString(), true);
	}

	public static Term t(@NonNull final String name)
	{
		return new Term(name, false);
	}

	@NonNull
	public final String name;
	public final boolean isLiteral;

	public Term resolve(@NonNull final Context context)
	{
		if (isLiteral)
			return this;
		throw new RuntimeException(this + ": Unable to resolve");
	}
}
