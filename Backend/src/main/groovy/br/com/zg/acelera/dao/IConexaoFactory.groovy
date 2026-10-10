package br.com.zg.acelera.dao

import java.sql.Connection
import java.sql.SQLException

interface IConexaoFactory {
    Connection conectar() throws SQLException
}