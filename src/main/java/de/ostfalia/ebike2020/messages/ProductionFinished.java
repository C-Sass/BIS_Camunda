package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class ProductionFinished implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CURRENT_STATUS", execution.getVariable("CURRENT_STATUS"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.correlateMessage("Status: fertig", key, hashMap);
    }

}
