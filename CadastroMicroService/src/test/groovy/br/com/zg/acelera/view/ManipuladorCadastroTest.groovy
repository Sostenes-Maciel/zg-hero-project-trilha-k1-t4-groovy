package br.com.zg.acelera.view

import br.com.zg.acelera.controller.ControladorCadastro
import br.com.zg.acelera.model.RepositorioCadastro
import br.com.zg.acelera.model.ServicoCadastro
import br.com.zg.acelera.view.ManipuladorHttpCadastro
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.SQLException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue

class ManipuladorCadastroTest {
    private static final String ORIGEM_PERMITIDA = 'http://localhost:5173'

    private HttpServer servidor
    private ExecutorService executor
    private HttpClient cliente
    private RepositorioDeTeste repositorio
    private URI uriBase

    @BeforeEach
    void iniciarServidor() {
        repositorio = new RepositorioDeTeste()
        ServicoCadastro servico = new ServicoCadastro(repositorio)
        ControladorCadastro controlador = new ControladorCadastro(servico)
        servidor = HttpServer.create(new InetSocketAddress('localhost', 0), 0)
        servidor.createContext('/', new ManipuladorHttpCadastro(controlador, ORIGEM_PERMITIDA))
        executor = Executors.newSingleThreadExecutor()
        servidor.executor = executor
        servidor.start()
        uriBase = URI.create("http://localhost:${servidor.address.port}")
        cliente = HttpClient.newHttpClient()
    }

    @AfterEach
    void encerrarServidor() {
        servidor?.stop(0)
        executor?.shutdownNow()
    }

    @Test
    void deveCadastrarCandidatoPeloEndpointHttp() {
        HttpResponse<String> resposta = enviarJson('POST', '/api/candidatos', [
                cpf: '12345678900',
                nome: 'Candidato Teste',
                email: 'candidato@example.com',
                pais: 'Brasil',
                estado: 'SP',
                cep: '01000000',
                descricao: 'Cadastro via HTTP',
                idade: 25,
                competencias: ['Java', 'Groovy']
        ])

        assertEquals(201, resposta.statusCode())
        assertEquals('application/json; charset=utf-8', resposta.headers().firstValue('Content-Type').orElse(null))
        assertEquals('candidato', interpretarJson(resposta).tipo)
        assertEquals('12345678900', interpretarJson(resposta).identificador)
        assertEquals('12345678900', repositorio.candidato.cpf)
        assertEquals(['Java', 'Groovy'], repositorio.candidato.competencias)
    }

    @Test
    void deveCadastrarEmpresaPeloEndpointHttp() {
        HttpResponse<String> resposta = enviarJson('POST', '/api/empresas', [
                cnpj: '12345678000199',
                nome: 'Empresa Teste',
                email: 'empresa@example.com',
                pais: 'Brasil',
                estado: 'RJ',
                cep: '20000000',
                descricao: 'Cadastro via HTTP',
                competencias: ['Logistica']
        ])

        assertEquals(201, resposta.statusCode())
        assertEquals('empresa', interpretarJson(resposta).tipo)
        assertEquals('12345678000199', repositorio.empresa.cnpj)
        assertEquals(['Logistica'], repositorio.empresa.competencias)
    }

    @Test
    void deveRetornar400ParaJsonMalformado() {
        HttpResponse<String> resposta = enviarTexto('POST', '/api/candidatos', '{json invalido', 'application/json')

        assertEquals(400, resposta.statusCode())
        assertEquals('JSON invalido.', interpretarJson(resposta).mensagem)
        assertNull(repositorio.candidato)
    }

    @Test
    void deveRetornar400QuandoJsonNaoForObjeto() {
        HttpResponse<String> resposta = enviarTexto('POST', '/api/candidatos', '["valor"]', 'application/json')

        assertEquals(400, resposta.statusCode())
        assertEquals('O JSON deve ser um objeto.', interpretarJson(resposta).mensagem)
    }

    @Test
    void deveRetornar415QuandoContentTypeNaoForJson() {
        HttpResponse<String> resposta = enviarTexto('POST', '/api/candidatos', '{}', 'text/plain')

        assertEquals(415, resposta.statusCode())
        String erro = interpretarJson(resposta).mensagem as String
        assertTrue(erro.contains('application/json'))
    }

    @Test
    void deveRetornar422ParaDadosDeCadastroInvalidos() {
        HttpResponse<String> resposta = enviarJson('POST', '/api/empresas', [cnpj: ''])

        assertEquals(422, resposta.statusCode())
        String erro = interpretarJson(resposta).mensagem as String
        assertTrue(erro.contains('cnpj'))
        assertNull(repositorio.empresa)
    }

    @Test
    void deveRetornar404ParaEndpointDesconhecido() {
        HttpResponse<String> resposta = enviarJson('POST', '/api/outro', [:])

        assertEquals(404, resposta.statusCode())
        assertEquals('Endpoint nao encontrado.', interpretarJson(resposta).mensagem)
    }

    @Test
    void deveRetornar405ParaMetodoNaoPermitido() {
        HttpResponse<String> resposta = enviarTexto('GET', '/api/candidatos', '', null)

        assertEquals(405, resposta.statusCode())
        assertEquals('POST, OPTIONS', resposta.headers().firstValue('Allow').orElse(null))
    }

    @Test
    void deveRetornar413QuandoCorpoExcederUmMiB() {
        String corpoGrande = 'x' * (1_048_577)
        HttpResponse<String> resposta = enviarTexto(
                'POST',
                '/api/candidatos',
                corpoGrande,
                'application/json'
        )

        assertEquals(413, resposta.statusCode())
        String erro = interpretarJson(resposta).mensagem as String
        assertTrue(erro.contains('1 MiB'))
    }

    @Test
    void devePermitirPreflightCorsDaOrigemConfigurada() {
        HttpRequest request = HttpRequest.newBuilder(uriBase.resolve('/api/candidatos'))
                .header('Origin', ORIGEM_PERMITIDA)
                .header('Access-Control-Request-Method', 'POST')
                .method('OPTIONS', HttpRequest.BodyPublishers.noBody())
                .build()

        HttpResponse<String> resposta = cliente.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(204, resposta.statusCode())
        assertEquals(
                ORIGEM_PERMITIDA,
                resposta.headers().firstValue('Access-Control-Allow-Origin').orElse(null)
        )
        assertEquals(
                'POST, OPTIONS',
                resposta.headers().firstValue('Access-Control-Allow-Methods').orElse(null)
        )
    }

    @Test
    void naoDevePermitirCorsDeOrigemDiferente() {
        HttpRequest request = HttpRequest.newBuilder(uriBase.resolve('/api/candidatos'))
                .header('Origin', 'https://origem-nao-autorizada.example')
                .header('Access-Control-Request-Method', 'POST')
                .method('OPTIONS', HttpRequest.BodyPublishers.noBody())
                .build()

        HttpResponse<String> resposta = cliente.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(204, resposta.statusCode())
        assertFalse(resposta.headers().firstValue('Access-Control-Allow-Origin').isPresent())
    }

    @Test
    void deveRetornar409QuandoIdentificadorOuEmailJaExistir() {
        repositorio.falha = new SQLException('violacao de unicidade', '23505')

        HttpResponse<String> resposta = enviarJson('POST', '/api/empresas', [
                cnpj: '12345678000199',
                nome: 'Empresa Teste',
                email: 'empresa@example.com',
                pais: 'Brasil',
                estado: 'RJ',
                cep: '20000000',
                descricao: 'Cadastro duplicado'
        ])

        assertEquals(409, resposta.statusCode())
        String erro = interpretarJson(resposta).mensagem as String
        assertTrue(erro.contains('Ja existe'))
    }

    @Test
    void deveRetornar500ParaFalhaDePersistenciaNaoRelacionadaAUnicidade() {
        repositorio.falha = new SQLException('banco indisponivel', '08006')

        HttpResponse<String> resposta = enviarJson('POST', '/api/candidatos', [
                cpf: '12345678900',
                nome: 'Candidato Teste',
                email: 'candidato@example.com',
                pais: 'Brasil',
                estado: 'SP',
                cep: '01000000',
                descricao: 'Teste de falha',
                idade: 25
        ])

        assertEquals(500, resposta.statusCode())
        assertEquals('Falha ao persistir o cadastro.', interpretarJson(resposta).mensagem)
    }

    @Test
    void deveRetornar500ParaFalhaInesperadaNoRepositorio() {
        repositorio.falha = new IllegalStateException('erro inesperado')

        HttpResponse<String> resposta = enviarJson('POST', '/api/empresas', [
                cnpj: '12345678000199',
                nome: 'Empresa Teste',
                email: 'empresa@example.com',
                pais: 'Brasil',
                estado: 'RJ',
                cep: '20000000',
                descricao: 'Teste de falha'
        ])

        assertEquals(500, resposta.statusCode())
        assertEquals('Falha interna ao processar o cadastro.', interpretarJson(resposta).mensagem)
    }

    private HttpResponse<String> enviarJson(String metodo, String caminho, Map payload) {
        enviarTexto(metodo, caminho, JsonOutput.toJson(payload), 'application/json')
    }

    private HttpResponse<String> enviarTexto(
            String metodo,
            String caminho,
            String corpo,
            String contentType
    ) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uriBase.resolve(caminho))
        if (contentType != null) {
            builder.header('Content-Type', contentType)
        }
        HttpRequest request = builder.method(
                metodo,
                HttpRequest.BodyPublishers.ofString(corpo)
        ).build()
        cliente.send(request, HttpResponse.BodyHandlers.ofString())
    }

    private static Map interpretarJson(HttpResponse<String> resposta) {
        new JsonSlurper().parseText(resposta.body()) as Map
    }

    private static class RepositorioDeTeste implements RepositorioCadastro {
        Map<String, Object> candidato
        Map<String, Object> empresa
        Exception falha

        @Override
        void inserirCandidato(Map<String, Object> dados) {
            if (falha) {
                throw falha
            }
            candidato = dados
        }

        @Override
        void inserirEmpresa(Map<String, Object> dados) {
            if (falha) {
                throw falha
            }
            empresa = dados
        }
    }
}
