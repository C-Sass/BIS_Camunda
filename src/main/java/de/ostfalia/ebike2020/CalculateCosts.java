package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;



public class CalculateCosts implements JavaDelegate {
    Double totalCosts;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        Map <Double, Integer> materialCosts = new HashMap<>();
        //Produktionskosten fehlen noch...Idee wie einbauen?
        Map <Double, Double> productionCosts  = new HashMap<>();
        Map<Double, Double> addProdCosts = new HashMap<>();



        // Test Statement für Konfiguration 2
        String getCosts = "SELECT Einzelpreis, Menge_pro_Produkt, Montagedauer_sec, Kostensatz_h, Zusatzmontagedauer_sec, Zusatzmaterialkosten\n" +
                "FROM e_bike_2020.konfigurationselement \n" +
                "LEFT JOIN e_bike_2020.variante ON variante.idVariante = konfigurationselement.idVariante\n" +
                "LEFT JOIN e_bike_2020.komponente ON komponente.idKomponente = variante.idKomponente\n" +
                "LEFT JOIN e_bike_2020.arbeitsgang ON arbeitsgang.idArbeitsgang = komponente.idKomponente\n" +
                "LEFT JOIN e_bike_2020.ressource ON ressource.idRessource = arbeitsgang.idRessource\n" +
                "Where idKonfiguration = 2 AND variante.idKomponente = konfigurationselement.idKomponente";

        PreparedStatement preparedStatement = connection.prepareStatement(getCosts);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            // is not NULL
            materialCosts.put(resultSet.getDouble("Einzelpreis"),
                        resultSet.getInt("Menge_pro_Produkt"));

            productionCosts.put(resultSet.getDouble("Kostensatz_h"),
                        resultSet.getDouble("Montagedauer_sec"));
            addProdCosts.put(resultSet.getDouble("Zusatzmontagedauer_sec"), resultSet.getDouble("Kostensatz_h"));

            if (resultSet.getDouble("Zusatzmaterialkosten") != 0.0) {
                materialCosts.put(resultSet.getDouble("Zusatzmaterialkosten"),
                        1);
            }
        }

        for (Map.Entry<Double, Integer> entry : materialCosts.entrySet()) {
           totalCosts += (entry.getKey()* entry.getValue());
        }
        for (Map.Entry<Double, Double> entry : productionCosts.entrySet()) {
            totalCosts += (entry.getKey()* entry.getValue());
        }
        for (Map.Entry<Double, Double> entry : addProdCosts.entrySet()) {
            totalCosts += (entry.getKey()* entry.getValue());
        }

        execution.setVariable("totalCosts", totalCosts);

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
