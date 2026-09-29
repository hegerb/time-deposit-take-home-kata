package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;

final class BasicInterestPolicy implements InterestPolicy {

    private static final AnnualRate RATE = new AnnualRate(0.01);

    @Override
    public String planType() {
        return PlanTypes.BASIC;
    }

    @Override
    public double monthlyInterest(TimeDeposit timeDeposit) {
        return RATE.monthlyShareOf(timeDeposit.getBalance());
    }
}
