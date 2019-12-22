package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class LoadVariants implements JavaDelegate {
    public void execute(DelegateExecution execution) throws Exception {
        HashMap<Integer, String> rahmen = new HashMap<>();
        HashMap<Integer, String> farbe = new HashMap<>();
        HashMap<Integer, String> akku = new HashMap<>();
        HashMap<Integer, String> motor = new HashMap<>();
        int productID = (int) execution.getVariable("PRODUCT_ID");

        String queryRahmen = "SELECT * FROM e_bike_2020.produkt_variante\n" +
                "NATURAL JOIN variante\n" +
                "WHERE idProdukt = ? AND idKomponente = 1";

        String queryFarbe = "SELECT * FROM e_bike_2020.produkt_variante\n" +
                "NATURAL JOIN variante\n" +
                "WHERE idProdukt = ? AND idKomponente = 2";

        String queryAkku = "SELECT * FROM e_bike_2020.produkt_variante\n" +
                "NATURAL JOIN variante\n" +
                "WHERE idProdukt = ? AND idKomponente = 25";

        String queryMotor = "SELECT * FROM e_bike_2020.produkt_variante\n" +
                "NATURAL JOIN variante\n" +
                "WHERE idProdukt = ? AND idKomponente = 40";

        fillMap(rahmen, productID, queryRahmen);
        fillMap(farbe, productID, queryFarbe);
        fillMap(akku, productID, queryAkku);
        fillMap(motor, productID, queryMotor);

        execution.setVariable("AVAILABLE_RAHMEN", Variables.objectValue(rahmen)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        execution.setVariable("AVAILABLE_FARBE", Variables.objectValue(farbe)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        execution.setVariable("AVAILABLE_AKKU", Variables.objectValue(akku)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        execution.setVariable("AVAILABLE_MOTOR", Variables.objectValue(motor)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
    }

    public void fillMap(HashMap<Integer, String> map, int productID, String query) throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, productID);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            map.put(resultSet.getInt("idVariante"), resultSet.getString("Name"));
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
