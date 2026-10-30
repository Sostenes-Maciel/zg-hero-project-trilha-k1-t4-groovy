package br.com.zg.aceleraspec.filter

import br.com.zg.acelera.filter.CorsFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import spock.lang.Specification

class CorsSpec extends Specification {


    CorsFilter filtroCors

    // Mocks do Tomcat
    HttpServletRequest requestMock
    HttpServletResponse responseMock
    FilterChain chainMock

    def setup() {
        filtroCors = new CorsFilter()
        requestMock = Mock(HttpServletRequest)
        responseMock = Mock(HttpServletResponse)
        chainMock = Mock(FilterChain)
    }

    def "deve interceptar requisicao OPTIONS, adicionar headers de CORS e retornar status 200 (Preflight)"() {
        given: "O navegador enviando uma requisição de checagem OPTIONS"
        requestMock.getMethod() >> "OPTIONS"

        when: "O filtro é executado"
        filtroCors.doFilter(requestMock, responseMock, chainMock)

        then: "Os headers de liberação devem ser injetados na resposta"
        1 * responseMock.setHeader("Access-Control-Allow-Origin", "*")
        1 * responseMock.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        1 * responseMock.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")

        and: "Deve retornar status 200 OK"
        1 * responseMock.setStatus(HttpServletResponse.SC_OK)

        and: "NÃO deve repassar a requisição para o Servlet (interrompe o fluxo)"
        0 * chainMock.doFilter(_, _)
    }

    def "deve adicionar headers de CORS e continuar o fluxo para outras requisicoes (POST, GET, etc)"() {
        given: "O navegador enviando um POST real (ex: Cadastro de Candidato)"
        requestMock.getMethod() >> "POST"

        when: "O filtro é executado"
        filtroCors.doFilter(requestMock, responseMock, chainMock)

        then: "Os headers de liberação devem ser injetados na resposta"
        1 * responseMock.setHeader("Access-Control-Allow-Origin", "*")
        1 * responseMock.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        1 * responseMock.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")

        and: "DEVE repassar a requisição para frente (para os Servlets tratarem)"
        1 * chainMock.doFilter(requestMock, responseMock)
    }
}