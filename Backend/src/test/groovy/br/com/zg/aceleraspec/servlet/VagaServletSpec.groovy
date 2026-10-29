package br.com.zg.aceleraspec.servlet

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.servlet.VagaServlet
import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import spock.lang.Specification

class VagaServletSpec extends Specification {

    VagaServlet servlet
    HttpServletRequest requestMock
    HttpServletResponse responseMock
    VagaController controllerMock
    StringWriter stringWriter
    PrintWriter printWriter

    def setup() {
        servlet = new VagaServlet()
        requestMock = Mock(HttpServletRequest)
        responseMock = Mock(HttpServletResponse)
        controllerMock = Mock(VagaController)

        stringWriter = new StringWriter()
        printWriter = new PrintWriter(stringWriter)

        servlet.vagaController = controllerMock
        servlet.gson = new Gson()
    }

    // --- TESTES DO POST ---
    def "deve retornar 201 ao cadastrar vaga com sucesso"() {
        given: "Um JSON válido contendo a empresa aninhada"
        String jsonRequest = '{"titulo":"Dev Pleno", "descricao":"Vaga Groovy", "pais":"Brasil", "estado":"GO", "empresa":{"cnpj":"12345678901234"}}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        _ * requestMock.getReader() >> reader
        _ * controllerMock.cadastrar(_) >> [status: 201, mensagem: "Vaga cadastrada com sucesso!", id: 1]

        when:
        servlet.doPost(requestMock, responseMock)

        then:
        1 * responseMock.setStatus(201)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Vaga cadastrada")
    }
    def "deve retornar 400 ao tentar cadastrar vaga com dados invalidos no POST"() {
        given: "Um JSON inválido (ex: sem título ou sem empresa)"
        String jsonRequest = '{"descricao":"Faltou o titulo"}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        _ * requestMock.getReader() >> reader
        _ * controllerMock.cadastrar(_) >> [status: 400, mensagem: "Erro de validação: O título é obrigatório."]

        when:
        servlet.doPost(requestMock, responseMock)

        then:
        1 * responseMock.setStatus(400)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("O título é obrigatório")
    }

    // --- TESTES DO GET ---
    def "deve retornar 200 e listar todas as vagas quando não passar CNPJ"() {
        given:
        _ * requestMock.getParameter("cnpj") >> null
        _ * controllerMock.listar() >> [status: 200, dados: [[titulo: "Dev Groovy", id: 1]]]

        when:
        servlet.doGet(requestMock, responseMock)

        then:
        1 * responseMock.setStatus(200)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Dev Groovy")
    }

    def "deve retornar 200 e listar vagas da empresa quando passar CNPJ"() {
        given: "Ensinamos ao Mock o que ele deve ter na URL e preparamos o Writer"
        requestMock.getParameter("cnpj") >> "12345678901234"
        requestMock.getParameter("cpf") >> null
        requestMock.getParameter("id") >> null

        responseMock.getWriter() >> printWriter

        and: "Simulamos o retorno do Controller"
        controllerMock.listarDaEmpresa("12345678901234") >> [status: 200, dados: [[titulo: "Dev Backend", empresa: [cnpj: "12345678901234"]]]]

        when: "O Servlet recebe o GET"
        servlet.doGet(requestMock, responseMock)

        then: "O status 200 deve ser setado exatamente 1 vez"
        1 * responseMock.setStatus(200)

        and: "O JSON resultante deve conter a vaga"
        stringWriter.toString().contains("Dev Backend")
    }

    // --- TESTES DO PUT ---
    def "deve retornar 200 ao atualizar vaga com sucesso"() {
        given:
        String jsonRequest = '{"id": 1, "titulo":"Dev Senior", "pais":"Brasil", "empresa":{"cnpj":"12345678901234"}}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        _ * requestMock.getReader() >> reader
        _ * controllerMock.atualizar(_) >> [status: 200, mensagem: "Vaga atualizada com sucesso!"]

        when:
        servlet.doPut(requestMock, responseMock)

        then:
        1 * responseMock.setStatus(200)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Vaga atualizada com sucesso!")
    }
    def "deve retornar 400 ao tentar atualizar vaga com regra de negocio violada no PUT"() {
        given: "Um JSON tentando atualizar algo inválido"
        String jsonRequest = '{"id": 1, "titulo":""}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        _ * requestMock.getReader() >> reader
        _ * controllerMock.atualizar(_) >> [status: 400, mensagem: "Erro de validação: Título não pode ser vazio."]

        when:
        servlet.doPut(requestMock, responseMock)

        then:
        1 * responseMock.setStatus(400)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Título não pode ser vazio")
    }

    // --- TESTES DO DELETE ---
    def "deve retornar 200 ao excluir vaga passando ID"() {
        given:
        _ * requestMock.getParameter(_) >> "1"
        _ * controllerMock.excluir(1) >> [status: 200, mensagem: "Vaga excluída com sucesso!"]

        when:
        servlet.doDelete(requestMock, responseMock)

        then:
        1 * responseMock.setStatus(200)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("Vaga excluída")
    }

    def "deve retornar 400 se tentar deletar vaga sem passar ID"() {
        given:
        _ * requestMock.getParameter("id") >> null
        0 * controllerMock.excluir(_)

        when:
        servlet.doDelete(requestMock, responseMock)

        then:
        1 * responseMock.setStatus(400)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("O ID da vaga é obrigatório")
    }
    def "deve retornar 400 se tentar deletar vaga passando um ID com letras no DELETE"() {
        given: "O usuário passa letras em vez de número na URL"
        _ * requestMock.getParameter(_) >> "abc"

        0 * controllerMock.excluir(_)

        when:
        servlet.doDelete(requestMock, responseMock)

        then: "O Servlet deve capturar o NumberFormatException e retornar 400"
        1 * responseMock.setStatus(400)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        stringWriter.toString().contains("O ID da vaga deve ser um número válido")
    }
}