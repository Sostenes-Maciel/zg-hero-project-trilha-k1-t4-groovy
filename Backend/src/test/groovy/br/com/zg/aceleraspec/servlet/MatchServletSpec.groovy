package br.com.zg.aceleraspec.servlet

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.servlet.MatchServlet
import com.google.gson.Gson
import spock.lang.Specification
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class MatchServletSpec extends Specification {

    HttpServletRequest req = Mock()
    HttpServletResponse resp = Mock()
    MatchController matchController = Mock()

    MatchServlet servlet = new MatchServlet()

    StringWriter stringWriter = new StringWriter()
    PrintWriter printWriter = new PrintWriter(stringWriter)

    def setup() {
        servlet.matchController = matchController
        servlet.gson = new Gson()

        resp.getWriter() >> printWriter
    }

    def "doPost - Deve processar apenas a curtida da CANDIDATO quando não houver Match (EMPRESA ainda não curtiu)"() {
        given: "Um JSON válido vindo do Postman"
        String jsonPostman = '{"autor": "CANDIDATO", "cpfCandidato": "11122233394", "vaga": {"id": 1}}'

        and: "Simulamos o leitor da requisição lendo esse JSON"
        req.getReader() >> new BufferedReader(new StringReader(jsonPostman))

        and: "Simulamos o Controller retornando sucesso (201)"
        matchController.curtirVaga("11122233394", _) >> [status: 201, mensagem: "Vaga curtida com sucesso!"]

        when: "O Servlet recebe o POST"
        servlet.doPost(req, resp)

        then: "Ele deve configurar os cabeçalhos de resposta (incluindo o UTF-8 que arrumamos!)"
        1 * resp.setContentType("application/json")
        1 * resp.setCharacterEncoding("UTF-8")

        and: "Deve setar o status HTTP para 201"
        1 * resp.setStatus(201)

        and: "O JSON devolvido deve conter a mensagem de sucesso"
        stringWriter.toString().contains("Vaga curtida com sucesso!")
    }

    def "doPost - Deve retornar erro 400 se o autor for inválido"() {
        given: "Um JSON com um autor não reconhecido"
        String jsonInvalido = '{"autor": "HACKER", "cpfCandidato": "11122233394"}'
        req.getReader() >> new BufferedReader(new StringReader(jsonInvalido))

        when: "O Servlet recebe o POST"
        servlet.doPost(req, resp)

        then: "Ele não deve chamar NADA no Controller"
        0 * matchController._

        and: "Deve retornar Status 400 avisando do erro"
        1 * resp.setStatus(400)
        stringWriter.toString().contains("Autor inválido")
    }

    def "doGet - Deve buscar os matches do Candidato quando o CPF for passado na URL"() {
        given: "O parâmetro ?cpf= na URL e nenhum ?cnpj="
        req.getParameter("cpf") >> "11122233394"
        req.getParameter("cnpj") >> null

        and: "O Controller retornando uma lista de matches simulada"
        matchController.listarMatchesCandidato("11122233394") >> [status: 200, dados: ["match1", "match2"]]

        when: "O Servlet recebe o GET"
        servlet.doGet(req, resp)

        then: "Deve setar o status 200"
        1 * resp.setStatus(200)

        and: "Deve devolver os dados no JSON"
        stringWriter.toString().contains("match1")
    }
    def "doPost - Deve processar apenas a curtida da EMPRESA quando não houver Match (Candidato ainda não curtiu)"() {
        given: "Um JSON da Empresa curtindo o Candidato"
        String jsonPostman = '{"autor": "EMPRESA", "cnpjEmpresa": "12345678901234", "cpfCandidato": "11122233394", "idVaga": 1}'
        req.getReader() >> new BufferedReader(new StringReader(jsonPostman))

        and: "Simulamos o Controller avisando que foi APENAS uma curtida avulsa (match: false)"
        matchController.curtirCandidato("12345678901234", "11122233394", 1) >> [status: 201, mensagem: "Candidato curtido com sucesso!", match: false]

        when: "O Servlet recebe o POST"
        servlet.doPost(req, resp)

        then: "O status 201 é retornado com a mensagem de curtida simples"
        1 * resp.setStatus(201)
        stringWriter.toString().contains("Candidato curtido com sucesso!")
        stringWriter.toString().contains('"match":false')
    }

    def "doPost - Deve retornar MATCH quando o CANDIDATO for o gatilho (Empresa já havia curtido antes)"() {
        given: "Um JSON do Candidato curtindo a Vaga"
        String jsonPostman = '{"autor": "CANDIDATO", "cpfCandidato": "11122233394", "vaga": {"id": 1}}'
        req.getReader() >> new BufferedReader(new StringReader(jsonPostman))

        and: "Simulamos o Controller avisando que essa curtida disparou o MATCH (match: true)"
        matchController.curtirVaga("11122233394", _) >> [status: 201, mensagem: "MATCH ENCONTRADO!", match: true]

        when: "O Servlet recebe o POST"
        servlet.doPost(req, resp)

        then: "O status 201 é retornado com o alerta de Match"
        1 * resp.setStatus(201)
        stringWriter.toString().contains("MATCH ENCONTRADO!")
        stringWriter.toString().contains('"match":true')
    }

    def "doPost - Deve retornar MATCH quando a EMPRESA for o gatilho (Candidato já havia curtido antes)"() {
        given: "Um JSON da Empresa curtindo o Candidato"
        String jsonPostman = '{"autor": "EMPRESA", "cnpjEmpresa": "12345678901234", "cpfCandidato": "11122233394", "idVaga": 1}'
        req.getReader() >> new BufferedReader(new StringReader(jsonPostman))

        and: "Simulamos o Controller avisando que essa curtida disparou o MATCH (match: true)"
        matchController.curtirCandidato("12345678901234", "11122233394", 1) >> [status: 201, mensagem: "MATCH ENCONTRADO!", match: true]

        when: "O Servlet recebe o POST"
        servlet.doPost(req, resp)

        then: "O status 201 é retornado com o alerta de Match"
        1 * resp.setStatus(201)
        stringWriter.toString().contains("MATCH ENCONTRADO!")
        stringWriter.toString().contains('"match":true')
    }
}