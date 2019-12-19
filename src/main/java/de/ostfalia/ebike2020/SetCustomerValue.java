package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SetCustomerValue implements JavaDelegate {
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM kunde WHERE idKunde = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (int) execution.getVariable("CUSTOMER_ID"));

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            execution.setVariable("CUSTOMER_NAME", resultSet.getString("Name"));
            execution.setVariable("CUSTOMER_ADDRESS", resultSet.getString("Adresse"));
            execution.setVariable("CUSTOMER_MAIL", resultSet.getString("E-Mail"));
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
