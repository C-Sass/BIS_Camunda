package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class OfferDeclined implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("assemblyPossible", execution.getVariable("assemblyPossible"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Status: Abgelehnt", hashMap);
    }
}
