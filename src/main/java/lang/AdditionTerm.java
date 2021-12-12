package lang;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

public class AdditionTerm extends Term
{
	public static final String NAME = "add";
	public AdditionTerm()
	{
		super(NAME, false);
	}

	@Override
	public Term resolve(@NonNull Context context)
	{
		final Term augendTerm = context.accept("augend").map(e -> e.resolve(context)).orElse(null);
		final Term addendTerm = context.accept("addend").map(e -> e.resolve(context)).orElse(null);
		final Term sumTerm = context.accept("sum").map(e -> e.resolve(context)).orElse(null);

		final BigDecimal augend = augendTerm != null ? new BigDecimal(augendTerm.name) : null;
		final BigDecimal addend = addendTerm != null ? new BigDecimal(addendTerm.name) : null;
		final BigDecimal sum = sumTerm != null ? new BigDecimal(sumTerm.name) : null;

		switch ((int) Stream.of(augend, addend, sum).filter(Objects::nonNull).count())
		{
			case 3:
			{
				if (!augend.add(addend).equals(sum))
					throw new RuntimeException(augend + " + " + addend + " != " + sum);
				break;
			}
			case 2:
				if (sum == null)
					context.put("sum", l(augend.add(addend)));
				else if (addend == null)
					context.put("augend", l(sum.subtract(augend)));
				else
					context.put("addend", l(sum.subtract(addend)));
				break;
			default:
			case 1:
				throw new RuntimeException("At least two of augend, addend, and sum must be present. Found augend=" + augend + "; addend=" + addend + "; sum=" + sum);
		}
		return this;
	}
}
