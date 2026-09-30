package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class GerenciadorDePerfis {

    CandidatoDAO candidatoDAO = new CandidatoDAO()
    CompetenciaDAO competenciaDAO = new CompetenciaDAO()
    EmpresaDAO empresaDAO = new EmpresaDAO()
    CurtidaDAO curtidaDAO = new CurtidaDAO()
    MatchDAO matchDAO = new MatchDAO()
    GerenciadorDeVagas gerenciadorDeVagas = new GerenciadorDeVagas()

    void listarCandidatos() {

        println("\n-- Candidatos Cadastrados --")

        List<Candidato> candidatos = candidatoDAO.listarTodos()

        if (candidatos.isEmpty()) {
            println("Nenhum candidato cadastrado ainda.\n")
            return
        }

        candidatos.eachWithIndex { candidato, indice ->

            candidato.competencias =
                    competenciaDAO.listarDoCandidato(candidato.cpf)

            println "ID: ${indice + 1}"
            println "Nome: ${candidato.nome}"
            println "CPF: ${candidato.cpf}"
            println "Estado: ${candidato.estado}"
            println "Competências: ${candidato.competencias.join(', ')}"
            println "-" * 60
        }

        println()
    }

    void listarEmpresas() {
        println("\n-- Empresas Cadastradas --")

        List<Empresa> empresas = empresaDAO.listarTodos()

        if (empresas.isEmpty()) {
            println("Nenhuma empresa cadastrada ainda.\n")
            return
        }

        empresas.each { empresa ->
            empresa.competencias = competenciaDAO.listarDaEmpresa(empresa.cnpj)

            println "Nome: ${empresa.nome} | CNPJ: ${empresa.cnpj} | País: ${empresa.pais}"
            println "Competências desejadas: ${empresa.competencias.join(', ')}"
            println "-" * 60
        }

        println()
    }

    void cadastroNovo(Scanner sc) {
        println("\n-- Opções de Cadastro --")
        println("1 - Candidato")
        println("2 - Empresa")
        print("Qual deseja cadastrar: ")

        try {
            if (!sc.hasNextInt()) {
                println("Erro: Entrada inválida. Retornando ao menu.")
                sc.next()
                return
            }
            int opcaoadd = sc.nextInt()
            sc.nextLine()

            if (opcaoadd == 1) {
                try {
                    println "\n--- CADASTRAR NOVO CANDIDATO ---"

                    print "Nome: "
                    String nome = sc.nextLine().trim()
                    ValidarCandidato.validarNome(nome)

                    print "E-mail: "
                    String email = sc.nextLine().trim()
                    ValidarCandidato.validarEmail(email)

                    print "CPF: "
                    String cpf = sc.nextLine().trim()
                    ValidarCandidato.validarCpf(cpf)

                    print "Idade: "
                    if (!sc.hasNextInt()) {
                        sc.nextLine()
                        throw new IllegalArgumentException("Idade deve ser um número inteiro.")
                    }

                    int idade = sc.nextInt()
                    sc.nextLine()
                    ValidarCandidato.validarIdade(idade)

                    print "País: "
                    String pais = sc.nextLine().trim()

                    print "Estado: "
                    String estado = sc.nextLine().trim()
                    ValidarCandidato.validarEstado(estado)

                    print "CEP: "
                    String cep = sc.nextLine().trim()
                    ValidarCandidato.validarCep(cep)

                    print "Descrição pessoal: "
                    String descricao = sc.nextLine().trim()
                    ValidarCandidato.validarDescricao(descricao)


                    Candidato novoCandidato = new Candidato(
                            nome: nome,
                            email: email,
                            cpf: cpf,
                            idade: idade,
                            pais: pais,
                            estado: estado,
                            cep: cep,
                            descricao: descricao,
                    )

                    candidatoDAO.cadastrar(novoCandidato)

                    competencias.each { String competencia ->
                        competenciaDAO.garantirCompetencia(competencia)
                        competenciaDAO.adicionarAoCandidato(novoCandidato.cpf, competencia)
                    }

                    println "Candidato cadastrado com sucesso!\n"

                } catch (IllegalArgumentException e) {
                    println "Erro: ${e.message}"
                } catch (Exception e) {
                    println "Erro inesperado: ${e.message}"
                }

            } else if (opcaoadd == 2) {
                try {
                    println "\n--- CADASTRAR NOVA EMPRESA ---"

                    print "Nome da Empresa: "
                    String nome = sc.nextLine().trim()
                    ValidarEmpresa.validarNome(nome)

                    print "E-mail Corporativo: "
                    String email = sc.nextLine().trim()
                    ValidarEmpresa.validarEmail(email)

                    print "CNPJ: "
                    String cnpj = sc.nextLine().trim()
                    ValidarEmpresa.validarCnpj(cnpj)

                    print "País: "
                    String pais = sc.nextLine().trim()
                    ValidarEmpresa.validarPais(pais)

                    print "Estado: "
                    String estado = sc.nextLine().trim()
                    ValidarEmpresa.validarEstado(estado)

                    print "CEP: "
                    String cep = sc.nextLine().trim()
                    ValidarEmpresa.validarCep(cep)

                    print "Descrição da empresa: "
                    String descricao = sc.nextLine().trim()
                    ValidarEmpresa.validarDescricao(descricao)

                    Empresa novaEmpresa = new Empresa(
                            nome: nome,
                            email: email,
                            cnpj: cnpj,
                            pais: pais,
                            estado: estado,
                            cep: cep,
                            descricao: descricao,
                    )

                    empresaDAO.cadastrar(novaEmpresa)

                    competencias.each { String competencia ->
                        competenciaDAO.garantirCompetencia(competencia)
                        competenciaDAO.adicionarAoEmpresa(novaEmpresa.cnpj, competencia)
                    }

                    println "Empresa cadastrada com sucesso!\n"

                } catch (IllegalArgumentException e) {
                    println "Erro ao cadastrar empresa: ${e.message}"

                } catch (Exception e) {
                    println "Erro no cadastro: ${e.message}"
                }
            }
        } catch (Exception e) {
            println "Erro ${e.message}"
        }
    }



    Empresa selecionarEmpresa(Scanner sc) {

        List<Empresa> empresas = empresaDAO.listarTodos()

        if (empresas.isEmpty()) {
            println "Nenhuma empresa cadastrada ainda."
            return null
        }

        println "\n-- Empresas Cadastradas --"

        empresas.eachWithIndex { empresa, indice ->
            println "${indice + 1} - ${empresa.nome} | CNPJ: ${empresa.cnpj}"
        }

        print "Selecione a empresa: "

        if (!sc.hasNextInt()) {
            println "Erro: informe apenas o número da empresa."
            sc.nextLine()
            return null
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > empresas.size()) {
            println "Empresa inválida."
            return null
        }

        return empresas[opcao - 1]
    }

    void gerenciarVagas(Scanner sc) {

        Empresa empresa = selecionarEmpresa(sc)

        if (empresa == null) {
            return
        }

        int opcao = 0

        while (opcao != 5) {

            println "\n--- VAGAS DE ${empresa.nome} ---"
            println "1 - Cadastrar vaga"
            println "2 - Listar vagas"
            println "3 - Atualizar vaga"
            println "4 - Excluir vaga"
            println "5 - Voltar"

            print "Escolha uma opção: "

            if (!sc.hasNextInt()) {
                println "Opção inválida."
                sc.nextLine()
                continue
            }

            opcao = sc.nextInt()
            sc.nextLine()

            switch (opcao) {
                case 1:
                    gerenciadorDeVagas.cadastrarVagaPeloTerminal(sc, empresa)
                    break

                case 2:
                    gerenciadorDeVagas.listarVagasPeloTerminal(empresa)
                    break

                case 3:
                    gerenciadorDeVagas.atualizarVagaPeloTerminal(sc, empresa)
                    break

                case 4:
                    gerenciadorDeVagas.excluirVagaPeloTerminal(sc, empresa)
                    break

                case 5:
                    println "Voltando..."
                    break

                default:
                    println "Opção inválida."
            }
        }
    }
    Candidato selecionarCandidato(Scanner sc) {

        List<Candidato> candidatos = candidatoDAO.listarTodos()

        if (candidatos.isEmpty()) {
            println "Nenhum candidato cadastrado ainda."
            return null
        }

        println "\n-- Candidatos Cadastrados --"

        candidatos.eachWithIndex { candidato, indice ->
            println "${indice + 1} - ${candidato.nome} | CPF: ${candidato.cpf}"
        }

        print "Selecione o candidato pelo ID: "

        if (!sc.hasNextInt()) {
            println "Erro: informe apenas o número do candidato."
            sc.nextLine()
            return null
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > candidatos.size()) {
            println "Candidato inválido."
            return null
        }

        return candidatos[opcao - 1]
    }
    void atualizarCandidatoPeloTerminal(Scanner sc) {

        Candidato candidato = selecionarCandidato(sc)

        if (candidato == null) {
            return
        }

        print "Novo nome: "
        String nome = sc.nextLine().trim()
        ValidarCandidato.validarNome(nome)

        print "Novo e-mail: "
        String email = sc.nextLine().trim()
        ValidarCandidato.validarEmail(email)

        print "Nova idade: "
        if (!sc.hasNextInt()) {
            println "Idade inválida."
            sc.nextLine()
            return
        }

        int idade = sc.nextInt()
        sc.nextLine()
        ValidarCandidato.validarIdade(idade)

        print "Novo país: "
        String pais = sc.nextLine().trim()

        print "Novo estado: "
        String estado = sc.nextLine().trim()
        ValidarCandidato.validarEstado(estado)

        print "Novo CEP: "
        String cep = sc.nextLine().trim()
        ValidarCandidato.validarCep(cep)

        print "Nova descrição: "
        String descricao = sc.nextLine().trim()
        ValidarCandidato.validarDescricao(descricao)

        candidato.nome = nome
        candidato.email = email
        candidato.idade = idade
        candidato.pais = pais
        candidato.estado = estado
        candidato.cep = cep
        candidato.descricao = descricao

        candidatoDAO.atualizar(candidato)

        println "Candidato atualizado com sucesso!"
    }
    void excluirCandidatoPeloTerminal(Scanner sc) {

        Candidato candidato = selecionarCandidato(sc)

        if (candidato == null) {
            return
        }

        candidatoDAO.excluir(candidato.cpf)

        println "Candidato excluído com sucesso!"
    }

    Empresa selecionarEmpresaParaEdicao(Scanner sc) {

        List<Empresa> empresas = empresaDAO.listarTodos()

        if (empresas.isEmpty()) {
            println "Nenhuma empresa cadastrada ainda."
            return null
        }

        println "\n-- Empresas Cadastradas --"

        empresas.eachWithIndex { empresa, indice ->
            println "${indice + 1} - ${empresa.nome}"
        }

        print "Selecione a empresa pelo ID: "

        if (!sc.hasNextInt()) {
            println "Opção inválida."
            sc.nextLine()
            return null
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > empresas.size()) {
            println "Empresa inválida."
            return null
        }

        return empresas[opcao - 1]
    }
    void atualizarEmpresaPeloTerminal(Scanner sc) {

        Empresa empresa = selecionarEmpresaParaEdicao(sc)

        if (empresa == null) {
            return
        }

        print "Novo nome da empresa: "
        String nome = sc.nextLine().trim()
        ValidarEmpresa.validarNome(nome)

        print "Novo e-mail corporativo: "
        String email = sc.nextLine().trim()
        ValidarEmpresa.validarEmail(email)

        print "Novo país: "
        String pais = sc.nextLine().trim()
        ValidarEmpresa.validarPais(pais)

        print "Novo estado: "
        String estado = sc.nextLine().trim()
        ValidarEmpresa.validarEstado(estado)

        print "Novo CEP: "
        String cep = sc.nextLine().trim()
        ValidarEmpresa.validarCep(cep)

        print "Nova descrição: "
        String descricao = sc.nextLine().trim()
        ValidarEmpresa.validarDescricao(descricao)

        empresa.nome = nome
        empresa.email = email
        empresa.pais = pais
        empresa.estado = estado
        empresa.cep = cep
        empresa.descricao = descricao

        empresaDAO.atualizar(empresa)

        println "Empresa atualizada com sucesso!"
    }
    void excluirEmpresaPeloTerminal(Scanner sc) {

        Empresa empresa = selecionarEmpresaParaEdicao(sc)

        if (empresa == null) {
            return
        }

        empresaDAO.excluir(empresa.cnpj)

        println "Empresa excluída com sucesso!"
    }
    List<String> listarCompetenciasDoCandidato(Candidato candidato) {

        return competenciaDAO.listarDoCandidato(candidato.cpf)
    }
    void adicionarCompetenciaAoCandidato(
            Candidato candidato,
            String competencia) {

        competenciaDAO.garantirCompetencia(competencia)

        competenciaDAO.adicionarAoCandidato(
                candidato.cpf,
                competencia
        )
    }
    void removerCompetenciaDoCandidato(
            Candidato candidato,
            String competencia) {

        competenciaDAO.removerDoCandidato(
                candidato.cpf,
                competencia
        )
    }
    List<String> listarCompetenciasDaEmpresa(Empresa empresa) {

        return competenciaDAO.listarDaEmpresa(empresa.cnpj)
    }

    void adicionarCompetenciaAEmpresa(
            Empresa empresa,
            String competencia) {

        competenciaDAO.garantirCompetencia(competencia)

        competenciaDAO.adicionarAoEmpresa(
                empresa.cnpj,
                competencia
        )
    }

    void removerCompetenciaDaEmpresa(
            Empresa empresa,
            String competencia) {

        competenciaDAO.removerDaEmpresa(
                empresa.cnpj,
                competencia
        )
    }
    void curtirVagaPeloTerminal(Scanner sc) {

        Candidato candidato = selecionarCandidato(sc)

        if (candidato == null) {
            return
        }

        List<Vaga> vagas = gerenciadorDeVagas.listarVagas()

        if (vagas.isEmpty()) {
            println "Nenhuma vaga cadastrada ainda."
            return
        }

        println "\n-- Vagas Disponíveis --"

        vagas.eachWithIndex { vaga, indice ->
            println "${indice + 1} - ${vaga.titulo} | Empresa: ${vaga.empresa.nome}"
        }

        print "Selecione a vaga pelo ID: "

        if (!sc.hasNextInt()) {
            println "Erro: informe apenas o número da vaga."
            sc.nextLine()
            return
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > vagas.size()) {
            println "Vaga inválida."
            return
        }

        Vaga vaga = vagas[opcao - 1]

        curtidaDAO.registrarCurtidaCandidato(
                candidato.cpf,
                vaga.id
        )
    }
    void curtirCandidatoPeloTerminal(Scanner sc) {

        Empresa empresa = selecionarEmpresa(sc)

        if (empresa == null) {
            return
        }

        Candidato candidato = selecionarCandidato(sc)

        if (candidato == null) {
            return
        }

        List<Vaga> vagas = gerenciadorDeVagas.listarVagasDaEmpresa(empresa.cnpj)

        if (vagas.isEmpty()) {
            println "Essa empresa não possui vagas cadastradas."
            return
        }

        println "\n-- Vagas da ${empresa.nome} --"

        vagas.eachWithIndex { vaga, indice ->
            println "${indice + 1} - ${vaga.titulo}"
        }

        print "Selecione a vaga: "

        if (!sc.hasNextInt()) {
            println "Erro: informe apenas o número da vaga."
            sc.nextLine()
            return
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > vagas.size()) {
            println "Vaga inválida."
            return
        }

        Vaga vaga = vagas[opcao - 1]

        curtidaDAO.registrarCurtidaEmpresa(
                candidato.cpf,
                empresa.cnpj
        )

        if (curtidaDAO.verificarMatch(
                candidato.cpf,
                empresa.cnpj,
                vaga.id)) {

            if (matchDAO.existeMatch(
                    candidato.cpf,
                    empresa.cnpj,
                    vaga.id)) {

                println "\nEsse match já está registrado."

            } else {

                matchDAO.registrarMatch(
                        candidato.cpf,
                        empresa.cnpj,
                        vaga.id
                )

                println "\n*** MATCH ENCONTRADO! ***"
                println "${empresa.nome} e ${candidato.nome} deram match!"
            }
        }
    }
}