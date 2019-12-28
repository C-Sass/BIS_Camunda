package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LoadConfiguration implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        Map<Integer, String> components = new HashMap<>();

        String getComp = "SELECT * FROM komponente";

        PreparedStatement preparedStatement = connection.prepareStatement(getComp);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            components.put(resultSet.getInt("idKomponente"), resultSet.getString("Name"));
        }
        execution.setVariable("AVAILABLE_COMPONENTS", Variables.objectValue(components)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
