package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class GerenciarEmpresasCommand implements ICommand {

    private final EmpresaController empresaController
    private final CandidatoController candidatoController
    private final VagaController vagaController
    private final MatchController matchController
    private final Scanner sc

    GerenciarEmpresasCommand(EmpresaController empresaController, CandidatoController candidatoController, VagaController vagaController, MatchController matchController, Scanner sc) {
        this.empresaController = empresaController
        this.candidatoController = candidatoController
        this.vagaController = vagaController
        this.matchController = matchController
        this.sc = sc
    }

    @Override
    void executar() {
        def respostaListagem = empresaController.listar()
        if (respostaListagem.status != 200 || !respostaListagem.dados) {
            println "\nNenhuma empresa cadastrada."
            return
        }

        println "\n--- SELECIONE A EMPRESA ---"
        List<Empresa> empresas = respostaListagem.dados as List<Empresa>
        empresas.eachWithIndex { emp, i -> println "${i + 1} - ${emp.nome}" }
        print "Opção: "

        int index = lerOpcaoInt(sc) - 1
        if (index < 0 || index >= empresas.size()) {
            println "Opção inválida."
            return
        }

        menuEmpresa(empresas[index])
    }

    private void menuEmpresa(Empresa empresa) {
        Map<Integer, ICommand> acoesEmpresa = [
                1: new AtualizarEmpresaCommand(empresaController, sc, empresa),
                2: new GerenciarCompetenciasEmpresaCommand(empresaController, sc, empresa),
                3: new CurtirCandidatoCommand(candidatoController, vagaController, matchController, sc, empresa),
                4: new VerMatchesEmpresaCommand(matchController, empresa),
                5: new ExcluirEmpresaCommand(empresaController, empresa)
        ] as Map<Integer, ICommand>

        int opcao = 0
        while (opcao != 6) {
            println "\n--- PERFIL DA EMPRESA: ${empresa.nome} ---"
            println "1 - Atualizar perfil"
            println "2 - Gerenciar Competências"
            println "3 - Curtir Candidato"
            println "4 - Ver Matches"
            println "5 - Excluir perfil"
            println "6 - Voltar"
            print "Escolha: "

            opcao = lerOpcaoInt(sc)

            if (opcao == 5) {
                println "Voltando..."
                break
            }

            ICommand comando = acoesEmpresa.get(opcao)
            if (comando != null) {
                comando.executar()
                if (opcao == 6) break
            } else {
                println "Opção inválida."
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