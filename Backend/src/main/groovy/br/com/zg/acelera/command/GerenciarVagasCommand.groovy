package br.com.zg.acelera.command

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class GerenciarVagasCommand implements ICommand {

    private final EmpresaController empresaController
    private final VagaController vagaController
    private final Scanner sc

    GerenciarVagasCommand(EmpresaController empresaController, VagaController vagaController, Scanner sc) {
        this.empresaController = empresaController
        this.vagaController = vagaController
        this.sc = sc
    }

    @Override
    void executar() {
        def respostaListagem = empresaController.listar()
        if (respostaListagem.status != 200 || !respostaListagem.dados) {
            println "\nNenhuma empresa cadastrada para gerenciar vagas."
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

        Empresa empresa = empresas[index]
        menuVagas(empresa)
    }

    private void menuVagas(Empresa empresa) {
        Map<Integer, ICommand> acoesVagas = [
                1: new ListarVagasCommand(vagaController, empresa),
                2: new CadastrarVagaCommand(vagaController, sc, empresa),
                3: new AtualizarVagaCommand(vagaController, sc, empresa),
                4: new ExcluirVagaCommand(vagaController, sc)
        ] as Map<Integer, ICommand>

        int opcao = 0
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