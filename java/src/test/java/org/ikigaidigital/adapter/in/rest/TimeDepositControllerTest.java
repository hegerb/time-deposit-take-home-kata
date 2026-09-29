package org.ikigaidigital.adapter.in.rest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.ikigaidigital.domain.model.PlanTypes;
import org.ikigaidigital.domain.model.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.model.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimeDepositController.class)
class TimeDepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllTimeDepositsUseCase getAllTimeDeposits;

    @MockitoBean
    private UpdateAllBalancesUseCase updateAllBalances;

    @Test
    void should_renderReadmeSchemaWithTwoDecimalMoney_whenGettingAllTimeDeposits() throws Exception {
        when(getAllTimeDeposits.getAllTimeDeposits()).thenReturn(List.of(premiumDepositWithOneWithdrawal()));

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 2, "planType": "premium", "balance": 2008.33, "days": 46,
                          "withdrawals": [{"id": 10, "amount": 25.00, "date": "2024-05-01"}]}]
                        """, true));
    }

    @Test
    void should_invokeTheUpdateUseCaseAndRenderItsResult_whenUpdatingAllBalances() throws Exception {
        when(updateAllBalances.updateAllBalances()).thenReturn(List.of(premiumDepositWithOneWithdrawal()));

        mockMvc.perform(post("/time-deposits/update-balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].balance").value(2008.33));
        verify(updateAllBalances).updateAllBalances();
    }

    private static TimeDepositWithWithdrawals premiumDepositWithOneWithdrawal() {
        return new TimeDepositWithWithdrawals(
                new TimeDeposit(2, PlanTypes.PREMIUM, 2008.33, 46),
                List.of(new Withdrawal(10, new BigDecimal("25.00"), LocalDate.of(2024, 5, 1))));
    }
}
