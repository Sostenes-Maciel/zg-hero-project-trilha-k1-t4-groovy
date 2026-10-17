package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.CadastrarCommand
import br.com.zg.acelera.service.GerenciadorDePerfisService
import spock.lang.Specification

class CadastrarCommandSpec extends Specification {

    def "deve chamar o metodo cadastroNovo do gerenciador ao executar o comando"() {
        given: "um mock do gerenciador e um scanner para injeção"
        def gerenciadorMock = Mock(GerenciadorDePerfisService)

        def scanner = new Scanner("")

        def comando = new CadastrarCommand(gerenciadorMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o mock do gerenciador deve ter o metodo 'cadastroNovo' chamado exatamente 1 vez"
        1 * gerenciadorMock.cadastroNovo(scanner)
    }
}