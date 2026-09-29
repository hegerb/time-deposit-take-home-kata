package org.ikigaidigital;

import org.ikigaidigital.domain.interest.InterestPolicies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TimeDepositCalculator {

    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int MONEY_SCALE = 2;

    private final InterestPolicies policies;

    public TimeDepositCalculator() {
        this(InterestPolicies.standard());
    }

    public TimeDepositCalculator(InterestPolicies policies) {
        this.policies = policies;
    }

    public void updateBalance(List<TimeDeposit> timeDeposits) {
        for (TimeDeposit timeDeposit : timeDeposits) {
            addMonthlyInterest(timeDeposit);
        }
    }

    private void addMonthlyInterest(TimeDeposit timeDeposit) {
        double interest = roundToCents(monthlyInterestOf(timeDeposit));
        timeDeposit.setBalance(timeDeposit.getBalance() + interest);
    }

    private double monthlyInterestOf(TimeDeposit timeDeposit) {
        if (isWithinGracePeriod(timeDeposit)) {
            return 0;
        }
        return policies.monthlyInterestFor(timeDeposit);
    }

    private static boolean isWithinGracePeriod(TimeDeposit timeDeposit) {
        return timeDeposit.getDays() <= GRACE_PERIOD_DAYS;
    }

    // new BigDecimal(double), not valueOf: the exact binary value of the double decides
    // half-cent rounding (docs/DESIGN.md, section 4)
    private static double roundToCents(double amount) {
        return new BigDecimal(amount).setScale(MONEY_SCALE, RoundingMode.HALF_UP).doubleValue();
    }
}
