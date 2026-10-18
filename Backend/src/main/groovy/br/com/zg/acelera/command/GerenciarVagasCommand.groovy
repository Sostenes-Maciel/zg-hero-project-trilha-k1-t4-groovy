package br.com.zg.acelera.command

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class GerenciarVagasCommand implements ICommand {

    private final EmpresaController empresaController
    private final VagaController vagaController
    private final Scanner sc
    private final Empresa empresa

    GerenciarVagasCommand(EmpresaController empresaController, VagaController vagaController, Scanner sc, Empresa empresa) {
        this.empresaController = empresaController
        this.vagaController = vagaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {

        int opcao = 0

        Map<Integer, ICommand> acoesVagas = [
                1: new ListarVagasCommand(vagaController, empresa),
                2: new CadastrarVagaCommand(vagaController, sc, empresa),
                3: new AtualizarVagaCommand(vagaController, sc, empresa),
                4: new ExcluirVagaCommand(vagaController, sc, empresa)
        ] as Map<Integer, ICommand>

        while (opcao != 5) {
            println "\n--- GERENCIAR VAGAS: ${empresa.nome} ---"
            println "1 - Listar vagas da empresa"
            println "2 - Cadastrar nova vaga"
            println "3 - Atualizar vaga"
            println "4 - Excluir vaga"
            println "5 - Voltar"
            print "Escolha: "

            opcao = lerOpcaoInt(sc)

            if (opcao == 5) {
                println "Voltando..."
                break
            }

            ICommand comando = acoesVagas.get(opcao)
            if (comando != null) {
                comando.executar()
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