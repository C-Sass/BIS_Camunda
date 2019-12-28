package de.ostfalia.ebike2020.messages;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class OfferDeclined implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        String currentStatus = "Der Bau Ihres Auftrags ist leider nicht möglich.";
        execution.setVariable("CURRENT_STATUS", currentStatus);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("assemblyPossible", execution.getVariable("assemblyPossible"));
        hashMap.put("CURRENT_STATUS", currentStatus);

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Status: Abgelehnt", hashMap);
    }
}
