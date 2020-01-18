package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class SendOfferManagement implements JavaDelegate {

    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();

        double totalCosts = (double) execution.getVariable("totalCosts");
        String totalCostsEuro = String.format("%1.2f €", totalCosts);

        hashMap.put("CUSTOMER_NAME", execution.getVariable("CUSTOMER_NAME"));
        hashMap.put("CONFIG_ID", execution.getVariable("CONFIG_ID"));
        hashMap.put("TOTAL_COSTS", totalCostsEuro);
        hashMap.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Prüfe Angebot", hashMap);
    }
}
