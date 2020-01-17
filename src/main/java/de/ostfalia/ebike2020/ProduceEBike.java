package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProduceEBike implements JavaDelegate {
    Connection connection = DatabaseConnection.getConnection();

    public ProduceEBike() throws SQLException {
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String sql = "select * " +
                "from variante " +
                "natural join konfigurationselement " +
                "where idKonfiguration = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (Integer) execution.getVariable("CONFIG_ID"));

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int comp = resultSet.getInt("idKomponente");
            int var = resultSet.getInt("idVariante");
            int bookB = resultSet.getInt("Bestand_Bestellt");
            int phyB = resultSet.getInt("Bestand_Physisch");

            bookB--;
            phyB--;

            booking(bookB, phyB, comp, var);
        }
        String currentStatus = "Ihr Auftrag wurde erfolgreich abgeschlossen";
        execution.setVariable("CURRENT_STATUS", currentStatus);

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    public void booking(int bookB, int phyB, int comp, int var) throws SQLException {

        String updateDB = "UPDATE `e_bike_2020`.`variante` " +
                "SET `Bestand_Bestellt` = '?', `Bestand_Physisch` = '?' " +
                "WHERE (`idKomponente` = '?') and (`idVariante` = '?');";

        PreparedStatement preparedStatement = connection.prepareStatement(updateDB);
        preparedStatement.setInt(1, bookB);
        preparedStatement.setInt(2, phyB);
        preparedStatement.setInt(3, comp);
        preparedStatement.setInt(4, var);

        preparedStatement.close();
    }
}
