package org.ikigaidigital.application.port.in;

import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;

import java.util.List;

public interface UpdateAllBalancesUseCase {

    /** Credits one month of interest to every time deposit and returns them with the new balances. */
    List<TimeDepositWithWithdrawals> updateAllBalances();
}
