package br.com.zg.acelera.interfaces

import java.sql.Connection
import java.sql.SQLException

interface IConexaoFactory {
    Connection conectar() throws SQLException
}