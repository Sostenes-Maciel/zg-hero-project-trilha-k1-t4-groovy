package br.com.zg.acelera.command

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class GerenciarCompetenciasEmpresaCommand implements ICommand {

    private final EmpresaController empresaController
    private final Scanner sc
    private final Empresa empresa

    GerenciarCompetenciasEmpresaCommand(EmpresaController empresaController, Scanner sc, Empresa empresa) {
        this.empresaController = empresaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        int opcao = 0
        while (opcao != 4) {
            println "\n--- COMPETÊNCIAS DA EMPRESA ${empresa.nome.toUpperCase()} ---"
            println "Atuais: ${empresa.competencias.isEmpty() ? 'Nenhuma' : empresa.competencias.join(', ')}"
            println "1 - Adicionar nova competência"
            println "2 - Remover uma competência"
            println "3 - Limpar todas"
            println "4 - Voltar"
            print "Escolha: "

            try {
                opcao = Integer.parseInt(sc.nextLine().trim())
            } catch (NumberFormatException ignored) {
                opcao = 0
            }

            switch (opcao) {
                case 1:
                    print "Digite a nova competência: "
                    String nova = sc.nextLine().trim()
                    if (!nova.isEmpty()) {
                        empresaController.adicionarCompetencia(empresa, nova)
                        if (!empresa.competencias.contains(nova)) {
                            empresa.competencias.add(nova)
                        }
                        println "Competência adicionada com sucesso!"
                    }
                    break
                case 2:
                    print "Qual competência deseja remover? (Digite exatamente igual): "
                    String remover = sc.nextLine().trim()
                    if (empresa.competencias.contains(remover)) {
                        empresaController.removerCompetencia(empresa, remover)
                        empresa.competencias.remove(remover)
                        println "Competência removida com sucesso!"
                    } else {
                        println "Competência não encontrada no perfil."
                    }
                    break
                case 3:
                    if (!empresa.competencias.isEmpty()) {
                        empresa.competencias.each { comp ->
                            empresaController.removerCompetencia(empresa, comp)
                        }
                        empresa.competencias.clear()
                        println "Todas as competências foram apagadas com sucesso!"
                    } else {
                        println "A empresa já não possui competências cadastradas."
                    }
                    break
                case 4:
                    println "Voltando..."
                    break
                default:
                    println "Opção inválida."
            }
        }
    }
}