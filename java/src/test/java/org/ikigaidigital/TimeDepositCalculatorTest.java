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
        TimeDeposit timeDeposit = timeDeposit(BASIC, 1234567.00, 45);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1235595.81);
    }

    @Test
    void should_addNoInterest_whenAnyPlanIsExactlyThirtyDaysOld() {
        TimeDeposit basic = timeDeposit(BASIC, 1000.00, 30);
        TimeDeposit student = timeDeposit(STUDENT, 1000.00, 30);
        TimeDeposit premium = timeDeposit(PREMIUM, 1000.00, 30);

        calculator.updateBalance(List.of(basic, student, premium));

        assertThat(basic.getBalance()).isEqualTo(1000.00);
        assertThat(student.getBalance()).isEqualTo(1000.00);
        assertThat(premium.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_addBasicInterest_whenBasicPlanIsThirtyOneDaysOld() {
        TimeDeposit timeDeposit = timeDeposit(BASIC, 1000.00, 31);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1000.83);
    }

    @Test
    void should_addStudentInterest_whenStudentPlanIsThirtyOneDaysOld() {
        TimeDeposit timeDeposit = timeDeposit(STUDENT, 1000.00, 31);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1002.50);
    }

    @Test
    void should_addStudentInterest_whenStudentPlanIsThreeHundredSixtyFiveDaysOld() {
        TimeDeposit timeDeposit = timeDeposit(STUDENT, 1000.00, 365);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1002.50);
    }

    @Test
    void should_addNoInterest_whenStudentPlanIsThreeHundredSixtySixDaysOld() {
        TimeDeposit timeDeposit = timeDeposit(STUDENT, 1000.00, 366);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_addNoInterest_whenPremiumPlanIsFortyFiveDaysOld() {
        TimeDeposit timeDeposit = timeDeposit(PREMIUM, 1000.00, 45);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_addPremiumInterest_whenPremiumPlanIsFortySixDaysOld() {
        TimeDeposit timeDeposit = timeDeposit(PREMIUM, 1000.00, 46);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1004.17);
    }

    @Test
    void should_addNoInterest_whenPlanTypeIsUnknown() {
        TimeDeposit timeDeposit = timeDeposit(UNKNOWN_PLAN_TYPE, 1000.00, 100);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(1000.00);
    }

    @Test
    void should_roundHalfCentDown_whenDoubleProductFallsBelowHalfCent() {
        // 18.00 * 0.01 / 12 is 0.01499999... in double, so HALF_UP gives 0.01, not 0.02
        TimeDeposit basicEighteen = timeDeposit(BASIC, 18.00, 31);
        TimeDeposit basicThirty = timeDeposit(BASIC, 30.00, 31);
        TimeDeposit studentTen = timeDeposit(STUDENT, 10.00, 31);

        calculator.updateBalance(List.of(basicEighteen, basicThirty, studentTen));

        assertThat(basicEighteen.getBalance()).isEqualTo(18.01);
        assertThat(basicThirty.getBalance()).isEqualTo(30.02);
        assertThat(studentTen.getBalance()).isEqualTo(10.02);
    }

    @Test
    void should_roundHalfCentUp_whenDoubleProductLandsAboveHalfCent() {
        // 6.00 * 0.01 / 12 is 0.00500000000000000010 in double, so HALF_UP gives 0.01
        TimeDeposit timeDeposit = timeDeposit(BASIC, 6.00, 31);

        calculator.updateBalance(List.of(timeDeposit));

        assertThat(timeDeposit.getBalance()).isEqualTo(6.01);
    }

    @Test
    void should_updateEveryDepositInPlace_whenListHasSeveralPlans() {
        TimeDeposit basic = timeDeposit(BASIC, 1000.00, 31);
        TimeDeposit student = timeDeposit(STUDENT, 1000.00, 31);
        TimeDeposit premium = timeDeposit(PREMIUM, 1000.00, 46);
        List<TimeDeposit> timeDeposits = List.of(basic, student, premium);

        calculator.updateBalance(timeDeposits);

        assertThat(timeDeposits).containsExactly(basic, student, premium);
        assertThat(basic.getBalance()).isEqualTo(1000.83);
        assertThat(student.getBalance()).isEqualTo(1002.50);
        assertThat(premium.getBalance()).isEqualTo(1004.17);
    }

    @Test
    void should_doNothing_whenListIsEmpty() {
        assertThatCode(() -> calculator.updateBalance(List.of())).doesNotThrowAnyException();
    }

    private static TimeDeposit timeDeposit(String planType, double balance, int days) {
        return new TimeDeposit(1, planType, balance, days);
    }
}
