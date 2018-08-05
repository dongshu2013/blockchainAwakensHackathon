package com.walletalarm.platform.cache;

import com.walletalarm.platform.core.Contract;
import com.walletalarm.platform.db.dao.ContractDAO;
import com.walletalarm.platform.handlers.EthUtils;
import org.web3j.protocol.Web3j;

public class CacheManager {
    private ContractDAO contractDAO;
    private Web3j web3j;

    public CacheManager(ContractDAO contractDAO, Web3j web3j) {
        this.contractDAO = contractDAO;
        this.web3j = web3j;
    }

    public Contract getContractFromDBOrNode(String address) {
        Contract contract = contractDAO.findByAddress(address);
        if (contract == null) { //Contract absent in db
            //Fetch from node
            contract = EthUtils.getContractDetails(web3j, address);
            if (contract != null) { //Save to db
                int contractId = contractDAO.create(contract);
                contract.setContractId(contractId);
            }
        }
        return contract;
    }
}
