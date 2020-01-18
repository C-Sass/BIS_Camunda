package de.ostfalia.ebike2020.messages;

import de.ostfalia.ebike2020.DatabaseConnection;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class OfferConfirmYes implements JavaDelegate {
    Connection connection = DatabaseConnection.getConnection();

    public OfferConfirmYes() throws SQLException {
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
            int resB = resultSet.getInt("Bestand_Reserviert");

            bookB++;
            resB--;

            booking(bookB, resB, comp, var);
        }
        addBooking((Integer) execution.getVariable("CUSTOMER_ID"), (Integer) execution.getVariable("CONFIG_ID"));

        connection.close();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));
        hashMap.put("CONFIG_ID", execution.getVariable("CONFIG_ID"));

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Starte Produktion", hashMap);
    }

    public void booking(int bookB, int resB, int comp, int var) throws SQLException {

        String updateDB = "UPDATE `e_bike_2020`.`variante` " +
                "SET `Bestand_Bestellt` = ?, `Bestand_Reserviert` = ? " +
                "WHERE (`idKomponente` = ?) and (`idVariante` = ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(updateDB);
        preparedStatement.setInt(1, bookB);
        preparedStatement.setInt(2, resB);
        preparedStatement.setInt(3, comp);
        preparedStatement.setInt(4, var);

        preparedStatement.close();
    }

    public void addBooking(int custID, int confID) throws SQLException {
        CallableStatement max = connection.prepareCall("SELECT MAX(idBestellung) FROM bestellung");
        ResultSet resultSet = max.executeQuery();
        int idBooking = !resultSet.next() ? 0 : resultSet.getInt(1) + 1;

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlNow = Timestamp.valueOf(now);

        String sql = "INSERT INTO `e_bike_2020`.`bestellung` (`idBestellung`, `idKunde`, `idKonfiguration`, `Zeitstempel`) " +
                "VALUES (?, ?, ?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, idBooking);
        preparedStatement.setInt(2, custID);
        preparedStatement.setInt(3, confID);
        preparedStatement.setTimestamp(4, sqlNow);

        resultSet.close();
        max.close();
        preparedStatement.close();
    }
}
