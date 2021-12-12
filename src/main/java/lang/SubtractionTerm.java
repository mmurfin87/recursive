package lang;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

public class SubtractionTerm extends Term
{
	public static final String NAME = "subtract";

	public SubtractionTerm()
	{
		super(NAME, false);
	}

	@Override
	public Term resolve(@NonNull Context context)
	{
		final Term minuendTerm = context.accept("minuend").map(e -> e.resolve(context)).orElse(null);
		final Term subtrahendTerm = context.accept("subtrahend").map(e -> e.resolve(context)).orElse(null);
		final Term differenceTerm = context.accept("difference").map(e -> e.resolve(context)).orElse(null);

		final BigDecimal minuend = minuendTerm != null ? new BigDecimal(minuendTerm.value) : null;
		final BigDecimal subtrahend = subtrahendTerm != null ? new BigDecimal(subtrahendTerm.value) : null;
		final BigDecimal difference = differenceTerm != null ? new BigDecimal(differenceTerm.value) : null;

		switch ((int) Stream.of(minuend, subtrahend, difference).filter(Objects::nonNull).count())
		{
			case 3:
			{
				if (!minuend.subtract(subtrahend).equals(difference))
					throw new RuntimeException(minuend + " + " + subtrahend + " != " + difference);
				break;
			}
			case 2:
				if (difference == null)
					context.put("difference", l(minuend.subtract(subtrahend)));
				else if (minuend == null)
					context.put("minuend", l(difference.add(subtrahend)));
				else
					context.put("subtrahend", l(minuend.subtract(difference)));
				break;
			default:
			case 1:
				throw new RuntimeException("At least two of augend, addend, and sum must be present. Found minuend=" + minuend + "; subtrahend=" + subtrahend + "; difference=" + difference);
		}
		return this;
	}
}
