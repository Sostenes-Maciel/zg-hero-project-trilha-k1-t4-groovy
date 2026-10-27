package br.com.zg.aceleraspec.servlet

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.servlet.CandidatoServlet
import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import spock.lang.Specification

class CandidatoServletSpec extends Specification {

    CandidatoServlet servlet
    HttpServletRequest requestMock
    HttpServletResponse responseMock
    CandidatoController controllerMock
    StringWriter stringWriter
    PrintWriter printWriter

    def setup() {
        requestMock = Mock(HttpServletRequest)
        responseMock = Mock(HttpServletResponse)
        controllerMock = Mock(CandidatoController)

        stringWriter = new StringWriter()
        printWriter = new PrintWriter(stringWriter)

        servlet = new CandidatoServlet()
        servlet.candidatoController = controllerMock
        servlet.gson = new Gson()
    }

    def "deve processar cadastro de candidato com sucesso e retornar status 201"() {
        given: "Um JSON válido vindo da requisição"
        String jsonRequest = '{"nome":"Douglas", "cpf":"09876543211", "email":"douglas.dev@email.com", "pais":"Brasil", "idade":24}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when: "O método doPost do Servlet é chamado"
        servlet.doPost(requestMock, responseMock)

        then: "Deve ler o corpo da requisição"
        1 * requestMock.getReader() >> reader

        and: "Deve repassar para o controller e receber um sucesso"
        1 * controllerMock.cadastrar(_) >> [status: 201, mensagem: "Candidato cadastrado com sucesso!"]

        and: "Deve configurar o Response corretamente"
        1 * responseMock.setContentType("application/json")
        1 * responseMock.setCharacterEncoding("UTF-8")
        1 * responseMock.setStatus(201)
        1 * responseMock.getWriter() >> printWriter

        and: "A resposta final deve conter a mensagem esperada"
        stringWriter.toString().contains("Candidato cadastrado com sucesso!")
    }
}