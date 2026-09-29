package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.PlanTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class InterestPoliciesTest {

    private static final String UNREGISTERED_PLAN_TYPE = "gold";
    private static final int PAST_EVERY_WAITING_PERIOD = 100;

    @Test
    void should_applyEachPlansPolicy_whenBuiltAsStandard() {
        InterestPolicies policies = InterestPolicies.standard();

        assertThat(policies.monthlyInterestFor(timeDeposit(PlanTypes.BASIC))).isCloseTo(1.00, within(1e-9));
        assertThat(policies.monthlyInterestFor(timeDeposit(PlanTypes.STUDENT))).isCloseTo(3.00, within(1e-9));
        assertThat(policies.monthlyInterestFor(timeDeposit(PlanTypes.PREMIUM))).isCloseTo(5.00, within(1e-9));
    }

    @Test
    void should_returnZero_whenPlanTypeHasNoPolicy() {
        InterestPolicies policies = InterestPolicies.standard();

        assertThat(policies.monthlyInterestFor(timeDeposit(UNREGISTERED_PLAN_TYPE))).isZero();
    }

    @Test
    void should_returnZero_whenBuiltWithoutPolicies() {
        InterestPolicies policies = new InterestPolicies(List.of());

        assertThat(policies.monthlyInterestFor(timeDeposit(PlanTypes.BASIC))).isZero();
    }

    @Test
    void should_rejectSecondPolicy_whenPlanTypeIsAlreadyRegistered() {
        List<InterestPolicy> twoBasicPolicies = List.of(new BasicInterestPolicy(), new BasicInterestPolicy());

        assertThatThrownBy(() -> new InterestPolicies(twoBasicPolicies)).isInstanceOf(IllegalStateException.class);
    }

    private static TimeDeposit timeDeposit(String planType) {
        return new TimeDeposit(1, planType, 1200.00, PAST_EVERY_WAITING_PERIOD);
    }
}
