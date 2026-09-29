package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class BasicInterestPolicyTest {

    private final BasicInterestPolicy policy = new BasicInterestPolicy();

    @Test
    void should_returnBasicPlanType_whenAskedForPlanType() {
        assertThat(policy.planType()).isEqualTo(PlanTypes.BASIC);
    }

    @Test
    void should_returnOneTwelfthOfOnePercent_whenDepositHasAnyAge() {
        TimeDeposit timeDeposit = new TimeDeposit(1, PlanTypes.BASIC, 1200.00, 100);

        assertThat(policy.monthlyInterest(timeDeposit)).isCloseTo(1.00, within(1e-9));
    }
}
