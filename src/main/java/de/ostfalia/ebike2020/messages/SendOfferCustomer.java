package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class SendOfferCustomer implements JavaDelegate {
    public void execute(DelegateExecution execution) {
        HashMap<String, Object> hashMap = new HashMap<>();
        String totalCostsEuro;

        try {
            double totalCosts = (double) execution.getVariable("totalCosts");
            totalCostsEuro = String.format("%1.2f €", totalCosts);
            hashMap.put("TOTAL_COSTS", totalCostsEuro);
        } catch (Exception e) {
            hashMap.put("TOTAL_COSTS", execution.getVariable("TOTAL_COSTS"));
        }

        hashMap.put("CONFIG_ID", execution.getVariable("CONFIG_ID"));

        String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.correlateMessage("Angebot erhalten", key, hashMap);
    }
}
