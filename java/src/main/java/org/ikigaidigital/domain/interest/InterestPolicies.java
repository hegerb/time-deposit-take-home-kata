package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** The interest policies in force, one per plan type. */
public final class InterestPolicies {

    private final Map<String, InterestPolicy> byPlanType;

    /** Two policies for one plan type is a wiring error, so it fails here, not at the first calculation. */
    public InterestPolicies(List<InterestPolicy> policies) {
        this.byPlanType = policies.stream()
                .collect(Collectors.toUnmodifiableMap(InterestPolicy::planType, Function.identity()));
    }

    /** The plans the README defines. A new plan type is one policy class and one line here. */
    public static InterestPolicies standard() {
        return new InterestPolicies(List.of(
                new BasicInterestPolicy(),
                new StudentInterestPolicy(),
                new PremiumInterestPolicy()));
    }

    /** Zero for a plan type without a policy, as the original calculation did. */
    public double monthlyInterestFor(TimeDeposit timeDeposit) {
        InterestPolicy policy = byPlanType.get(timeDeposit.getPlanType());
        if (policy == null) {
            return 0;
        }
        return policy.monthlyInterest(timeDeposit);
    }
}
