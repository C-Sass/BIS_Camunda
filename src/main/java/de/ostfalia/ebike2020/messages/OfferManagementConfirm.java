package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class OfferManagementConfirm implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CONFIG_ID", execution.getVariable("CONFIG_ID"));
        hashMap.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));
        hashMap.put("TOTAL_COSTS", execution.getVariable("TOTAL_COSTS"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Angebot ok", hashMap);
    }
}
