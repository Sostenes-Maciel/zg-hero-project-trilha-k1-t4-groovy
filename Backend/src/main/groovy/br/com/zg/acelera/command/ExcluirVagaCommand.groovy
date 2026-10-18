package br.com.zg.acelera.command

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class ExcluirVagaCommand implements ICommand {

    private final VagaController vagaController
    private final Scanner sc
    private final Empresa empresa

    ExcluirVagaCommand(VagaController vagaController, Scanner sc, Empresa empresa) {
        this.vagaController = vagaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        println "\n--- EXCLUIR VAGA ---"

        try {
            Map respostaLista = vagaController.listarDaEmpresa(empresa.cnpj)
            if (respostaLista.status != 200) {
                throw new RuntimeException("Falha no servidor: ${respostaLista.mensagem}")
            }

            List<Vaga> vagasDaEmpresa = respostaLista.dados as List<Vaga>

            if (!vagasDaEmpresa) {
                throw new IllegalStateException("Você não possui nenhuma vaga cadastrada para excluir.")
            }

            println "Suas vagas disponíveis:"
            vagasDaEmpresa.each { println "ID: ${it.id} | Título: ${it.titulo}" }
            println "----------------------"

            print "Digite o ID da vaga que deseja excluir: "
            String input = sc.nextLine().trim()

            if (!input.isInteger()) {
                throw new IllegalArgumentException("O ID digitado deve ser um número válido.")
            }

            int idVaga = input.toInteger()

            if (!vagasDaEmpresa.any { it.id == idVaga }) {
                throw new IllegalArgumentException("A vaga de ID ${idVaga} não existe ou não pertence à sua empresa.")
            }

            print "Tem certeza que deseja excluir a vaga ID ${idVaga}? (s/N): "
            if (sc.nextLine().trim().toLowerCase() != "s") {
                println "Exclusão cancelada pelo usuário."
                return
            }

            def resposta = vagaController.excluir(idVaga)
            println "\n[${resposta.status}] - ${resposta.mensagem}"

        } catch (IllegalStateException | IllegalArgumentException e) {
            println "\n[Atenção]: ${e.message}"
        } catch (Exception e) {
            println "\n[Erro Crítico]: Ocorreu um problema inesperado - ${e.message}"
        }
    }
}