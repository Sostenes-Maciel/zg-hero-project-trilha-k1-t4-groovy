package br.com.zg.acelera.dao

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConexaoBD {

    private static final String URL = "jdbc:postgresql://localhost:5432/LinkeTinder_BD"

    private static final String USUARIO = "postgres"
    private static final String SENHA = "postgres"

    static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA)
        } catch (SQLException e) {
            throw new RuntimeException("Falha fatal ao conectar no banco de dados. Verifique se o Postgres está rodando e as credenciais.", e)
        }
    }
}

