package br.com.zg.aceleraspec.servlet

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.servlet.EmpresaServlet
import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import spock.lang.Specification

class EmpresaServletSpec extends Specification {

    EmpresaServlet servlet
    HttpServletRequest requestMock
    HttpServletResponse responseMock
    EmpresaController controllerMock
    StringWriter stringWriter
    PrintWriter printWriter

    def setup() {
        requestMock = Mock(HttpServletRequest)
        responseMock = Mock(HttpServletResponse)
        controllerMock = Mock(EmpresaController)

        stringWriter = new StringWriter()
        printWriter = new PrintWriter(stringWriter)

        servlet = new EmpresaServlet()
        servlet.empresaController = controllerMock
        servlet.gson = new Gson()
    }

    def "deve processar cadastro de empresa com sucesso e retornar status 201"() {
        given: "Um JSON válido vindo da requisição"
        String jsonRequest = '{"nome":"Veritas IT Solutions", "cnpj":"98765432000188", "email":"recrutamento@veritasit.com.br", "pais":"Brasil"}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when: "O método doPost do Servlet é chamado"
        servlet.doPost(requestMock, responseMock)

        then: "Deve ler o corpo da requisição"
        1 * requestMock.getReader() >> reader

        and: "Deve repassar para o controller e receber um sucesso"
        1 * controllerMock.cadastrar(_) >> [status: 201, mensagem: "Empresa cadastrada com sucesso!"]

        and: "Deve configurar o Response corretamente"
        1 * responseMock.setContentType("application/json")
        1 * responseMock.setCharacterEncoding("UTF-8")
        1 * responseMock.setStatus(201)
        1 * responseMock.getWriter() >> printWriter

        and: "A resposta final deve conter a mensagem esperada"
        stringWriter.toString().contains("Empresa cadastrada com sucesso!")
    }
    def "deve retornar status 400 quando o CNPJ da empresa for invalido"() {
        given: "Um JSON com CNPJ inválido"
        String jsonRequest = '{"nome":"Veritas", "cnpj":"000"}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when:
        servlet.doPost(requestMock, responseMock)

        then:
        1 * requestMock.getReader() >> reader
        1 * controllerMock.cadastrar(_) >> [status: 400, mensagem: "Erro de validação: CNPJ deve conter 14 números."]

        and: "Deve retornar 400 Bad Request"
        1 * responseMock.setContentType("application/json")
        1 * responseMock.setCharacterEncoding("UTF-8")
        1 * responseMock.setStatus(400)
        1 * responseMock.getWriter() >> printWriter
        stringWriter.toString().contains("CNPJ deve conter 14 números.")
    }

    def "deve capturar excecao nao tratada no Servlet e retornar 500"() {
        given: "O leitor da requisição configurado para dar erro"

        when: "O doPost é chamado"
        servlet.doPost(requestMock, responseMock)

        then: "Quando o Servlet tentar ler, disparamos uma exceção violenta"
        1 * requestMock.getReader() >> { throw new RuntimeException("Conexão de rede interrompida") }

        and: "O catch do Servlet deve assumir o controle e retornar status 500"
        1 * responseMock.setContentType("application/json")
        1 * responseMock.setCharacterEncoding("UTF-8")
        1 * responseMock.setStatus(500)
        1 * responseMock.getWriter() >> printWriter

        and: "A resposta devolve a mensagem formatada pelo catch do próprio Servlet"
        stringWriter.toString().contains("Erro interno no Servlet: Conexão de rede interrompida")
    }
    // --- TESTES DO GET ---
    def "deve retornar 200 e listar todas as empresas quando não passar CNPJ"() {
        when:
        servlet.doGet(requestMock, responseMock)

        then:
        1 * requestMock.getParameter("cnpj") >> null
        1 * controllerMock.listar() >> [status: 200, dados: [[nome: "Veritas", cnpj: "12345678901234"]]]

        and:
        1 * responseMock.setStatus(200)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Veritas")
    }

    def "deve retornar 200 e a empresa quando passar CNPJ valido no GET"() {
        when:
        servlet.doGet(requestMock, responseMock)

        then:
        1 * requestMock.getParameter(_) >> "12345678901234"
        1 * controllerMock.buscarPorCnpj(_) >> [status: 200, dados: [nome: "ZG Solucoes"]]

        and:
        1 * responseMock.setStatus(200)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("ZG Solucoes")
    }

    def "deve retornar 404 quando o CNPJ não for encontrado no GET"() {
        when:
        servlet.doGet(requestMock, responseMock)

        then:
        1 * requestMock.getParameter(_) >> "00000000000000"
        1 * controllerMock.buscarPorCnpj(_) >> null

        and:
        1 * responseMock.setStatus(404)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Empresa não encontrada")
    }

    // --- TESTES DO PUT ---
    def "deve retornar 200 ao atualizar empresa com sucesso no PUT"() {
        given:
        String jsonRequest = '{"nome":"Veritas Atualizada", "cnpj":"12345678901234", "pais":"Brasil"}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when:
        servlet.doPut(requestMock, responseMock)

        then:
        1 * requestMock.getReader() >> reader
        1 * controllerMock.atualizar(_) >> [status: 200, mensagem: "Empresa atualizada com sucesso!"]

        and:
        1 * responseMock.setStatus(200)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Empresa atualizada com sucesso!")
    }

    // --- TESTES DO DELETE ---
    def "deve retornar 200 ao excluir empresa informando CNPJ no DELETE"() {
        when:
        servlet.doDelete(requestMock, responseMock)

        then:
        1 * requestMock.getParameter(_) >> "12345678901234"
        1 * controllerMock.excluir(_) >> [status: 200, mensagem: "Empresa excluída com sucesso!"]

        and:
        1 * responseMock.setStatus(200)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Empresa excluída com sucesso!")
    }

    def "deve retornar 400 se tentar deletar sem informar CNPJ na URL"() {
        when:
        servlet.doDelete(requestMock, responseMock)

        then:
        1 * requestMock.getParameter("cnpj") >> null
        0 * controllerMock.excluir(_)

        and:
        1 * responseMock.setStatus(400)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("O CNPJ é obrigatório para exclusão")
    }
}