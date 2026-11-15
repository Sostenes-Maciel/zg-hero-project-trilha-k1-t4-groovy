package br.com.zg.aceleraspec.service

import br.com.zg.acelera.interfaces.IEmpresaDAO
import br.com.zg.acelera.interfaces.IRecrutadorDAO
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.service.GerenciadorDeRecrutadoresService
import br.com.zg.acelera.service.ValidarRecrutadorService
import spock.lang.Specification

import java.time.LocalDate

class GerenciadorDeRecrutadoresServiceSpec extends Specification {

    IRecrutadorDAO recrutadorDAO
    IEmpresaDAO empresaDAO
    GerenciadorDeRecrutadoresService service

    def setup() {
        recrutadorDAO = Mock()
        empresaDAO = Mock()
        service = new GerenciadorDeRecrutadoresService(recrutadorDAO, empresaDAO)
    }

    def "cadastra recrutador somente quando a empresa existe e persiste senha com hash"() {
        given:
        Recrutador recrutador = valido()
        empresaDAO.buscarPorId("empresa-1") >> new Empresa(cnpj: "empresa-1")

        when:
        service.cadastrar(recrutador, "empresa-1")

        then:
        1 * recrutadorDAO.salvar({
            it.empresaId == "empresa-1" &&
                    it.cpf == "12345678900" &&
                    it.senhaHash != "123456" &&
                    it.senhaHash
        })
    }

    def "nega cadastro para empresa inexistente"() {
        given:
        empresaDAO.buscarPorId("inexistente") >> null

        when:
        service.cadastrar(valido(), "inexistente")

        then:
        IllegalArgumentException erro = thrown()
        erro.message == "Empresa não encontrada."
        0 * recrutadorDAO.salvar(_)
    }

    def "rejeita email inválido e senha fraca antes de acessar o DAO"() {
        given:
        empresaDAO.buscarPorId("empresa-1") >> new Empresa(cnpj: "empresa-1")
        Recrutador recrutador = valido()
        recrutador.email = "invalido"
        recrutador.senhaHash = "12345"

        when:
        service.cadastrar(recrutador, "empresa-1")

        then:
        thrown(IllegalArgumentException)
        0 * recrutadorDAO.salvar(_)
    }

    def "nega atualização de recrutador inexistente"() {
        given:
        empresaDAO.buscarPorId("empresa-1") >> new Empresa(cnpj: "empresa-1")
        recrutadorDAO.buscarPorId(99) >> null

        when:
        service.atualizar(valido(id: 99), "empresa-1")

        then:
        IllegalArgumentException erro = thrown()
        erro.message == "Recrutador não encontrado."
        0 * recrutadorDAO.atualizar(_)
    }

    def "inativa somente recrutador da empresa autenticada"() {
        given:
        recrutadorDAO.buscarPorId(10) >> new Recrutador(id: 10, empresaId: "empresa-1", ativo: true)
        recrutadorDAO.buscarPorId(11) >> new Recrutador(id: 11, empresaId: "outra", ativo: true)

        when:
        boolean permitido = service.inativar(10, "empresa-1")
        boolean negado = service.inativar(11, "empresa-1")

        then:
        permitido
        !negado
        1 * recrutadorDAO.inativar(10) >> true
        0 * recrutadorDAO.inativar(11)
    }

    def "nega autenticação de recrutador inativo ou senha inválida"() {
        given:
        String hash = ValidarRecrutadorService.gerarHashSenha("123456")
        recrutadorDAO.buscarPorEmail("r@empresa.com") >> new Recrutador(
                email: "r@empresa.com", ativo: ativo
        )
        recrutadorDAO.buscarHashPorEmail("r@empresa.com") >> hash

        when:
        Recrutador resultado = service.autenticar("r@empresa.com", senha)

        then:
        resultado == esperado

        where:
        ativo | senha     || esperado
        false | "123456"  || null
        true  | "654321"  || null
    }

    def "autentica recrutador ativo com senha correta"() {
        given:
        String hash = ValidarRecrutadorService.gerarHashSenha("123456")
        Recrutador salvo = new Recrutador(id: 5, email: "r@empresa.com", ativo: true)
        recrutadorDAO.buscarPorEmail("r@empresa.com") >> salvo
        recrutadorDAO.buscarHashPorEmail("r@empresa.com") >> hash

        expect:
        service.autenticar("r@empresa.com", "123456") == salvo
    }

    private static Recrutador valido(Map extras = [:]) {
        Recrutador recrutador = new Recrutador(
                nome: "Recrutador Teste", areaAtuacao: "TI", nivelPermissao: "Limitado",
                descricao: "Seleção", email: "recrutador@empresa.com",
                cpf: "123.456.789-00", dataNascimento: LocalDate.now().minusYears(25),
                senhaHash: "123456"
        )
        extras.each { chave, valor -> recrutador."$chave" = valor }
        recrutador
    }
}
