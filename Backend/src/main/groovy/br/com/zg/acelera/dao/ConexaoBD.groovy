package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexaoFactory

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConexaoBD implements IConexaoFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/LinkeTinder_BD"
    private static final String USUARIO = "postgres"
    private static final String SENHA = "postgres"

    @Override
    Connection conectar() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA)
        } catch (SQLException e) {
            throw new RuntimeException("Falha fatal ao conectar no banco de dados.", e)
        }
    }
}