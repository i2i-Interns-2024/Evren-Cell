package com.i2i.aom.service;

import com.i2i.aom.dto.CustomerBalance;
import com.i2i.aom.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    /**
     * This method is used to get the remaining customer balance by customer msisdn
     * @param msisdn
     * @return CustomerBalance
     * @throws IOException
     * @throws InterruptedException
     * @throws ProcCallException
     */
    public CustomerBalance getRemainingCustomerBalance(String msisdn) throws IOException, InterruptedException, ProcCallException {
        return balanceRepository.getRemainingCustomerBalanceByMsisdn(msisdn);
    }
}
