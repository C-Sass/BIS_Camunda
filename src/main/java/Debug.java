import de.ostfalia.ebike2020.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Debug {
    static double totalCosts;

    public static void main(String[] args) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        HashMap<Double, Integer> materialCosts = new HashMap<>();
        HashMap<Double, Integer> productionCosts = new HashMap<>();

        String getCosts = "SELECT Einzelpreis, Menge_pro_Produkt, Montagedauer_sec, Kostensatz_h, Zusatzmontagedauer_sec, Zusatzmaterialkosten " +
                "FROM e_bike_2020.konfigurationselement " +
                "NATURAL JOIN variante " +
                "INNER JOIN komponente ON komponente.idKomponente = konfigurationselement.idKomponente " +
                "INNER JOIN arbeitsgang on arbeitsgang.idArbeitsgang = komponente.idArbeitsgang " +
                "INNER JOIN ressource on ressource.idRessource = arbeitsgang.idRessource " +
                "WHERE idKonfiguration = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(getCosts);
        preparedStatement.setInt(1, 1);
        //preparedStatement.setInt(1, (Integer) execution.getVariable("CONFIG_ID"));
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
            System.out.println("material_key: " + entry.getKey());
            System.out.println("material_value: " + entry.getValue());
            totalCosts += (entry.getKey() * entry.getValue());
            System.out.println("material_added: " + entry.getKey() * entry.getValue());
            System.out.println("totalCosts: " + totalCosts);
            System.out.println();
        }
        for (HashMap.Entry<Double, Integer> entry : productionCosts.entrySet()) {
            System.out.println("prod_key: " + entry.getKey());
            System.out.println("prod_value: " + entry.getValue());
            totalCosts += ((entry.getKey() / 3600) * entry.getValue());
            System.out.println("prod_added: " + (entry.getKey() / 3600) * entry.getValue());
            System.out.println("totalCosts: " + totalCosts);
            System.out.println();
        }

        totalCosts *= 1.5; //Gewinnzuschlag
        System.out.println("totalCosts + Gewinn: " + totalCosts);
        //execution.setVariable("totalCosts", totalCosts);

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
