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
	public static Term l(@NonNull final Object value)
	{
		return new Term(value.toString(), true);
	}

	public static Term t(@NonNull final String value)
	{
		return new Term(value, false);
	}

	@NonNull
	public final String value;
	public final boolean isLiteral;

	public Term resolve(@NonNull final Context context)
	{
		if (isLiteral)
			return this;
		throw new RuntimeException(this + ": Unable to resolve");
	}
}
