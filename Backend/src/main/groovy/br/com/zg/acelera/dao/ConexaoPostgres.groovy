package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

import groovy.transform.CompileStatic

class ConexaoPostgres implements IConexao {

    private static final String URL = "jdbc:postgresql://localhost:5432/LinkeTinder_BD"
    private static final String USUARIO = "postgres"
    private static final String SENHA = "postgres"

    private static ConexaoPostgres instancia
    private Connection conexaoAberta

    private ConexaoPostgres() {}

    static ConexaoPostgres getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoPostgres()
        }
        return instancia
    }

    @CompileStatic
    Connection conectar() throws SQLException {
        try {

            Class.forName("org.postgresql.Driver")

            if (conexaoAberta == null || conexaoAberta.isClosed()) {
                conexaoAberta = DriverManager.getConnection(URL, USUARIO, SENHA)
            }
            return conexaoAberta
        } catch (SQLException e) {
            throw new RuntimeException("Falha fatal ao conectar no banco de dados.", e)
        }
    }
}