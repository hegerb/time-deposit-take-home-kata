package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;

final class StudentInterestPolicy implements InterestPolicy {

    private static final AnnualRate RATE = new AnnualRate(0.03);
    private static final int FIRST_YEAR_DAYS = 365;

    @Override
    public String planType() {
        return PlanTypes.STUDENT;
    }

    @Override
    public double monthlyInterest(TimeDeposit timeDeposit) {
        if (isWithinFirstYear(timeDeposit)) {
            return RATE.monthlyShareOf(timeDeposit.getBalance());
        }
        return 0;
    }

    private static boolean isWithinFirstYear(TimeDeposit timeDeposit) {
        return timeDeposit.getDays() <= FIRST_YEAR_DAYS;
    }
}
