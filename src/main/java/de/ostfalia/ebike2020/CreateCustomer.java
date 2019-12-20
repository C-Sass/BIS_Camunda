package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class CreateCustomer implements JavaDelegate {
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        CallableStatement max = connection.prepareCall("SELECT MAX(idKunde) FROM kunde");
        ResultSet resultSet = max.executeQuery();
        int id = !resultSet.next() ? 0 : resultSet.getInt(1) + 1;
        resultSet.close();

        execution.setVariable("CUSTOMER_ID", id);

        String sql = "INSERT INTO `e_bike_2020`.`kunde` (`idKunde`, `Name`, `Adresse`, `E-Mail`) VALUES (?, ?, ?, ?);";

        CallableStatement callableStatement = connection.prepareCall(sql);
        callableStatement.setInt(1, id);
        callableStatement.setString(2, execution.getVariable("CUSTOMER_NAME").toString());
        callableStatement.setString(3, execution.getVariable("CUSTOMER_ADDRESS").toString());
        callableStatement.setString(4, execution.getVariable("CUSTOMER_MAIL").toString());
        callableStatement.executeUpdate();

        callableStatement.close();
        connection.close();
    }
}
