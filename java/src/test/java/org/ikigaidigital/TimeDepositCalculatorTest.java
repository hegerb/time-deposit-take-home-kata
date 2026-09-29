package org.ikigaidigital;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.ikigaidigital.domain.model.PlanTypes.BASIC;
import static org.ikigaidigital.domain.model.PlanTypes.PREMIUM;
import static org.ikigaidigital.domain.model.PlanTypes.STUDENT;

/**
 * Characterization tests: they pin the behaviour of updateBalance as it was before any
 * refactoring, because the README declares that behaviour correct and requires it unchanged.
 * Expected values are computed by hand from the original algorithm, never from the code.
 */
class TimeDepositCalculatorTest {

    private static final String UNKNOWN_PLAN_TYPE = "gold";

    private final TimeDepositCalculator calculator = new TimeDepositCalculator();

    @Test
    void should_addOneMonthOfBasicInterest_whenBasicPlanIsOlderThanThirtyDays() {
        TimeDeposit deposit = deposit(BASIC, 1234567.00, 45);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1235595.81);
    }

    @Test
    void should_addNoInterest_whenAnyPlanIsExactlyThirtyDaysOld() {
        TimeDeposit basic = deposit(BASIC, 1000.00, 30);
        TimeDeposit student = deposit(STUDENT, 1000.00, 30);
        TimeDeposit premium = deposit(PREMIUM, 1000.00, 30);

        calculator.updateBalance(List.of(basic, student, premium));

        assertThat(basic.getBalance()).isEqualTo(1000.00);
        assertThat(student.getBalance()).isEqualTo(1000.00);
        assertThat(premium.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_addBasicInterest_whenBasicPlanIsThirtyOneDaysOld() {
        TimeDeposit deposit = deposit(BASIC, 1000.00, 31);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1000.83);
    }

    @Test
    void should_addStudentInterest_whenStudentPlanIsThirtyOneDaysOld() {
        TimeDeposit deposit = deposit(STUDENT, 1000.00, 31);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1002.50);
    }

    @Test
    void should_addStudentInterest_whenStudentPlanIsThreeHundredSixtyFiveDaysOld() {
        TimeDeposit deposit = deposit(STUDENT, 1000.00, 365);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1002.50);
    }

    @Test
    void should_addNoInterest_whenStudentPlanIsThreeHundredSixtySixDaysOld() {
        TimeDeposit deposit = deposit(STUDENT, 1000.00, 366);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_addNoInterest_whenPremiumPlanIsFortyFiveDaysOld() {
        TimeDeposit deposit = deposit(PREMIUM, 1000.00, 45);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_addPremiumInterest_whenPremiumPlanIsFortySixDaysOld() {
        TimeDeposit deposit = deposit(PREMIUM, 1000.00, 46);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1004.17);
    }

    @Test
    void should_addNoInterest_whenPlanTypeIsUnknown() {
        TimeDeposit deposit = deposit(UNKNOWN_PLAN_TYPE, 1000.00, 100);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_roundHalfCentDown_whenDoubleProductFallsBelowHalfCent() {
        // 18.00 * 0.01 / 12 is 0.01499999... in double, so HALF_UP gives 0.01, not 0.02
        TimeDeposit basicEighteen = deposit(BASIC, 18.00, 31);
        TimeDeposit basicThirty = deposit(BASIC, 30.00, 31);
        TimeDeposit studentTen = deposit(STUDENT, 10.00, 31);

        calculator.updateBalance(List.of(basicEighteen, basicThirty, studentTen));

        assertThat(basicEighteen.getBalance()).isEqualTo(18.01);
        assertThat(basicThirty.getBalance()).isEqualTo(30.02);
        assertThat(studentTen.getBalance()).isEqualTo(10.02);
    }

    @Test
    void should_roundHalfCentUp_whenDoubleProductLandsAboveHalfCent() {
        // 6.00 * 0.01 / 12 is 0.00500000000000000010 in double, so HALF_UP gives 0.01
        TimeDeposit deposit = deposit(BASIC, 6.00, 31);

        calculator.updateBalance(List.of(deposit));

        assertThat(deposit.getBalance()).isEqualTo(6.01);
    }

    @Test
    void should_updateEveryDepositInPlace_whenListHasSeveralPlans() {
        TimeDeposit basic = deposit(BASIC, 1000.00, 31);
        TimeDeposit student = deposit(STUDENT, 1000.00, 31);
        TimeDeposit premium = deposit(PREMIUM, 1000.00, 46);
        List<TimeDeposit> deposits = List.of(basic, student, premium);

        calculator.updateBalance(deposits);

        assertThat(deposits).containsExactly(basic, student, premium);
        assertThat(basic.getBalance()).isEqualTo(1000.83);
        assertThat(student.getBalance()).isEqualTo(1002.50);
        assertThat(premium.getBalance()).isEqualTo(1004.17);
    }

    @Test
    void should_doNothing_whenListIsEmpty() {
        assertThatCode(() -> calculator.updateBalance(List.of())).doesNotThrowAnyException();
    }

    private static TimeDeposit deposit(String planType, double balance, int days) {
        return new TimeDeposit(1, planType, balance, days);
    }
}
