import java.math.BigDecimal;

public final class MoneyValidator {

    private MoneyValidator() {
        // Prevent object creation.
    }

    // Checks whether a monetary amount is greater than zero.
    public static boolean isPositiveAmount(BigDecimal amount) {

        return amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    // Checks whether a balance is zero or greater.
    public static boolean isNonNegativeBalance(BigDecimal balance) {

        return balance != null
                && balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    // Checks whether an amount is within the specified range.
    public static boolean isWithinRange(
            BigDecimal amount,
            BigDecimal minimum,
            BigDecimal maximum) {

        if (!isPositiveAmount(amount)
                || minimum == null
                || maximum == null
                || minimum.compareTo(maximum) > 0) {

            return false;
        }

        return amount.compareTo(minimum) >= 0
                && amount.compareTo(maximum) <= 0;
    }

    // Checks whether an amount is a multiple of the denomination.
    public static boolean isMultipleOf(
            BigDecimal amount,
            BigDecimal denomination) {

        if (!isPositiveAmount(amount)
                || denomination == null
                || denomination.compareTo(BigDecimal.ZERO) <= 0) {

            return false;
        }

        return amount.remainder(denomination)
                .compareTo(BigDecimal.ZERO) == 0;
    }
}
