package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LoadCustomer implements JavaDelegate {
    public void execute(DelegateExecution execution) throws Exception {
        Map<Integer, String> customers = new HashMap<>();
        Connection connection = DatabaseConnection.getConnection();

        String query = "SELECT * FROM kunde";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            customers.put(resultSet.getInt("idKunde"), resultSet.getString("name"));
        }
        customers.put(-1, "Kunde anlegen");

        execution.setVariable("AVAILABLE_CUSTOMERS", Variables.objectValue(customers)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());

        resultSet.close();
        preparedStatement.close();
        connection.close();

        if (execution.getProcessBusinessKey() == null) {
            final String key = BusinessKeyGenerator.getKey(21);
            execution.setVariable("DEMO_BUSINESS_KEY", key);
        } else {
            execution.setVariable("DEMO_BUSINESS_KEY", execution.getProcessBusinessKey());
        }
    }
}
