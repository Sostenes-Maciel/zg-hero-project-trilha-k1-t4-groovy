package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class GerenciadorDePerfisService {

    private final IConexao conexaoFactory

     CandidatoDAO candidatoDAO
     CompetenciaDAO competenciaDAO
     EmpresaDAO empresaDAO
     CurtidaDAO curtidaDAO
     MatchDAO matchDAO
     GerenciadorDeVagasService gerenciadorDeVagas

    GerenciadorDePerfisService(IConexao conexaoFactory) {
        this.conexaoFactory = conexaoFactory
        this.candidatoDAO = new CandidatoDAO(conexaoFactory)
        this.competenciaDAO = new CompetenciaDAO(conexaoFactory)
        this.empresaDAO = new EmpresaDAO(conexaoFactory)
        this.curtidaDAO = new CurtidaDAO(conexaoFactory)
        this.matchDAO = new MatchDAO(conexaoFactory)
        this.gerenciadorDeVagas = new GerenciadorDeVagasService(new VagaDAO(conexaoFactory), this.competenciaDAO)
    }


    void cadastroNovo(Scanner sc) {
        println("\n-- Opções de Cadastro --")
        println("1 - Candidato")
        println("2 - Empresa")
        print("Qual deseja cadastrar: ")

        if (!sc.hasNextInt()) {
            println("Erro: Entrada inválida. Retornando ao menu.")
            sc.next()
            return
        }

        int opcaoadd = sc.nextInt()
        sc.nextLine()

        if (opcaoadd == 1) {
            cadastrarCandidatoPeloTerminal(sc)
        } else if (opcaoadd == 2) {
            cadastrarEmpresaPeloTerminal(sc)
        } else {
            println("Opção inválida.")
        }
    }

    private void cadastrarCandidatoPeloTerminal(Scanner sc) {
        try {
            println "\n--- CADASTRAR NOVO CANDIDATO ---"

            print "Nome: "
            String nome = sc.nextLine().trim()
            ValidarCandidatoService.validarNome(nome)

            print "E-mail: "
            String email = sc.nextLine().trim()
            ValidarCandidatoService.validarEmail(email)

            print "CPF: "
            String cpf = sc.nextLine().trim()
            ValidarCandidatoService.validarCpf(cpf)

            print "Idade: "
            if (!sc.hasNextInt()) {
                sc.nextLine()
                throw new IllegalArgumentException("Idade deve ser um número inteiro.")
            }
            int idade = sc.nextInt()
            sc.nextLine()
            ValidarCandidatoService.validarIdade(idade)

            print "País: "
            String pais = sc.nextLine().trim()

            print "Estado: "
            String estado = sc.nextLine().trim()
            ValidarCandidatoService.validarEstado(estado)

            print "CEP: "
            String cep = sc.nextLine().trim()
            ValidarCandidatoService.validarCep(cep)

            print "Descrição pessoal: "
            String descricao = sc.nextLine().trim()
            ValidarCandidatoService.validarDescricao(descricao)

            Candidato novoCandidato = new Candidato(
                    nome: nome, email: email, cpf: cpf, idade: idade,
                    pais: pais, estado: estado, cep: cep, descricao: descricao
            )

            candidatoDAO.cadastrar(novoCandidato)
            println "Candidato cadastrado com sucesso!\n"

        } catch (IllegalArgumentException e) {
            println "Erro: ${e.message}"
        } catch (Exception e) {
            println "Erro inesperado: ${e.message}"
        }
    }

    private void cadastrarEmpresaPeloTerminal(Scanner sc) {
        try {
            println "\n--- CADASTRAR NOVA EMPRESA ---"

            print "Nome da Empresa: "
            String nome = sc.nextLine().trim()
            ValidarEmpresaService.validarNome(nome)

            print "E-mail Corporativo: "
            String email = sc.nextLine().trim()
            ValidarEmpresaService.validarEmail(email)

            print "CNPJ: "
            String cnpj = sc.nextLine().trim()
            ValidarEmpresaService.validarCnpj(cnpj)

            print "País: "
            String pais = sc.nextLine().trim()
            ValidarEmpresaService.validarPais(pais)

            print "Estado: "
            String estado = sc.nextLine().trim()
            ValidarEmpresaService.validarEstado(estado)

            print "CEP: "
            String cep = sc.nextLine().trim()
            ValidarEmpresaService.validarCep(cep)

            print "Descrição da empresa: "
            String descricao = sc.nextLine().trim()
            ValidarEmpresaService.validarDescricao(descricao)

            Empresa novaEmpresa = new Empresa(
                    nome: nome, email: email, cnpj: cnpj, pais: pais,
                    estado: estado, cep: cep, descricao: descricao
            )

            empresaDAO.cadastrar(novaEmpresa)
            println "Empresa cadastrada com sucesso!\n"

        } catch (IllegalArgumentException e) {
            println "Erro ao cadastrar empresa: ${e.message}"
        } catch (Exception e) {
            println "Erro no cadastro: ${e.message}"
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
            println "${indice + 1} - ${empresa.nome}"
        }

        print "Selecione a empresa: "

        return lerOpcaoDaLista(sc, empresas, "Erro: informe apenas o número da empresa.", "Empresa inválida.")
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
            println "${indice + 1} - ${candidato.nome}"
        }

        print "Selecione o candidato pelo ID: "

        return lerOpcaoDaLista(sc, candidatos, "Erro: informe apenas o número do candidato.", "Candidato inválido.")

    }

    Candidato selecionarCandidatoParaCurtir(Scanner sc) {
        List<Candidato> candidatos = candidatoDAO.listarTodos()

        if (candidatos.isEmpty()) {
            println "Nenhum candidato cadastrado ainda."
            return null
        }

        println "\n-- Candidatos Disponíveis --"

        candidatos.eachWithIndex { candidato, indice ->
            List<String> competencias = competenciaDAO.listarDoCandidato(candidato.cpf)

            println "${indice + 1} - Competências: " +
                    (competencias.isEmpty()
                            ? "Nenhuma competência cadastrada"
                            : competencias.join(', '))
        }

        print "Selecione o candidato: "

        return lerOpcaoDaLista(sc, candidatos, "Erro: informe apenas o número do candidato.", "Candidato inválido.")
    }

    void atualizarCandidatoPeloTerminal(Scanner sc, Candidato candidato) {
        try {
            print "Novo nome: "
            String nome = sc.nextLine().trim()
            ValidarCandidatoService.validarNome(nome)

            print "Novo e-mail: "
            String email = sc.nextLine().trim()
            ValidarCandidatoService.validarEmail(email)

            print "Nova idade: "
            if (!sc.hasNextInt()) {
                sc.nextLine()
                throw new IllegalArgumentException("Idade deve ser um número inteiro.")
            }

            int idade = sc.nextInt()
            sc.nextLine()
            ValidarCandidatoService.validarIdade(idade)

            print "Novo país: "
            String pais = sc.nextLine().trim()

            print "Novo estado: "
            String estado = sc.nextLine().trim()
            ValidarCandidatoService.validarEstado(estado)

            print "Novo CEP: "
            String cep = sc.nextLine().trim()
            ValidarCandidatoService.validarCep(cep)

            print "Nova descrição: "
            String descricao = sc.nextLine().trim()
            ValidarCandidatoService.validarDescricao(descricao)

            candidato.nome = nome
            candidato.email = email
            candidato.idade = idade
            candidato.pais = pais
            candidato.estado = estado
            candidato.cep = cep
            candidato.descricao = descricao

            candidatoDAO.atualizar(candidato)
            println "Candidato atualizado com sucesso!"

        } catch (IllegalArgumentException e) {
            println "Erro: ${e.message}"
        } catch (Exception e) {
            println "Erro inesperado: ${e.message}"
        }
    }

    void excluirCandidatoPeloTerminal(Scanner sc, Candidato candidato) {
        print "Tem certeza que deseja excluir o perfil de ${candidato.nome}? (S/N): "
        String confirmacao = sc.nextLine().trim().toUpperCase()

        if (confirmacao == "S") {
            candidatoDAO.excluir(candidato.cpf)
            println "Candidato excluído com sucesso!"
        } else {
            println "Exclusão cancelada."
        }
    }

    void atualizarEmpresaPeloTerminal(Scanner sc, Empresa empresa) {
        try {
            print "Novo nome da empresa: "
            String nome = sc.nextLine().trim()
            ValidarEmpresaService.validarNome(nome)

            print "Novo e-mail corporativo: "
            String email = sc.nextLine().trim()
            ValidarEmpresaService.validarEmail(email)

            print "Novo país: "
            String pais = sc.nextLine().trim()
            ValidarEmpresaService.validarPais(pais)

            print "Novo estado: "
            String estado = sc.nextLine().trim()
            ValidarEmpresaService.validarEstado(estado)

            print "Novo CEP: "
            String cep = sc.nextLine().trim()
            ValidarEmpresaService.validarCep(cep)

            print "Nova descrição: "
            String descricao = sc.nextLine().trim()
            ValidarEmpresaService.validarDescricao(descricao)

            empresa.nome = nome
            empresa.email = email
            empresa.pais = pais
            empresa.estado = estado
            empresa.cep = cep
            empresa.descricao = descricao

            empresaDAO.atualizar(empresa)
            println "Empresa atualizada com sucesso!"

        } catch (IllegalArgumentException e) {
            println "Erro ao atualizar empresa: ${e.message}"
        } catch (Exception e) {
            println "Erro inesperado: ${e.message}"
        }
    }

    void excluirEmpresaPeloTerminal(Scanner sc, Empresa empresa) {
        print "Tem certeza que deseja excluir o perfil da empresa ${empresa.nome}? (S/N): "
        String confirmacao = sc.nextLine().trim().toUpperCase()

        if (confirmacao == "S") {
            empresaDAO.excluir(empresa.cnpj)
            println "Empresa excluída com sucesso!"
        } else {
            println "Exclusão cancelada."
        }
    }

    List<String> listarCompetenciasDoCandidato(Candidato candidato) {
        return competenciaDAO.listarDoCandidato(candidato.cpf)
    }

    void adicionarCompetenciaAoCandidato(Candidato candidato, String competencia) {
        competenciaDAO.garantirCompetencia(competencia)
        competenciaDAO.adicionarAoCandidato(candidato.cpf, competencia)
    }

    void removerCompetenciaDoCandidato(Candidato candidato, String competencia) {
        competenciaDAO.removerDoCandidato(candidato.cpf, competencia)
    }

    List<String> listarCompetenciasDaEmpresa(Empresa empresa) {
        return competenciaDAO.listarDaEmpresa(empresa.cnpj)
    }

    void adicionarCompetenciaAEmpresa(Empresa empresa, String competencia) {
        competenciaDAO.garantirCompetencia(competencia)
        competenciaDAO.adicionarAoEmpresa(empresa.cnpj, competencia)
    }

    void removerCompetenciaDaEmpresa(Empresa empresa, String competencia) {
        competenciaDAO.removerDaEmpresa(empresa.cnpj, competencia)
    }

    void curtirVagaPeloTerminal(Scanner sc, Candidato candidato) {
        List<Vaga> vagas = gerenciadorDeVagas.listarVagas()

        if (vagas.isEmpty()) {
            println "Nenhuma vaga cadastrada ainda."
            return
        }

        println "\n-- Vagas Disponíveis --"

        vagas.eachWithIndex { vaga, indice ->
            println "${indice + 1} - ${vaga.titulo}"
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

        curtidaDAO.registrarCurtidaCandidato(candidato.cpf, vaga.id)
        processarPossivelMatch(candidato.cpf, vaga.empresa.cnpj, vaga.id, candidato.nome, vaga.empresa.nome)
    }

    void curtirCandidatoPeloTerminal(Scanner sc, Empresa empresa) {
        Candidato candidato = selecionarCandidatoParaCurtir(sc)

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

        Vaga vaga = lerOpcaoDaLista(sc, vagas, "Erro: informe apenas o número da vaga.", "Vaga inválida.")
        if (vaga == null) return

        curtidaDAO.registrarCurtidaEmpresa(candidato.cpf, empresa.cnpj)
        processarPossivelMatch(candidato.cpf, empresa.cnpj, vaga.id, empresa.nome, candidato.nome)
    }

     void processarPossivelMatch(String cpf, String cnpj, int vagaId, String nome1, String nome2) {
        if (curtidaDAO.verificarMatch(cpf, cnpj, vagaId)) {
            if (matchDAO.existeMatch(cpf, cnpj, vagaId)) {
                println "\nEsse match já está registrado."
            } else {
                matchDAO.registrarMatch(cpf, cnpj, vagaId)
                println "\n*** MATCH ENCONTRADO! ***"
                println "${nome1} e ${nome2} deram match!"
            }
        } else {
            println "\nVaga curtida com sucesso! Aguardando o match."
        }
    }
    private static <T> T lerOpcaoDaLista(Scanner sc, List<T> lista, String msgErroFormato, String msgErroInvalido) {
        if (!sc.hasNextInt()) {
            println msgErroFormato
            sc.nextLine()
            return null
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > lista.size()) {
            println msgErroInvalido
            return null
        }

        return lista[opcao - 1]
    }
}