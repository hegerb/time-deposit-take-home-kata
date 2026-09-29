package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PremiumInterestPolicyTest {

    private final PremiumInterestPolicy policy = new PremiumInterestPolicy();

    @Test
    void should_returnPremiumPlanType_whenAskedForPlanType() {
        assertThat(policy.planType()).isEqualTo(PlanTypes.PREMIUM);
    }

    @Test
    void should_returnZero_whenDepositIsFortyFiveDaysOld() {
        TimeDeposit timeDeposit = new TimeDeposit(1, PlanTypes.PREMIUM, 1200.00, 45);

        assertThat(policy.monthlyInterest(timeDeposit)).isZero();
    }

    @Test
    void should_returnOneTwelfthOfFivePercent_whenDepositIsFortySixDaysOld() {
        TimeDeposit timeDeposit = new TimeDeposit(1, PlanTypes.PREMIUM, 1200.00, 46);

        assertThat(policy.monthlyInterest(timeDeposit)).isCloseTo(5.00, within(1e-9));
    }
}
