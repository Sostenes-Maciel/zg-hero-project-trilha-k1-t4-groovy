package br.com.zg.aceleraspec.servlet

import br.com.zg.acelera.controller.RecrutadorController
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.servlet.RecrutadorServlet
import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import java.time.LocalDate
import spock.lang.Specification

class RecrutadorServletSpec extends Specification {

    def servlet = new RecrutadorServlet()
    def controller = Mock(RecrutadorController)
    def request = Mock(HttpServletRequest)
    def response = Mock(HttpServletResponse)
    def session = Mock(HttpSession)
    def output = new StringWriter()

    def setup() {
        servlet.recrutadorController = controller
        servlet.gson = new Gson()
        response.getWriter() >> new PrintWriter(output)
    }

    def "retorna 401 no login inválido"() {
        given:
        request.getServletPath() >> "/api/recrutadores/login"
        request.getReader() >> new BufferedReader(new StringReader(
                '{"email":"recrutador@empresa.com","senha":"123456"}'))
        controller.login("recrutador@empresa.com", "123456") >>
                [status: 401, mensagem: "E-mail ou senha incorretos."]

        when:
        servlet.doPost(request, response)

        then:
        1 * response.setStatus(401)
        output.toString().contains("E-mail ou senha")
    }

    def "retorna 200 no login e remove dados sensíveis do payload"() {
        given:
        Recrutador autenticado = new Recrutador(
                id: 8, empresaId: "empresa-1", nome: "Ana", areaAtuacao: "TI",
                nivelPermissao: "Total", email: "ana@empresa.com",
                cpf: "12345678900", dataNascimento: LocalDate.of(1990, 1, 1),
                senhaHash: "hash-secreto", ativo: true
        )
        request.getServletPath() >> "/api/recrutadores/login"
        request.getReader() >> new BufferedReader(new StringReader(
                '{"email":"ana@empresa.com","senha":"123456"}'))
        request.getSession(true) >> session
        controller.login("ana@empresa.com", "123456") >>
                [status: 200, recrutador: autenticado]

        when:
        servlet.doPost(request, response)

        then:
        1 * session.setAttribute("recrutador", autenticado)
        1 * response.setStatus(200)
        output.toString().contains('"nome":"Ana"')
        !output.toString().contains("12345678900")
        !output.toString().contains("1990-01-01")
        !output.toString().contains("hash-secreto")
        !output.toString().contains("senhaHash")
    }

    def "retorna 201 ao cadastrar recrutador"() {
        given:
        request.getServletPath() >> "/api/recrutadores"
        request.getReader() >> new BufferedReader(new StringReader('''
            {"empresaId":"empresa-1","nome":"Ana","areaAtuacao":"TI",
             "nivelPermissao":"Limitado","email":"ana@empresa.com",
             "cpf":"123.456.789-00","dataNascimento":"1990-01-01","senha":"123456"}
        '''))
        controller.cadastrar(_, "empresa-1") >> [status: 201, id: 9]

        when:
        servlet.doPost(request, response)

        then:
        1 * response.setStatus(201)
        output.toString().contains('"id":9')
    }

    def "retorna 400 para JSON inválido"() {
        given:
        request.getServletPath() >> "/api/recrutadores"
        request.getReader() >> new BufferedReader(new StringReader("{"))

        when:
        servlet.doPost(request, response)

        then:
        1 * response.setStatus(400)
        output.toString().contains("Requisição inválida")
    }

    def "propaga 403 quando o serviço nega a operação"() {
        given:
        request.getServletPath() >> "/api/recrutadores"
        request.getReader() >> new BufferedReader(new StringReader(
                '{"empresaId":"empresa-1","nome":"Ana","areaAtuacao":"TI","nivelPermissao":"Limitado","cpf":"12345678900","dataNascimento":"1990-01-01","senha":"123456"}'))
        controller.cadastrar(_, "empresa-1") >> [status: 403, mensagem: "Acesso negado."]

        when:
        servlet.doPost(request, response)

        then:
        1 * response.setStatus(403)
        output.toString().contains("Acesso negado")
    }

    def "propaga 409 para e-mail ou CPF duplicado"() {
        given:
        request.getServletPath() >> "/api/recrutadores"
        request.getReader() >> new BufferedReader(new StringReader(
                '{"empresaId":"empresa-1","nome":"Ana","areaAtuacao":"TI","nivelPermissao":"Limitado","cpf":"12345678900","dataNascimento":"1990-01-01","senha":"123456"}'))
        controller.cadastrar(_, "empresa-1") >> [status: 409, mensagem: "E-mail ou CPF já cadastrado."]

        when:
        servlet.doPost(request, response)

        then:
        1 * response.setStatus(409)
        output.toString().contains("já cadastrado")
    }

    def "retorna 200 ao inativar recrutador"() {
        given:
        request.getParameter("id") >> "10"
        request.getParameter("empresaId") >> "empresa-1"
        controller.inativar(10, "empresa-1") >>
                [status: 200, mensagem: "Recrutador inativado com sucesso."]

        when:
        servlet.doDelete(request, response)

        then:
        1 * response.setStatus(200)
        output.toString().contains("inativado")
    }

    def "retorna 400 quando inativação não recebe identificadores"() {
        given:
        request.getParameter("id") >> null
        request.getParameter("empresaId") >> null

        when:
        servlet.doDelete(request, response)

        then:
        1 * response.setStatus(400)
        output.toString().contains("obrigatórios")
    }
}
