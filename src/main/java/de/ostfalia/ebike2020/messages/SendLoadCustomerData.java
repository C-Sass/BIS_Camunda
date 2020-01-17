package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class SendLoadCustomerData implements JavaDelegate {
    public void execute(DelegateExecution execution) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CUSTOMER_ID", execution.getVariable("CUSTOMER_ID"));
        hashMap.put("CUSTOMER_NAME", execution.getVariable("CUSTOMER_NAME"));
        hashMap.put("CUSTOMER_ADDRESS", execution.getVariable("CUSTOMER_ADDRESS"));
        hashMap.put("CUSTOMER_MAIL", execution.getVariable("CUSTOMER_MAIL"));
        hashMap.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Lade Kundendaten", hashMap);
    }
}
