package br.com.zg.acelera.command

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class AtualizarVagaCommand implements ICommand {

    private final VagaController vagaController
    private final Scanner sc
    private final Empresa empresa

    AtualizarVagaCommand(VagaController vagaController, Scanner sc, Empresa empresa) {
        this.vagaController = vagaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        println "\n--- ATUALIZAR VAGA ---"

        Map respostaLista = vagaController.listarDaEmpresa(empresa.cnpj)

        if (respostaLista.status != 200) {
            println "Erro ao buscar as vagas: ${respostaLista.mensagem}"
            return
        }

        List<Vaga> vagasDaEmpresa = respostaLista.dados as List<Vaga>

        if (vagasDaEmpresa == null || vagasDaEmpresa.isEmpty()) {
            println "Você ainda não possui nenhuma vaga cadastrada para atualizar."
            return
        }

        println "Suas vagas disponíveis:"
        vagasDaEmpresa.each { v ->
            println "ID: ${v.id} | Título: ${v.titulo}"
        }
        println "----------------------"

        print "Digite o ID da vaga que deseja atualizar: "
        String input = sc.nextLine().trim()
        if (!input.isInteger()) {
            println "ID inválido. Operação cancelada."
            return
        }
        int idVaga = input.toInteger()

        Vaga vagaParaAtualizar = vagasDaEmpresa.find { it.id == idVaga }

        if (vagaParaAtualizar == null) {
            println "Vaga não encontrada ou não pertence à sua empresa!"
            return
        }

        println "\nDica: Para manter o valor atual, apenas aperte ENTER vazia."

        print "Título [${vagaParaAtualizar.titulo}]: "
        String titulo = sc.nextLine().trim()
        if (!titulo.isEmpty()) vagaParaAtualizar.titulo = titulo

        print "Descrição [${vagaParaAtualizar.descricao}]: "
        String descricao = sc.nextLine().trim()
        if (!descricao.isEmpty()) vagaParaAtualizar.descricao = descricao

        print "País [${vagaParaAtualizar.pais}]: "
        String pais = sc.nextLine().trim()
        if (!pais.isEmpty()) vagaParaAtualizar.pais = pais

        print "Estado [${vagaParaAtualizar.estado}]: "
        String estado = sc.nextLine().trim()
        if (!estado.isEmpty()) vagaParaAtualizar.estado = estado

        print "Competências [${vagaParaAtualizar.competencia?.join(', ')}]: "
        String novaEntrada = sc.nextLine().trim()
        if (!novaEntrada.isEmpty()) {
            vagaParaAtualizar.competencia = novaEntrada.split(",").collect { it.trim() }
        }

        def resposta = vagaController.atualizar(vagaParaAtualizar)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
    }
}