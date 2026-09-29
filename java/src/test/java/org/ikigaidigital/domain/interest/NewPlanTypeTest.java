package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Shows that a plan type the README does not know is one new policy, with no change elsewhere. */
class NewPlanTypeTest {

    private static final String GOLD_PLAN_TYPE = "gold";

    @Test
    void should_applyNewPlanType_whenItsPolicyIsRegistered() {
        InterestPolicy goldPolicy = new InterestPolicy() {
            private static final AnnualRate RATE = new AnnualRate(0.12);

            @Override
            public String planType() {
                return GOLD_PLAN_TYPE;
            }

            @Override
            public double monthlyInterest(TimeDeposit timeDeposit) {
                return RATE.monthlyShareOf(timeDeposit.getBalance());
            }
        };
        TimeDepositCalculator calculator = new TimeDepositCalculator(new InterestPolicies(List.of(goldPolicy)));
        TimeDeposit timeDeposit = new TimeDeposit(1, GOLD_PLAN_TYPE, 1000.00, 31);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1010.00);
    }
}
