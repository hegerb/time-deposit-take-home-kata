package org.ikigaidigital;

import org.ikigaidigital.domain.model.PlanTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TimeDepositCalculator {
    public void updateBalance(List<TimeDeposit> xs) {
        for (int i = 0; i < xs.size(); i++) {
            double interest = 0;

            if (xs.get(i).getDays() > 30) {
                if (xs.get(i).getPlanType().equals(PlanTypes.STUDENT)) {
                    if (xs.get(i).getDays() < 366) {
                        interest += xs.get(i).getBalance() * 0.03 / 12;
                    }
                } else if (xs.get(i).getPlanType().equals(PlanTypes.PREMIUM)) {
                    if (xs.get(i).getDays() > 45) {
                        interest += xs.get(i).getBalance() * 0.05 / 12;
                    }
                } else if (xs.get(i).getPlanType().equals(PlanTypes.BASIC)) {
                    interest += xs.get(i).getBalance() * 0.01 / 12;
                }
            }

            double a2d = xs.get(i).getBalance() + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
            xs.get(i).setBalance(a2d);
        }
    }
}
