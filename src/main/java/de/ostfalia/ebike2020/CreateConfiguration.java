package de.ostfalia.ebike2020;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CreateConfiguration implements JavaDelegate {

    Connection connection = DatabaseConnection.getConnection();
    HashMap<Integer, Integer> CompVar = new HashMap<>();

    public CreateConfiguration() throws SQLException {
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        CallableStatement max = connection.prepareCall("SELECT MAX(idKonfiguration) FROM konfiguration");
        ResultSet resultSet = max.executeQuery();
        int idConfig = !resultSet.next() ? 0 : resultSet.getInt(1) + 1;
        execution.setVariable("CONFIG_ID", idConfig);

        LocalDateTime now = LocalDateTime.now();
        Timestamp sqlNow = Timestamp.valueOf(now);

        String addConfig = "INSERT INTO `e_bike_2020`.`konfiguration` (`idKonfiguration`, `idKunde`, `Zeitstempel`, `FreitextWunsch`) VALUES (?, ?, ?, ?);";
        CallableStatement callableStatement = connection.prepareCall(addConfig);
        callableStatement.setInt(1, idConfig);
        callableStatement.setInt(2, (Integer) execution.getVariable("CUSTOMER_ID"));
        callableStatement.setTimestamp(3, sqlNow);
        callableStatement.setString(4, (String) execution.getVariable("ADDITIONAL_WISH"));
        callableStatement.executeUpdate();

        addCompVarS(execution.getVariable("PRODUCT_ID"));
        addCompVarM(execution.getVariable("PRODUCT_ID"), execution.getVariable("RAHMEN_COMP_ID"), execution.getVariable("RAHMEN_ID"));
        addCompVarM(execution.getVariable("PRODUCT_ID"), execution.getVariable("MOTOR_COMP_ID"), execution.getVariable("MOTOR_ID"));
        addCompVarL(execution.getVariable("PRODUCT_ID"), execution.getVariable("RAHMEN_ID"), execution.getVariable("MOTOR_ID"));

        sqlInsertConfigElement(idConfig, execution.getVariable("PRODUCT_ID"), execution.getVariable("RAHMEN_ID"), execution.getVariable("RAHMEN_COMP_ID"));
        sqlInsertConfigElement(idConfig, execution.getVariable("PRODUCT_ID"), execution.getVariable("FARBE_ID"), execution.getVariable("FARBE_COMP_ID"));
        sqlInsertConfigElement(idConfig, execution.getVariable("PRODUCT_ID"), execution.getVariable("AKKU_ID"), execution.getVariable("AKKU_COMP_ID"));
        sqlInsertConfigElement(idConfig, execution.getVariable("PRODUCT_ID"), execution.getVariable("MOTOR_ID"), execution.getVariable("MOTOR_COMP_ID"));
        for (Map.Entry<Integer, Integer> entry : CompVar.entrySet()) {
            sqlInsertConfigElement(idConfig, execution.getVariable("PRODUCT_ID"), entry.getKey(), entry.getValue());
        }
        boolean textAdded;
        textAdded = !((String) execution.getVariable("ADDITIONAL_WISH")).isEmpty();
        execution.setVariable("textAdded", textAdded);

        resultSet.close();
        callableStatement.close();
        connection.close();
    }

    public void sqlInsertConfigElement(int config, Object product, Object component, Object variant) throws SQLException {

        String addComponents = "INSERT INTO `e_bike_2020`.`konfigurationselement` (`idKonfiguration`, `idProdukt`, `idKomponente`, `idVariante`) VALUES (?, ?, ?, ?);";
        CallableStatement callableStatement = connection.prepareCall(addComponents);
        callableStatement.setInt(1, config);
        callableStatement.setInt(2, (Integer) product);
        callableStatement.setInt(3, (Integer) component);
        callableStatement.setInt(4, (Integer) variant);
        callableStatement.executeUpdate();
    }

    public void addCompVarS(Object product) throws SQLException {

        String sql = "SELECT *" +
                " FROM e_bike_2020.produkt_variante" +
                " WHERE idProdukt = ?" +
                " GROUP BY idKomponente" +
                " HAVING count(*) = 1";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (Integer) product);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            CompVar.put(resultSet.getInt("idKomponente"), resultSet.getInt("idVariante"));
        }
    }

    public void addCompVarM(Object product, Object comp_basis, Object var_basis) throws SQLException {

        String sql = "SELECT *" +
                " FROM e_bike_2020.produkt_variante" +
                " NATURAL JOIN variante_hat_abhaengigkeit" +
                " WHERE idProdukt = ?" +
                " AND idKomponente_Basis = ?" +
                " AND idVariante_Basis = ?" +
                " GROUP BY idKomponente" +
                " HAVING count(*) = 1";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (Integer) product);
        preparedStatement.setInt(2, (Integer) comp_basis);
        preparedStatement.setInt(3, (Integer) var_basis);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            CompVar.put(resultSet.getInt("idKomponente"), resultSet.getInt("idVariante"));
        }
    }

    public void addCompVarL(Object product, Object var_basis1, Object var_basis40) throws SQLException {

        HashSet<Integer> comp13_1 = new HashSet<>();
        HashSet<Integer> comp13_40 = new HashSet<>();
        HashSet<Integer> comp21_1 = new HashSet<>();
        HashSet<Integer> comp21_40 = new HashSet<>();

        String sql_13_1 = "SELECT a.*" +
                " FROM (SELECT *" +
                " FROM e_bike_2020.produkt_variante" +
                " NATURAL JOIN variante_hat_abhaengigkeit" +
                " WHERE idProdukt = ?" +
                " AND idKomponente = 13)a" +
                " WHERE idKomponente_Basis = 1" +
                " AND idVariante_Basis = ?" +
                " AND idProdukt = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql_13_1);
        preparedStatement.setInt(1, (Integer) product);
        preparedStatement.setInt(2, (Integer) var_basis1);
        preparedStatement.setInt(3, (Integer) product);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            comp13_1.add(resultSet.getInt("idVariante"));
        }

        String sql_13_40 = "SELECT a.*" +
                " FROM (SELECT *" +
                " FROM e_bike_2020.produkt_variante" +
                " NATURAL JOIN variante_hat_abhaengigkeit" +
                " WHERE idProdukt = ?" +
                " AND idKomponente = 13)a" +
                " WHERE idKomponente_Basis = 40" +
                " AND idVariante_Basis = ?" +
                " AND idProdukt = ?";

        PreparedStatement preparedStatement1 = connection.prepareStatement(sql_13_40);
        preparedStatement1.setInt(1, (Integer) product);
        preparedStatement1.setInt(2, (Integer) var_basis40);
        preparedStatement1.setInt(3, (Integer) product);
        ResultSet resultSet1 = preparedStatement1.executeQuery();

        while (resultSet1.next()) {
            comp13_40.add(resultSet1.getInt("idVariante"));
        }

        String sql_21_1 = "SELECT a.*" +
                " FROM (SELECT *" +
                " FROM e_bike_2020.produkt_variante" +
                " NATURAL JOIN variante_hat_abhaengigkeit" +
                " WHERE idProdukt = ?" +
                " AND idKomponente = 21)a" +
                " WHERE idKomponente_Basis = 1" +
                " AND idVariante_Basis = ?" +
                " AND idProdukt = ?";

        PreparedStatement preparedStatement2 = connection.prepareStatement(sql_21_1);
        preparedStatement2.setInt(1, (Integer) product);
        preparedStatement2.setInt(2, (Integer) var_basis1);
        preparedStatement2.setInt(3, (Integer) product);
        ResultSet resultSet2 = preparedStatement2.executeQuery();

        while (resultSet2.next()) {
            comp21_1.add(resultSet2.getInt("idVariante"));
        }

        String sql_21_40 = "SELECT a.*" +
                " FROM (SELECT *" +
                " FROM e_bike_2020.produkt_variante" +
                " NATURAL JOIN variante_hat_abhaengigkeit" +
                " WHERE idProdukt = ?" +
                " AND idKomponente = 21)a" +
                " WHERE idKomponente_Basis = 40" +
                " AND idVariante_Basis = ?" +
                " AND idProdukt = ?";

        PreparedStatement preparedStatement3 = connection.prepareStatement(sql_21_40);
        preparedStatement3.setInt(1, (Integer) product);
        preparedStatement3.setInt(2, (Integer) var_basis40);
        preparedStatement3.setInt(3, (Integer) product);
        ResultSet resultSet3 = preparedStatement3.executeQuery();

        while (resultSet3.next()) {
            comp21_40.add(resultSet3.getInt("idVariante"));
        }

        for (int i : comp13_1) {
            if (comp13_40.contains(i)) {
                CompVar.put(13, i);
            }
        }
        for (int i : comp21_1) {
            if (comp21_40.contains(i)) {
                CompVar.put(21, i);
            }
        }
    }
}
