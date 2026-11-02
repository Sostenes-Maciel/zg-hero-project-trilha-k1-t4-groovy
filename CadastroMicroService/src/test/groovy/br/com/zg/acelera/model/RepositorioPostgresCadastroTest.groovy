package br.com.zg.acelera.model

import br.com.zg.acelera.model.RepositorioPostgresCadastro
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverPropertyInfo
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Logger

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class RepositorioPostgresCadastroTest {
    private static final String URL_TESTE = 'jdbc:cadastro-teste:'

    private DriverDeTeste driver
    private RepositorioPostgresCadastro repositorio

    @BeforeEach
    void registrarDriverDeTeste() {
        driver = new DriverDeTeste()
        DriverManager.registerDriver(driver)
        repositorio = new RepositorioPostgresCadastro(URL_TESTE, 'usuario-teste', 'senha-teste')
    }

    @AfterEach
    void removerDriverDeTeste() {
        DriverManager.deregisterDriver(driver)
    }

    @Test
    void deveExecutarInsercaoDeCandidatoComParametrosEFecharRecursos() {
        repositorio.inserirCandidato([
                cpf: '12345678900',
                nome: 'Candidato Teste',
                email: 'candidato@example.com',
                pais: 'Brasil',
                estado: 'SP',
                cep: '01000000',
                descricao: 'Descricao',
                idade: 26,
                competencias: ['Java']
        ])

        assertTrue(driver.comandos[0].contains(
                'INSERT INTO candidato (cpf, nome, email, pais, estado, cep, descricao, idade)'
        ))
        assertEquals(
                [
                        1: '12345678900',
                        2: 'Candidato Teste',
                        3: 'candidato@example.com',
                        4: 'Brasil',
                        5: 'SP',
                        6: '01000000',
                        7: 'Descricao',
                        8: 26
                ],
                driver.parametros[0]
        )
        assertTrue(driver.comandos[1].contains('INSERT INTO competencia (nome)'))
        assertEquals([1: 'Java'], driver.parametros[1])
        assertTrue(driver.comandos[2].contains('INSERT INTO candidato_competencia (cpf_candidato, id_competencia)'))
        assertEquals([1: '12345678900', 2: 42L], driver.parametros[2])
        assertTrue(driver.transacaoConfirmada)
        assertTrue(driver.statementFechado)
        assertTrue(driver.conexaoFechada)
    }

    @Test
    void deveExecutarInsercaoDeEmpresaComParametrosEFecharRecursos() {
        repositorio.inserirEmpresa([
                cnpj: '12345678000199',
                nome: 'Empresa Teste',
                email: 'empresa@example.com',
                pais: 'Brasil',
                estado: 'RJ',
                cep: '20000000',
                descricao: 'Descricao',
                competencias: ['Logistica']
        ])

        assertTrue(driver.comandos[0].contains(
                'INSERT INTO empresa (cnpj, nome, email, pais, estado, cep, descricao)'
        ))
        assertEquals(
                [
                        1: '12345678000199',
                        2: 'Empresa Teste',
                        3: 'empresa@example.com',
                        4: 'Brasil',
                        5: 'RJ',
                        6: '20000000',
                        7: 'Descricao'
                ],
                driver.parametros[0]
        )
        assertTrue(driver.comandos[2].contains('INSERT INTO empresa_competencia (cnpj_empresa, id_competencia)'))
        assertEquals([1: '12345678000199', 2: 42L], driver.parametros[2])
        assertTrue(driver.transacaoConfirmada)
        assertTrue(driver.statementFechado)
        assertTrue(driver.conexaoFechada)
    }

    private static class DriverDeTeste implements Driver {
        List<String> comandos = []
        List<Map<Integer, Object>> parametros = []
        boolean statementFechado
        boolean conexaoFechada
        boolean transacaoConfirmada
        boolean transacaoRevertida
        boolean autoCommit = true

        @Override
        Connection connect(String url, Properties info) throws SQLException {
            if (!acceptsURL(url)) {
                return null
            }
            Connection conexao = Proxy.newProxyInstance(
                    getClass().classLoader,
                    [Connection] as Class[],
                    { proxy, method, args ->
                        switch (method.name) {
                            case 'prepareStatement':
                                comandos.add(args[0] as String)
                                parametros.add([:])
                                int indice = comandos.size() - 1
                                return criarInstrucao(indice)
                            case 'setAutoCommit':
                                autoCommit = args[0] as boolean
                                return null
                            case 'getAutoCommit':
                                return autoCommit
                            case 'commit':
                                transacaoConfirmada = true
                                return null
                            case 'rollback':
                                transacaoRevertida = true
                                return null
                            case 'close':
                                conexaoFechada = true
                                return null
                            case 'isClosed':
                                return conexaoFechada
                            case 'toString':
                                return 'Connection de teste'
                            default:
                                return valorPadrao(method.returnType)
                        }
                    } as InvocationHandler
            ) as Connection
            conexao
        }

        private PreparedStatement criarInstrucao(int indice) {
            Proxy.newProxyInstance(
                    getClass().classLoader,
                    [PreparedStatement] as Class[],
                    { proxy, method, args ->
                        if (method.name in ['setString', 'setInt']) {
                            parametros[indice][args[0] as Integer] = args[1]
                            return null
                        }
                        switch (method.name) {
                            case 'executeUpdate':
                                return 1
                            case 'setLong':
                                parametros[indice][args[0] as Integer] = args[1]
                                return null
                            case 'executeQuery':
                                return Proxy.newProxyInstance(
                                        getClass().classLoader,
                                        [ResultSet] as Class[],
                                        { resultadoProxy, resultadoMetodo, resultadoArgs ->
                                            switch (resultadoMetodo.name) {
                                                case 'next':
                                                    return true
                                                case 'getLong':
                                                    return 42L
                                                case 'close':
                                                    return null
                                                case 'toString':
                                                    return 'ResultSet de teste'
                                                default:
                                                    return valorPadrao(resultadoMetodo.returnType)
                                            }
                                        } as InvocationHandler
                                ) as ResultSet
                            case 'close':
                                statementFechado = true
                                return null
                            case 'isClosed':
                                return statementFechado
                            case 'toString':
                                return 'PreparedStatement de teste'
                            default:
                                return valorPadrao(method.returnType)
                        }
                    } as InvocationHandler
            ) as PreparedStatement
        }

        @Override
        boolean acceptsURL(String url) {
            url?.startsWith(URL_TESTE)
        }

        @Override
        DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
            [] as DriverPropertyInfo[]
        }

        @Override
        int getMajorVersion() {
            1
        }

        @Override
        int getMinorVersion() {
            0
        }

        @Override
        boolean jdbcCompliant() {
            false
        }

        @Override
        Logger getParentLogger() {
            Logger.getGlobal()
        }

        private static Object valorPadrao(Class tipo) {
            if (!tipo.isPrimitive() || tipo == Void.TYPE) {
                return null
            }
            if (tipo == Boolean.TYPE) {
                return false
            }
            if (tipo == Character.TYPE) {
                return (char) 0
            }
            0
        }
    }
}
