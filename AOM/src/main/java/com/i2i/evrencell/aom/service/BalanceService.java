package com.i2i.evrencell.aom.service;

import com.i2i.evrencell.voltdb.VoltCustomerBalance;
import com.i2i.evrencell.voltdb.VoltdbOperator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;

@Service
public class BalanceService {
    private final VoltdbOperator voltdbOperator = new VoltdbOperator();
    private final static Logger logger = LogManager.getLogger(BalanceService.class);

    /**
     * This method is used to get the remaining customer balance by customer msisdn
     * @param msisdn
     * @return CustomerBalance
     * @throws IOException
     * @throws InterruptedException
     * @throws ProcCallException
     */
    public VoltCustomerBalance getRemainingCustomerBalance(String msisdn) throws IOException, InterruptedException, ProcCallException {
        logger.debug("Getting remaining customer balance for MSISDN: " + msisdn);
        return voltdbOperator.getRemainingCustomerBalanceByMsisdn(msisdn);
    }
}
