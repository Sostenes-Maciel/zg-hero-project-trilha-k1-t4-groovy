package br.com.zg.aceleraspec.command

import br.com.zg.acelera.command.GerenciarEmpresasCommand
import br.com.zg.acelera.view.MenuView
import spock.lang.Specification

class GerenciarEmpresasCommandSpec extends Specification {

    def "deve chamar o metodo gerenciarEmpresas da view ao executar o comando"() {
        given: "um mock da MenuView e um scanner para injecao"
        def menuViewMock = Mock(MenuView)
        def scanner = new Scanner("")

        def comando = new GerenciarEmpresasCommand(menuViewMock, scanner)

        when: "o comando eh executado"
        comando.executar()

        then: "o mock da view deve ter o metodo 'gerenciarEmpresas' chamado exatamente 1 vez"
        1 * menuViewMock.gerenciarEmpresas(scanner)
    }
}