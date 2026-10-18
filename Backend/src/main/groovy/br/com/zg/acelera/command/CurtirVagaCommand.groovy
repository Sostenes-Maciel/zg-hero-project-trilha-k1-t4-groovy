package br.com.zg.acelera.command

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Vaga

class CurtirVagaCommand implements ICommand {

    private final VagaController vagaController
    private final MatchController matchController
    private final Scanner sc
    private final Candidato candidato

    CurtirVagaCommand(VagaController vagaController, MatchController matchController, Scanner sc, Candidato candidato) {
        this.vagaController = vagaController
        this.matchController = matchController
        this.sc = sc
        this.candidato = candidato
    }

    @Override
    void executar() {
        println "\n--- VAGAS DISPONÍVEIS (MODO ANÔNIMO) ---"

        try {
            def respVagas = vagaController.listar()
            if (respVagas.status != 200 || !respVagas.dados) {
                println "Nenhuma vaga disponível no momento."
                return
            }

            List<Vaga> vagas = respVagas.dados as List<Vaga>

            vagas.eachWithIndex { v, i ->
                println "[Opção ${i + 1}] - ${v.titulo}"
                println "   Local: ${v.estado ?: 'N/A'} / ${v.pais ?: 'N/A'}"
                println "   Competências: ${v.competencia?.join(', ') ?: 'N/A'}"
                println "   Descrição: ${v.descricao ?: 'N/A'}"
                println "------------------------------------------------"
            }

            print "Selecione o número da vaga para curtir (ou 0 para cancelar): "
            String input = sc.nextLine().trim()

            if (!input.isInteger()) {
                throw new IllegalArgumentException("Opção inválida. Digite um número.")
            }

            int opcao = input.toInteger()

            if (opcao == 0) {
                println "Operação cancelada."
                return
            }

            int idx = opcao - 1

            if (idx < 0 || idx >= vagas.size()) {
                throw new IllegalArgumentException("Número de vaga inexistente.")
            }

            def respostaMatch = matchController.curtirVaga(candidato.cpf, vagas[idx])
            println "\n[${respostaMatch.status}] - ${respostaMatch.mensagem}"

        } catch (IllegalArgumentException e) {
            println "\n[Atenção]: ${e.message}"
        } catch (Exception e) {
            println "\n[Erro Crítico]: Ocorreu um problema inesperado - ${e.message}"
        }
    }
}