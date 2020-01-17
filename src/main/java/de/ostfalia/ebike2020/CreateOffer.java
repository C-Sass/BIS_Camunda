package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateOffer implements JavaDelegate {
    Connection connection = DatabaseConnection.getConnection();

    public CreateOffer() throws SQLException {
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
            int phyB = resultSet.getInt("Bestand_Physisch");
            int resB = resultSet.getInt("Bestand_Reserviert");

            phyB--;
            resB++;

            reservation(phyB, resB, comp, var);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    public void reservation(int phyB, int resB, int comp, int var) throws SQLException {

        String updateDB = "UPDATE `e_bike_2020`.`variante` " +
                "SET `Bestand_Physisch` = '?', `Bestand_Reserviert` = '?' " +
                "WHERE (`idKomponente` = '?') and (`idVariante` = '?');";

        PreparedStatement preparedStatement = connection.prepareStatement(updateDB);
        preparedStatement.setInt(1, phyB);
        preparedStatement.setInt(2, resB);
        preparedStatement.setInt(3, comp);
        preparedStatement.setInt(4, var);

        preparedStatement.close();
    }
}
