package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class StudentInterestPolicyTest {

    private final StudentInterestPolicy policy = new StudentInterestPolicy();

    @Test
    void should_returnStudentPlanType_whenAskedForPlanType() {
        assertThat(policy.planType()).isEqualTo(PlanTypes.STUDENT);
    }

    @Test
    void should_returnOneTwelfthOfThreePercent_whenDepositIsWithinFirstYear() {
        TimeDeposit timeDeposit = new TimeDeposit(1, PlanTypes.STUDENT, 1200.00, 365);

        assertThat(policy.monthlyInterest(timeDeposit)).isCloseTo(3.00, within(1e-9));
    }

    @Test
    void should_returnZero_whenDepositIsOlderThanOneYear() {
        TimeDeposit timeDeposit = new TimeDeposit(1, PlanTypes.STUDENT, 1200.00, 366);

        assertThat(policy.monthlyInterest(timeDeposit)).isZero();
    }
}
