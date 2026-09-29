package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;

final class PremiumInterestPolicy implements InterestPolicy {

    private static final AnnualRate RATE = new AnnualRate(0.05);
    private static final int WAITING_PERIOD_DAYS = 45;

    @Override
    public String planType() {
        return PlanTypes.PREMIUM;
    }

    @Override
    public double monthlyInterest(TimeDeposit timeDeposit) {
        if (hasPassedWaitingPeriod(timeDeposit)) {
            return RATE.monthlyShareOf(timeDeposit.getBalance());
        }
        return 0;
    }

    private static boolean hasPassedWaitingPeriod(TimeDeposit timeDeposit) {
        return timeDeposit.getDays() > WAITING_PERIOD_DAYS;
    }
}
