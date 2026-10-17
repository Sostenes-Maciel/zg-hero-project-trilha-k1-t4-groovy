package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato

class AtualizarCandidatoCommand implements ICommand {

    private final CandidatoController candidatoController
    private final Scanner sc
    private final Candidato candidato

    AtualizarCandidatoCommand(CandidatoController candidatoController, Scanner sc, Candidato candidato) {
        this.candidatoController = candidatoController
        this.sc = sc
        this.candidato = candidato
    }

    @Override
    void executar() {
        println "\n--- ATUALIZAR CANDIDATO ---"
        println "Dica: Para manter o valor atual, apenas aperte ENTER vazia."

        print "Nome [${candidato.nome}]: "
        String nome = sc.nextLine().trim()
        if (!nome.isEmpty()) candidato.nome = nome

        print "E-mail [${candidato.email}]: "
        String email = sc.nextLine().trim()
        if (!email.isEmpty()) candidato.email = email

        print "Idade [${candidato.idade}]: "
        String idade = sc.nextLine().trim()
        if (!idade.isEmpty()) {
            try {
                candidato.idade = Integer.parseInt(idade)
            } catch (NumberFormatException e) {
                println "Idade inválida. Mantendo a original."
            }
        }

        print "Estado [${candidato.estado}]: "
        String estado = sc.nextLine().trim()
        if (!estado.isEmpty()) candidato.estado = estado

        print "CEP [${candidato.cep}]: "
        String cep = sc.nextLine().trim()
        if (!cep.isEmpty()) candidato.cep = cep

        print "Descrição [${candidato.descricao}]: "
        String descricao = sc.nextLine().trim()
        if (!descricao.isEmpty()) candidato.descricao = descricao

        print "Competências [${candidato.competencias?.join(', ')}]: "
        String competencias = sc.nextLine().trim()
        if (!competencias.isEmpty()) candidato.competencias = competencias.split(",").collect { it.trim() }

        def resposta = candidatoController.atualizar(candidato)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
    }
}