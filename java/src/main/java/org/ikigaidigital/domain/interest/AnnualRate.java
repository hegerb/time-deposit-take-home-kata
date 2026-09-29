package org.ikigaidigital.domain.interest;

/** Yearly interest rate as a fraction, for example 0.03 for three percent. */
record AnnualRate(double value) {

    private static final int MONTHS_PER_YEAR = 12;

    // Kept as balance * rate / 12 in double: the operation order decides half-cent rounding
    // and the README fixes the current results (docs/DESIGN.md, section 4)
    double monthlyShareOf(double balance) {
        return balance * value / MONTHS_PER_YEAR;
    }
}
