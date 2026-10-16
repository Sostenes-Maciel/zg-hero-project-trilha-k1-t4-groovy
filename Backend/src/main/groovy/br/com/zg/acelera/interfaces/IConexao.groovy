package br.com.zg.acelera.interfaces

import java.sql.Connection
import java.sql.SQLException

interface IConexao {
    Connection conectar() throws SQLException
}