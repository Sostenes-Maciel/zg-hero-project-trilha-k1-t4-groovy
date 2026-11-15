package br.com.zg.aceleraspec.servlet

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.servlet.CandidatoServlet
import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import spock.lang.Specification

class CandidatoServletSpec extends Specification {

    CandidatoServlet servlet
    HttpServletRequest requestMock
    HttpServletResponse responseMock
    CandidatoController controllerMock
    HttpSession sessionMock
    StringWriter stringWriter
    PrintWriter printWriter

    def setup() {
        requestMock = Mock(HttpServletRequest)
        responseMock = Mock(HttpServletResponse)
        controllerMock = Mock(CandidatoController)
        sessionMock = Mock(HttpSession)

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
        1 * requestMock.getServletPath() >> "/api/candidatos"
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

    def "deve retornar status 400 quando os dados do candidato falharem na validacao"() {
        given: "Um JSON com CPF incompleto vindo da requisição"
        String jsonRequest = '{"nome":"Douglas", "cpf":"123"}' // CPF inválido
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when: "O Servlet processa a requisição"
        servlet.doPost(requestMock, responseMock)

        then: "Deve ler o corpo e repassar ao controller"
        1 * requestMock.getServletPath() >> "/api/candidatos"
        1 * requestMock.getReader() >> reader

        and: "O controller barra a operação retornando status 400"
        1 * controllerMock.cadastrar(_) >> [status: 400, mensagem: "Erro de validação: CPF deve conter 11 números."]

        and: "O Servlet deve configurar o response com HTTP 400 Bad Request"
        1 * responseMock.setContentType("application/json")
        1 * responseMock.setCharacterEncoding("UTF-8")
        1 * responseMock.setStatus(400)
        1 * responseMock.getWriter() >> printWriter

        and: "A mensagem de erro deve estar no JSON de resposta"
        stringWriter.toString().contains("Erro de validação: CPF deve conter 11 números.")
    }

    def "deve processar login e retornar 200"() {
        given: "JSON com credenciais"
        String jsonRequest = '{"email":"test@test.com", "cpf":"12345678900"}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when:
        servlet.doPost(requestMock, responseMock)

        then:
        1 * requestMock.getServletPath() >> "/api/candidatos/login"
        1 * requestMock.getReader() >> reader
        1 * controllerMock.login("test@test.com", "12345678900") >> [status: 200, mensagem: "Login bem sucedido"]
        1 * responseMock.setStatus(200)
        1 * responseMock.getWriter() >> printWriter
        stringWriter.toString().contains("Login bem sucedido")
    }


    def "deve retornar status 500 quando ocorrer um erro interno no servidor"() {
        given: "Um JSON válido"
        String jsonRequest = '{"nome":"Douglas", "cpf":"09876543211"}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when: "O Servlet processa a requisição"
        servlet.doPost(requestMock, responseMock)

        then:
        1 * requestMock.getServletPath() >> "/api/candidatos"
        1 * requestMock.getReader() >> reader

        and: "O controller simula uma falha grave (ex: banco fora do ar) retornando 500"
        1 * controllerMock.cadastrar(_) >> [status: 500, mensagem: "Erro interno: Falha de conexão"]

        and: "O Servlet deve devolver HTTP 500 Internal Server Error"
        1 * responseMock.setStatus(500)
        1 * responseMock.getWriter() >> printWriter
        stringWriter.toString().contains("Erro interno: Falha de conexão")
    }

    // GET

    def "deve retornar 200 e listar todos os candidatos anonimizados quando não passar CPF no GET (sem recrutador logado)"() {
        when: "O doGet é chamado sem parâmetro"
        servlet.doGet(requestMock, responseMock)

        then: "Deve extrair o parâmetro cpf (que será nulo)"
        1 * requestMock.getParameter("cpf") >> null
        1 * requestMock.getSession(false) >> null

        and: "Deve chamar listar no controller"
        // O controller realiza a listagem padrão (sem filtro por recrutador)
        1 * controllerMock.listar() >> [status: 200, dados: [[nome: "Douglas", cpf: "12345678900", competencias: ["Java"]]]]

        and: "Deve configurar a resposta 200"
        1 * responseMock.setContentType("application/json")
        1 * responseMock.setCharacterEncoding("UTF-8")
        1 * responseMock.setStatus(200)
        _ * responseMock.getWriter() >> printWriter

        and: "A resposta deve ter o envelope {status: 200, dados: [...]} e ser anonimizada"
        def jsonResult = stringWriter.toString()
        jsonResult.contains('"status":200')
        jsonResult.contains('"dados":[')
        // Verifica a anonimização implementada no Servlet
        jsonResult.contains("Candidato Oculto")
        jsonResult.contains("12345678900")
        jsonResult.contains("Java")
        !jsonResult.contains("Douglas")
    }

    def "deve retornar 200 e listar candidatos filtrados anonimizados quando houver recrutador logado"() {
        given:
        Recrutador recrutadorFake = new Recrutador()

        when: "O doGet é chamado sem CPF, mas com recrutador na sessão"
        servlet.doGet(requestMock, responseMock)

        then:
        1 * requestMock.getParameter("cpf") >> null
        1 * requestMock.getSession(false) >> sessionMock
        1 * sessionMock.getAttribute("recrutador") >> recrutadorFake

        and: "Chama a sobrecarga de listar passando o recrutador"
        1 * controllerMock.listar(recrutadorFake) >> [status: 200, dados: [[nome: "Maria", cpf: "11122233344", estado: "PE"]]]

        and:
        1 * responseMock.setStatus(200)
        _ * responseMock.getWriter() >> printWriter

        and: "Verifica envelope e anonimização"
        def jsonResult = stringWriter.toString()
        jsonResult.contains("Candidato Oculto")
        jsonResult.contains("11122233344")
        jsonResult.contains("PE")
        !jsonResult.contains("Maria")
    }

    def "deve retornar 200 e os dados do candidato anonimizados quando passar CPF no GET"() {
        when: "O doGet é chamado com um CPF"
        servlet.doGet(requestMock, responseMock)

        then: "Retorna o CPF da requisição"
        1 * requestMock.getParameter("cpf") >> "12345678900"

        and: "Chama a busca especifica no controller"
        1 * controllerMock.buscarPorCpf("12345678900") >> [status: 200, dados: [nome: "Sandubinha", cpf: "12345678900"]]

        and: "Retorna 200 OK"
        1 * responseMock.setStatus(200)
        1 * responseMock.getWriter() >> printWriter

        and: "Verifica se anonimizou um candidato específico"
        def jsonResult = stringWriter.toString()
        jsonResult.contains("Candidato Oculto")
        !jsonResult.contains("Sandubinha")
    }

    def "deve retornar 404 quando o CPF não for encontrado no GET"() {
        when: "Executamos o método HTTP"
        servlet.doGet(requestMock, responseMock)

        then: "O parâmetro CPF é fornecido"
        1 * requestMock.getParameter("cpf") >> "00000000000"
        1 * controllerMock.buscarPorCpf("00000000000") >> null // Simula não encontrar

        and: "Garantimos a resposta com status 404"
        1 * responseMock.setStatus(404)
        _ * responseMock.setContentType(_)
        _ * responseMock.setCharacterEncoding(_)
        _ * responseMock.getWriter() >> printWriter

        and: "Validamos a saída de erro padrão"
        stringWriter.toString().contains("Candidato não encontrado")
    }

    // PUT

    def "deve retornar 200 ao atualizar candidato com sucesso no PUT"() {
        given: "Um JSON válido com os novos dados"
        String jsonRequest = '{"nome":"Douglas Atualizado", "cpf":"12345678900", "idade":30}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when:
        servlet.doPut(requestMock, responseMock)

        then:
        1 * requestMock.getReader() >> reader
        1 * controllerMock.atualizar(_) >> [status: 200, mensagem: "Candidato atualizado com sucesso!"]

        and:
        1 * responseMock.setStatus(200)
        1 * responseMock.getWriter() >> printWriter
        stringWriter.toString().contains("Candidato atualizado com sucesso!")
    }

    def "deve retornar 400 quando falhar a validação na atualização do PUT"() {
        given: "JSON com idade inválida (ex: menor de idade)"
        String jsonRequest = '{"nome":"Douglas", "cpf":"12345678900", "idade":15}'
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest))

        when:
        servlet.doPut(requestMock, responseMock)

        then:
        1 * requestMock.getReader() >> reader
        1 * controllerMock.atualizar(_) >> [status: 400, mensagem: "Erro de validação: O candidato deve ser maior de idade."]

        and:
        1 * responseMock.setStatus(400)
        1 * responseMock.getWriter() >> printWriter
        stringWriter.toString().contains("O candidato deve ser maior de idade")
    }

    //DELETE

    def "deve retornar 200 ao excluir candidato passando CPF no DELETE"() {
        when:
        servlet.doDelete(requestMock, responseMock)

        then: "Pega o parâmetro cpf da URL"
        1 * requestMock.getParameter("cpf") >> "12345678900"

        and: "Controller realiza a exclusão"
        1 * controllerMock.excluir("12345678900") >> [status: 200, mensagem: "Candidato excluído com sucesso!"]

        and:
        1 * responseMock.setStatus(200)
        1 * responseMock.getWriter() >> printWriter
        stringWriter.toString().contains("Candidato excluído com sucesso!")
    }

    def "deve retornar 400 se o usuário tentar dar DELETE sem informar o CPF na URL"() {
        when: "Tenta deletar sem passar CPF"
        servlet.doDelete(requestMock, responseMock)

        then: "Retorna nulo ou vazio no getParameter"
        1 * requestMock.getParameter("cpf") >> ""

        and: "O Servlet deve travar a requisição antes mesmo de chamar o Controller!"
        0 * controllerMock.excluir(_)

        and: "Deve retornar 400"
        1 * responseMock.setStatus(400)
        1 * responseMock.getWriter() >> printWriter
        stringWriter.toString().contains("O CPF é obrigatório para exclusão")
    }
}