package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteOrder implements JavaDelegate {
    Connection connection = DatabaseConnection.getConnection();

    public DeleteOrder() throws SQLException {
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

            phyB++;
            resB--;

            reservationCancel(phyB, resB, comp, var);
        }
        deleteConfig((Integer) execution.getVariable("CONFIG_ID"));

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    public void reservationCancel(int phyB, int resB, int comp, int var) throws SQLException {

        String updateDB = "UPDATE `e_bike_2020`.`variante` " +
                "SET `Bestand_Physisch` = ?, `Bestand_Reserviert` = ? " +
                "WHERE (`idKomponente` = ?) and (`idVariante` = ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(updateDB);
        preparedStatement.setInt(1, phyB);
        preparedStatement.setInt(2, resB);
        preparedStatement.setInt(3, comp);
        preparedStatement.setInt(4, var);

        preparedStatement.close();
    }

    public void deleteConfig(int configID) throws SQLException {

        String delEl = "delete from konfigurationselement " +
                "where idKonfiguration = ?";

        String delCon = "delete from konfiguration " +
                "where idKonfiguration = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(delEl);
        preparedStatement.setInt(1, configID);
        preparedStatement.executeUpdate();

        PreparedStatement preparedStatement1 = connection.prepareStatement(delCon);
        preparedStatement1.setInt(1, configID);
        preparedStatement1.executeUpdate();

        preparedStatement.close();
        preparedStatement1.close();
    }
}
