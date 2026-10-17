package br.com.zg.acelera.command

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.model.Empresa

class AtualizarEmpresaCommand implements ICommand {

    private final EmpresaController empresaController
    private final Scanner sc
    private final Empresa empresa

    AtualizarEmpresaCommand(EmpresaController empresaController, Scanner sc, Empresa empresa) {
        this.empresaController = empresaController
        this.sc = sc
        this.empresa = empresa
    }

    @Override
    void executar() {
        println "\n--- ATUALIZAR EMPRESA ---"
        println "Dica: Para manter o valor atual, apenas aperte ENTER vazia."

        print "Nome [${empresa.nome}]: "
        String nome = sc.nextLine().trim()
        if (!nome.isEmpty()) empresa.nome = nome

        print "E-mail [${empresa.email}]: "
        String email = sc.nextLine().trim()
        if (!email.isEmpty()) empresa.email = email

        print "País [${empresa.pais}]: "
        String pais = sc.nextLine().trim()
        if (!pais.isEmpty()) empresa.pais = pais

        print "Estado [${empresa.estado}]: "
        String estado = sc.nextLine().trim()
        if (!estado.isEmpty()) empresa.estado = estado

        print "CEP [${empresa.cep}]: "
        String cep = sc.nextLine().trim()
        if (!cep.isEmpty()) empresa.cep = cep

        print "Descrição [${empresa.descricao}]: "
        String descricao = sc.nextLine().trim()
        if (!descricao.isEmpty()) empresa.descricao = descricao

        print "Competências [${empresa.competencias?.join(', ')}]: "
        String competencias = sc.nextLine().trim()
        if (!competencias.isEmpty()) empresa.competencias = competencias.split(",").collect { it.trim() }

        def resposta = empresaController.atualizar(empresa)
        println "\n[${resposta.status}] - ${resposta.mensagem}"
    }
}