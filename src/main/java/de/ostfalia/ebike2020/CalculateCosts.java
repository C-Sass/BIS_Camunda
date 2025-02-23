package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class CalculateCosts implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        HashMap<Double, Integer> materialCosts = new HashMap<>();
        HashMap<Double, Integer> productionCosts = new HashMap<>();
        double totalCosts = 0.0;

        String getCosts = "SELECT Einzelpreis, Menge_pro_Produkt, Montagedauer_sec, Kostensatz_h, Zusatzmontagedauer_sec, Zusatzmaterialkosten " +
                "FROM e_bike_2020.konfigurationselement " +
                "NATURAL JOIN variante " +
                "INNER JOIN komponente ON komponente.idKomponente = konfigurationselement.idKomponente " +
                "INNER JOIN arbeitsgang on arbeitsgang.idArbeitsgang = komponente.idArbeitsgang " +
                "INNER JOIN ressource on ressource.idRessource = arbeitsgang.idRessource " +
                "WHERE idKonfiguration = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(getCosts);
        preparedStatement.setInt(1, (Integer) execution.getVariable("CONFIG_ID"));
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            materialCosts.put(resultSet.getDouble("Einzelpreis"),
                    resultSet.getInt("Menge_pro_Produkt"));

            productionCosts.put(resultSet.getDouble("Kostensatz_h"),
                    resultSet.getInt("Montagedauer_sec"));

            if (resultSet.getInt("Zusatzmontagedauer_sec") > 0) {
                productionCosts.put(resultSet.getDouble("Kostensatz_h"), resultSet.getInt("Zusatzmontagedauer_sec"));
            }

            if (resultSet.getDouble("Zusatzmaterialkosten") > 0.0) {
                materialCosts.put(resultSet.getDouble("Zusatzmaterialkosten"), 1);
            }
        }

        for (HashMap.Entry<Double, Integer> entry : materialCosts.entrySet()) {
            double key = entry.getKey();
            int value = entry.getValue();
            totalCosts += (key * value);
        }
        for (HashMap.Entry<Double, Integer> entry : productionCosts.entrySet()) {
            double key = entry.getKey();
            int value = entry.getValue();
            totalCosts += ((key / 3600) * value);
        }

        totalCosts *= 1.5; //Gewinnzuschlag
        execution.setVariable("totalCosts", totalCosts);

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
