package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class SendConfiguration implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("CUSTOMER_ID", execution.getVariable("CUSTOMER_ID"));
        hashMap.put("CUSTOMER_NAME", execution.getVariable("CUSTOMER_NAME"));
        hashMap.put("PRODUCT_ID", execution.getVariable("PRODUCT_ID"));
        hashMap.put("RAHMEN_ID", execution.getVariable("RAHMEN_ID"));
        hashMap.put("RAHMEN_COMP_ID", 1);
        hashMap.put("FARBE_ID", execution.getVariable("FARBE_ID"));
        hashMap.put("FARBE_COMP_ID", 2);
        hashMap.put("AKKU_ID", execution.getVariable("AKKU_ID"));
        hashMap.put("AKKU_COMP_ID", 25);
        hashMap.put("MOTOR_ID", execution.getVariable("MOTOR_ID"));
        hashMap.put("MOTOR_COMP_ID", 40);
        hashMap.put("ADDITIONAL_WISH", execution.getVariable("ADDITIONAL_WISH"));
        hashMap.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Konfiguration erstellen", hashMap);
    }
}
