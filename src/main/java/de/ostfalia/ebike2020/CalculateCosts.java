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

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();
        Map <Float, Float> additionCosts = new HashMap<>();
        Map <Float, Integer> materialCosts = new HashMap<>();
        //Produktionskosten fehlen noch...Idee wie einbauen?
        // Map <Float> productionCosts  = new HashMap<>();

        // Test Statement für Konfiguration 2
        String getCosts = "* " +
                "FROM konfigurationselement " +
                "LEFT JOIN variante ON variante.idVariante = konfigurationselement.idVariante " +
                "LEFT JOIN komponente ON komponente.idKomponente = variante.idKomponente " +
                "LEFT JOIN arbeitsgang ON arbeitsgang.idArbeitsgang = komponente.idKomponente " +
                "LEFT JOIN ressource ON ressource.idRessource = arbeitsgang.idRessource " +
                "WHERE idKonfiguration = 2" +
                "AND variante.idKomponente = konfigurationselement.idKomponente";

        PreparedStatement preparedStatement = connection.prepareStatement(getCosts);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
                additionCosts.put(resultSet.getFloat("Zusatzmontagedauer_sec"),
                        resultSet.getFloat("Zusatzmaterialkosten"));
                materialCosts.put(resultSet.getFloat("Einzelpreis"),
                        resultSet.getInt("Menge_pro_Produkt"));
               // productionCosts.put(resultSet.getInt("Kostensatz_h"));
        }
        execution.setVariable("ZUSATZKOSTEN", Variables.objectValue(additionCosts)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        execution.setVariable("MATERIALKOSTEN", Variables.objectValue(materialCosts)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
