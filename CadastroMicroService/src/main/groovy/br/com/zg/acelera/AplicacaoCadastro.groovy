package br.com.zg.acelera

import br.com.zg.acelera.controller.ControladorCadastro
import br.com.zg.acelera.model.RepositorioPostgresCadastro
import br.com.zg.acelera.model.ServicoCadastro
import br.com.zg.acelera.view.ManipuladorHttpCadastro

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import java.util.concurrent.Executors
import java.util.logging.Logger

class AplicacaoCadastro {
    private static final Logger REGISTRADOR = Logger.getLogger(AplicacaoCadastro.name)

    static void main(String[] args) {
        String urlBanco = System.getenv().getOrDefault(
                'DB_URL',
                'jdbc:postgresql://localhost:5432/LinkeTinder_BD'
        )
        String usuarioBanco = obterVariavelObrigatoria('DB_USER')
        String senhaBanco = obterVariavelObrigatoria('DB_PASSWORD')
        int porta = Integer.parseInt(System.getenv().getOrDefault('PORT', '8081'))
        String origemFrontend = System.getenv().getOrDefault(
                'FRONTEND_ORIGIN',
                'http://localhost:5173'
        )

        ServicoCadastro servico = new ServicoCadastro(
                new RepositorioPostgresCadastro(urlBanco, usuarioBanco, senhaBanco)
        )
        ControladorCadastro controlador = new ControladorCadastro(servico)
        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0)
        HttpHandler manipulador = new ManipuladorHttpCadastro(controlador, origemFrontend) as HttpHandler
        servidor.createContext('/', manipulador)
        servidor.executor = Executors.newFixedThreadPool(
                Math.max(2, Runtime.runtime.availableProcessors())
        )
        Runtime.runtime.addShutdownHook(new Thread({
            servidor.stop(1)
            REGISTRADOR.info('Microservico de cadastro encerrado.')
        }))

        servidor.start()
        REGISTRADOR.info("Microservico de cadastro disponivel na porta ${porta}.")
    }

    private static String obterVariavelObrigatoria(String nome) {
        String valor = System.getenv(nome)
        if (!valor) {
            throw new IllegalStateException("Variavel de ambiente obrigatoria ausente: ${nome}")
        }
        valor
    }
}
