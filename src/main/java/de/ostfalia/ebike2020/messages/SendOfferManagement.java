package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.HashMap;

public class SendOfferManagement {

    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("CUSTOMER_NAME", execution.getVariable("CUSTOMER_NAME"));
        hashMap.put("CONFIG_ID", execution.getVariable("CONFIG_ID"));
        hashMap.put("TOTAL_COSTS", execution.getVariable("totalCosts"));
        hashMap.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Prüfe Angebot", hashMap);
    }
}
