package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarVagasCommand
import br.com.zg.acelera.service.GerenciadorDePerfisService
import spock.lang.Specification
import java.util.Scanner

class GerenciarVagasCommandSpec extends Specification {

    def "deve chamar o metodo gerenciarVagas do gerenciador ao executar o comando"() {
        given: "um mock do gerenciador e um scanner para injecao"
        def gerenciadorMock = Mock(GerenciadorDePerfisService)
        def scanner = new Scanner("")

        def comando = new GerenciarVagasCommand(gerenciadorMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o mock do gerenciador deve ter o metodo 'gerenciarVagas' chamado exatamente 1 vez"
        1 * gerenciadorMock.gerenciarVagas(scanner)
    }
}