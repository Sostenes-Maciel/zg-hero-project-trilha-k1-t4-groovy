package br.com.zg.acelera.view

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import br.com.zg.acelera.controller.ControladorCadastro
import br.com.zg.acelera.model.CadastroInvalidoException
import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

class ManipuladorHttpCadastro implements HttpHandler {
    private static final Logger REGISTRADOR = Logger.getLogger(ManipuladorHttpCadastro.name)
    private static final int LIMITE_CORPO_BYTES = 1_048_576

    private final ControladorCadastro controlador
    private final String origemFrontend

    ManipuladorHttpCadastro(ControladorCadastro controlador, String origemFrontend) {
        this.controlador = controlador
        this.origemFrontend = origemFrontend
    }

    @Override
    void handle(HttpExchange exchange) {
        adicionarCabecalhosCors(exchange)
        try {
            if (exchange.requestMethod == 'OPTIONS') {
                exchange.responseHeaders.set('Allow', 'POST, OPTIONS')
                exchange.sendResponseHeaders(204, -1)
                return
            }
            if (exchange.requestMethod != 'POST') {
                exchange.responseHeaders.set('Allow', 'POST, OPTIONS')
                responder(exchange, 405, [mensagem: 'Metodo nao permitido.'])
                return
            }
            if (!exchange.requestHeaders.getFirst('Content-Type')?.toLowerCase()
                    ?.startsWith('application/json')) {
                responder(exchange, 415, [mensagem: 'Content-Type deve ser application/json.'])
                return
            }

            byte[] corpo = exchange.requestBody.readNBytes(LIMITE_CORPO_BYTES + 1)
            if (corpo.length > LIMITE_CORPO_BYTES) {
                responder(exchange, 413, [mensagem: 'Corpo da requisicao excede o limite de 1 MiB.'])
                return
            }

            Object dadosInterpretados = new JsonSlurper().parse(corpo)
            if (!(dadosInterpretados instanceof Map)) {
                responder(exchange, 400, [mensagem: 'O JSON deve ser um objeto.'])
                return
            }
            Map<String, Object> dados = (Map<String, Object>) dadosInterpretados

            Map<String, String> resultado
            switch (exchange.requestURI.path) {
                case '/api/candidatos':
                    resultado = controlador.cadastrarCandidato(dados)
                    break
                case '/api/empresas':
                    resultado = controlador.cadastrarEmpresa(dados)
                    break
                default:
                    responder(exchange, 404, [mensagem: 'Endpoint nao encontrado.'])
                    return
            }
            responder(exchange, 201, resultado)
        } catch (CadastroInvalidoException exception) {
            responder(exchange, 422, [mensagem: exception.message])
        } catch (JsonException ignored) {
            responder(exchange, 400, [mensagem: 'JSON invalido.'])
        } catch (SQLException exception) {
            if (exception.SQLState == '23505') {
                responder(exchange, 409, [mensagem: 'Ja existe um cadastro com esse identificador ou email.'])
            } else {
                REGISTRADOR.log(Level.SEVERE, 'Falha ao persistir cadastro no PostgreSQL.', exception)
                responder(exchange, 500, [mensagem: 'Falha ao persistir o cadastro.'])
            }
        } catch (Exception exception) {
            REGISTRADOR.log(Level.SEVERE, 'Falha inesperada ao processar cadastro.', exception)
            responder(exchange, 500, [mensagem: 'Falha interna ao processar o cadastro.'])
        } finally {
            exchange.close()
        }
    }

    private void adicionarCabecalhosCors(HttpExchange exchange) {
        String origem = exchange.requestHeaders.getFirst('Origin')
        if (origem == origemFrontend) {
            exchange.responseHeaders.set('Access-Control-Allow-Origin', origemFrontend)
            exchange.responseHeaders.set('Vary', 'Origin')
            exchange.responseHeaders.set('Access-Control-Allow-Methods', 'POST, OPTIONS')
            exchange.responseHeaders.set('Access-Control-Allow-Headers', 'Content-Type')
        }
    }

    private static void responder(HttpExchange exchange, int status, Map corpo) {
        byte[] resposta = JsonOutput.toJson(corpo).getBytes('UTF-8')
        exchange.responseHeaders.set('Content-Type', 'application/json; charset=utf-8')
        exchange.sendResponseHeaders(status, resposta.length)
        exchange.responseBody.withCloseable { it.write(resposta) }
    }
}
