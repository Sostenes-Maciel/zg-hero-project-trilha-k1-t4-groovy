package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarCandidatosCommand
import br.com.zg.acelera.view.MenuView
import spock.lang.Specification

class GerenciarCandidatosCommandSpec extends Specification {

    def "deve chamar o metodo gerenciarCandidatos da view ao executar o comando"() {
        given: "um mock da MenuView e um scanner para injecao"
        def menuViewMock = Mock(MenuView)
        def scanner = new Scanner("")

        def comando = new GerenciarCandidatosCommand(menuViewMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o mock da view deve ter o metodo 'gerenciarCandidatos' chamado exatamente 1 vez"
        1 * menuViewMock.gerenciarCandidatos(scanner)
    }
}