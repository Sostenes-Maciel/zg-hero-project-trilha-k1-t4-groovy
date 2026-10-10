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
            println "Erro ao conectar com o banco de dados: ${e.message}"
            throw e
        }
    }
}

