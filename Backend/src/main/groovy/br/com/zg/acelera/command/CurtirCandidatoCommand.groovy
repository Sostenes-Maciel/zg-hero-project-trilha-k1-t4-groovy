package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class CurtirCandidatoCommand implements ICommand {

    private final CandidatoController candidatoController
    private final VagaController vagaController
    private final MatchController matchController
    private final Object sc
    private final Empresa empresa

    CurtirCandidatoCommand(CandidatoController candidatoController, VagaController vagaController, MatchController matchController, Object sc, Empresa empresa) {
        this.candidatoController = candidatoController
        this.vagaController = vagaController
        this.matchController = matchController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        println "\n--- CANDIDATOS DISPONÍVEIS (MODO ANÔNIMO) ---"

        try {
            def respCandidatos = candidatoController.listar()

            println "[DEBUG] Status: ${respCandidatos?.status}"

            if (!respCandidatos || respCandidatos.status != 200) {
                println "[ERRO] Falha ao buscar candidatos: ${respCandidatos?.mensagem}"
                return
            }

            List<Candidato> candidatos = respCandidatos.dados as List<Candidato>

            if (!candidatos) {
                println "Nenhum candidato disponível no momento."
                return
            }

            candidatos.eachWithIndex { c, i ->
                println "[Opção ${i + 1}] - Perfil Profissional"
                println "   Local: ${c.estado ?: 'N/A'}"
                println "   Competências: ${c.competencias ?: 'N/A'}"
                println "   Descrição: ${c.descricao ?: 'N/A'}"
                println "------------------------------------------------"
            }

            print "Selecione o número do candidato para curtir (ou 0 para cancelar): "
            String inputCandidato = sc.nextLine().trim()

            if (!inputCandidato.isInteger()) {
                throw new IllegalArgumentException("Opção inválida. Digite um número.")
            }

            int opcaoCandidato = inputCandidato.toInteger()

            if (opcaoCandidato == 0) {
                println "Operação cancelada."
                return
            }

            int idxCandidato = opcaoCandidato - 1

            if (idxCandidato < 0 || idxCandidato >= candidatos.size()) {
                throw new IllegalArgumentException("Número de candidato inexistente.")
            }

            Candidato candidatoSelecionado = candidatos[idxCandidato]

            def respVagas = vagaController.listarDaEmpresa(empresa.cnpj)
            if (respVagas.status != 200 || !respVagas.dados) {
                throw new IllegalStateException("Você precisa cadastrar vagas antes de curtir um candidato.")
            }

            List<Vaga> vagas = respVagas.dados as List<Vaga>
            if (!vagas) {
                throw new IllegalStateException("Você precisa cadastrar vagas antes de curtir um candidato.")
            }

            println "\n--- PARA QUAL VAGA? ---"
            vagas.eachWithIndex { v, i -> println "[Opção ${i + 1}] - ID: ${v.id} | ${v.titulo}" }
            print "Selecione o número da vaga (ou 0 para cancelar): "

            String inputVaga = sc.nextLine().trim()

            if (!inputVaga.isInteger()) {
                throw new IllegalArgumentException("Opção de vaga inválida. Digite um número.")
            }

            int opcaoVaga = inputVaga.toInteger()

            if (opcaoVaga == 0) {
                println "Operação cancelada."
                return
            }

            int idxVaga = opcaoVaga - 1

            if (idxVaga < 0 || idxVaga >= vagas.size()) {
                throw new IllegalArgumentException("Número de vaga inexistente.")
            }

            Vaga vagaSelecionada = vagas[idxVaga]

            def respostaMatch = matchController.curtirCandidato(empresa.cnpj, candidatoSelecionado.cpf, vagaSelecionada.id)
            println "\n[${respostaMatch.status}] - ${respostaMatch.mensagem}"

        } catch (IllegalStateException | IllegalArgumentException e) {
            println "\n[Atenção]: ${e.message}"
        } catch (Exception e) {
            println "\n[Erro Crítico]: Ocorreu um problema inesperado - ${e.message}"
        }
    }
}