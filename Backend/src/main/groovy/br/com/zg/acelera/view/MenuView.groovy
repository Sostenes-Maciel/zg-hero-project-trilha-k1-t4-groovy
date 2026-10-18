package br.com.zg.acelera.view

import br.com.zg.acelera.command.CadastrarCommand
import br.com.zg.acelera.command.GerenciarCandidatosCommand
import br.com.zg.acelera.command.GerenciarEmpresasCommand
import br.com.zg.acelera.command.GerenciarVagasCommand
import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand

class MenuView {

    private final Scanner sc
    private final Map<Integer, ICommand> menuPrincipal

    MenuView(CandidatoController candidatoController, EmpresaController empresaController, VagaController vagaController, MatchController matchController) {
        this.sc = new Scanner(System.in)

        this.menuPrincipal = [
                1: new GerenciarCandidatosCommand(candidatoController, vagaController, matchController, sc),
                2: new GerenciarEmpresasCommand(empresaController, candidatoController, vagaController, matchController, sc),
                3: new CadastrarCommand(candidatoController, empresaController, sc),
        ] as Map<Integer, ICommand>
    }

    void iniciar() {
        int opcao = 0
        println "Bem-vindo ao Linketinder MVC <3"

        while (opcao != 5) {
            println "\n1 - Gerenciar Candidatos"
            println "2 - Gerenciar Empresas"
            println "3 - Cadastrar (Candidato/Empresa)"
            println "4 - Sair"
            print "Escolha uma opção: "

            opcao = lerOpcaoInt(sc)

            if (opcao == 5) {
                println "Saindo do Linketinder. Até logo!"
                break
            }

            ICommand comando = menuPrincipal.get(opcao)
            if (comando != null) {
                comando.executar()
            } else {
                println "Opção indisponível ou inválida."
            }
        }
    }

    private static int lerOpcaoInt(Scanner sc) {
        if (!sc.hasNextInt()) {
            sc.nextLine()
            return -1
        }
        int op = sc.nextInt()
        sc.nextLine()
        return op
    }
}