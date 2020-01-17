package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateDatabase implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE `e_bike_2020`.`konfigurationselement` " +
                "SET `Zusatzmontagedauer_sec` = ?, `Zusatzmaterialkosten` = ? " +
                "WHERE (`idKonfiguration` = ?) and (`idKomponente` = ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDouble(1, (Double) execution.getVariable("ADDITIONAL_TIME"));
        preparedStatement.setDouble(2, (Double) execution.getVariable("ADDITIONAL_COST"));
        preparedStatement.setInt(3, (Integer) execution.getVariable("CONFIG_ID"));
        preparedStatement.setInt(4, (Integer) execution.getVariable("COMPONENT_ID"));
        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
    }
}
