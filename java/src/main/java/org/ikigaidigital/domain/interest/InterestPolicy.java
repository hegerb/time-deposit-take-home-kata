package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

/**
 * Interest rule of one plan type. The calculator applies the common 30-day grace period for
 * every plan, so monthlyInterest is only asked for deposits older than 30 days.
 */
public interface InterestPolicy {

    String planType();

    /** Unrounded interest for one month, or zero when the plan's own conditions are not met. */
    double monthlyInterest(TimeDeposit timeDeposit);
}
