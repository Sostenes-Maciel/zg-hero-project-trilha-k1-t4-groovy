package br.com.zg.acelera.command

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa

class CadastrarCommand implements ICommand {

    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final Scanner sc

    CadastrarCommand(CandidatoController candidatoController, EmpresaController empresaController, Scanner sc) {
        this.candidatoController = candidatoController
        this.empresaController = empresaController
        this.sc = sc
    }

    @Override
    void executar() {
        println "\n--- CADASTRAR ---"
        println "1 - Candidato"
        println "2 - Empresa"
        print "Escolha uma opção: "

        int opcao = lerOpcaoInt(sc)

        if (opcao == 1) {
            Candidato c = new Candidato()
            print "Nome: "; c.nome = sc.nextLine().trim()
            print "CPF (11 dígitos): "; c.cpf = sc.nextLine().trim()
            print "E-mail: "; c.email = sc.nextLine().trim()
            print "Idade: "; c.idade = lerOpcaoInt(sc)
            print "Estado: "; c.estado = sc.nextLine().trim()
            print "CEP: "; c.cep = sc.nextLine().trim()
            print "Descrição: "; c.descricao = sc.nextLine().trim()
            print "Competências (separadas por vírgula): "
            c.competencias = sc.nextLine().split(",").collect { it.trim() }

            def resposta = candidatoController.cadastrar(c)
            println "\n[${resposta.status}] -${resposta.mensagem}"

        } else if (opcao == 2) {
            Empresa e = new Empresa()
            print "Nome: "; e.nome = sc.nextLine().trim()
            print "CNPJ (14 dígitos): "; e.cnpj = sc.nextLine().trim()
            print "E-mail: "; e.email = sc.nextLine().trim()
            print "País: "; e.pais = sc.nextLine().trim()
            print "Estado: "; e.estado = sc.nextLine().trim()
            print "CEP: "; e.cep = sc.nextLine().trim()
            print "Descrição: "; e.descricao = sc.nextLine().trim()
            print "Competências desejadas (separadas por vírgula): "
            e.competencias = sc.nextLine().split(",").collect { it.trim() }

            def resposta = empresaController.cadastrar(e)
            println "\n[${resposta.status}] -${resposta.mensagem}"
        } else {
            println "Opção inválida."
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