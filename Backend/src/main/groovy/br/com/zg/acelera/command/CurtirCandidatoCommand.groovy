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
    private final Scanner sc
    private final Empresa empresa

    CurtirCandidatoCommand(CandidatoController candidatoController, VagaController vagaController, MatchController matchController, Scanner sc, Empresa empresa) {
        this.candidatoController = candidatoController
        this.vagaController = vagaController
        this.matchController = matchController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        def respCandidatos = candidatoController.listar()
        if (respCandidatos.status != 200 || !respCandidatos.dados) {
            println "Nenhum candidato disponível no momento."
            return
        }

        println "\n--- CANDIDATOS DISPONÍVEIS ---"
        List<Candidato> candidatos = respCandidatos.dados as List<Candidato>
        candidatos.eachWithIndex { c, i -> println "${i + 1} - ${c.nome} (Competências: ${c.competencias})" }
        print "Selecione o número do candidato para curtir (ou 0 para cancelar): "

        int idxCandidato = lerOpcaoInt(sc) - 1
        if (idxCandidato == -1) return
        if (idxCandidato < 0 || idxCandidato >= candidatos.size()) {
            println "Opção inválida."
            return
        }
        Candidato candidato = candidatos[idxCandidato]

        def respVagas = vagaController.listarDaEmpresa(empresa.cnpj)
        if (respVagas.status != 200 || !respVagas.dados) {
            println "Você precisa cadastrar vagas antes de curtir um candidato."
            return
        }

        println "\n--- PARA QUAL VAGA? ---"
        List<Vaga> vagas = respVagas.dados as List<Vaga>
        vagas.eachWithIndex { v, i -> println "${i + 1} - ID: ${v.id} | ${v.titulo}" }
        print "Selecione o número da vaga: "

        int idxVaga = lerOpcaoInt(sc) - 1
        if (idxVaga < 0 || idxVaga >= vagas.size()) {
            println "Opção de vaga inválida."
            return
        }
        Vaga vagaSelecionada = vagas[idxVaga]

        def respostaMatch = matchController.curtirCandidato(empresa.cnpj, candidato.cpf, vagaSelecionada.id)
        println "\n[${respostaMatch.status}] - ${respostaMatch.mensagem}"
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