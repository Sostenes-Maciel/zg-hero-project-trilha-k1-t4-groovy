package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class GerenciadorDeVagasService {

    private final IConexao conexaoFactory
    private final VagaDAO vagaDAO
    private final CompetenciaDAO competenciaDAO

    GerenciadorDeVagasService(IConexao conexaoFactory) {
        this.conexaoFactory = conexaoFactory
        this.vagaDAO = new VagaDAO(conexaoFactory)
        this.competenciaDAO = new CompetenciaDAO(conexaoFactory)
    }

    Integer cadastrarVaga(Vaga vaga) {
        vagaDAO.cadastrar(vaga)

        Integer idVaga = vaga.id

        vaga.competencia.each { competencia ->
            competenciaDAO.garantirCompetencia(competencia)
            competenciaDAO.adicionarAVaga(idVaga, competencia)
        }

        return idVaga
    }

    List<Vaga> listarVagas() {
        List<Vaga> vagas = vagaDAO.listarTodos()

        vagas.each { vaga ->
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vagas
    }

    List<Vaga> listarVagasDaEmpresa(String cnpj) {
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(cnpj)

        vagas.each { vaga ->
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vagas
    }

    Vaga buscarVaga(Integer id) {
        Vaga vaga = vagaDAO.buscarPorId(id)

        if (vaga != null) {
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vaga
    }

    void atualizarVaga(Vaga vaga) {
        vagaDAO.atualizar(vaga)
    }

    void excluirVaga(Integer id) {
        vagaDAO.excluir(id)
    }

    void cadastrarVagaPeloTerminal(Scanner sc, Empresa empresa) {
        println "\n--- CADASTRAR NOVA VAGA ---"
        println "Empresa: ${empresa.nome}"

        print "Título da vaga: "
        String titulo = sc.nextLine().trim()

        print "País: "
        String pais = sc.nextLine().trim()

        print "Estado: "
        String estado = sc.nextLine().trim()

        print "Descrição: "
        String descricao = sc.nextLine().trim()

        print "Competências exigidas: "
        String entradaComps = sc.nextLine()

        List<String> competencias = extrairCompetencias(entradaComps)

        Vaga vaga = new Vaga(
                titulo: titulo,
                pais: pais,
                estado: estado,
                descricao: descricao,
                competencia: competencias,
                empresa: empresa
        )

        Integer idVaga = cadastrarVaga(vaga)

        println "Vaga cadastrada com sucesso!"
        println "ID da vaga: ${idVaga}"
    }

    void listarVagasPeloTerminal(Empresa empresa) {
        List<Vaga> vagas = listarVagasDaEmpresa(empresa.cnpj)

        println "\n--- VAGAS DA EMPRESA: ${empresa.nome} ---"

        if (vagas.isEmpty()) {
            println "Nenhuma vaga cadastrada."
            return
        }

        vagas.each { vaga ->
            println "ID: ${vaga.id}"
            println "Título: ${vaga.titulo}"
            println "País: ${vaga.pais}"
            println "Estado: ${vaga.estado}"
            println "Descrição: ${vaga.descricao}"
            println "Competências: ${vaga.competencia.join(', ')}"
            println "-" * 60
        }
    }

    void atualizarVagaPeloTerminal(Scanner sc, Empresa empresa) {
        listarVagasPeloTerminal(empresa)

        Vaga vaga = solicitarEValidarVaga(sc, empresa)
        if (vaga == null) return

        List<String> competenciasAntigas = new ArrayList<>(vaga.competencia)

        print "Novo título: "
        vaga.titulo = sc.nextLine().trim()

        print "Novo país: "
        vaga.pais = sc.nextLine().trim()

        print "Novo estado: "
        vaga.estado = sc.nextLine().trim()

        print "Nova descrição: "
        vaga.descricao = sc.nextLine().trim()

        print "Novas competências (separe por vírgula): "
        String entradaComps = sc.nextLine()

        vaga.competencia = extrairCompetencias(entradaComps)

        vagaDAO.atualizar(vaga)

        competenciasAntigas.each { competencia ->
            competenciaDAO.removerDaVaga(vaga.id, competencia)
        }

        vaga.competencia.each { competencia ->
            competenciaDAO.garantirCompetencia(competencia)
            competenciaDAO.adicionarAVaga(vaga.id, competencia)
        }

        println "Vaga atualizada com sucesso!"
    }

    void excluirVagaPeloTerminal(Scanner sc, Empresa empresa) {
        listarVagasPeloTerminal(empresa)

        Vaga vaga = solicitarEValidarVaga(sc, empresa)
        if (vaga == null) return

        excluirVaga(vaga.id)
        println "Vaga excluída com sucesso!"
    }

    private List<String> extrairCompetencias(String entrada) {
        if (!entrada) return []
        return entrada.tokenize(',').collect { it.trim() }.findAll { it }
    }

    private Vaga solicitarEValidarVaga(Scanner sc, Empresa empresa) {
        print "\nInforme o ID da vaga: "

        if (!sc.hasNextInt()) {
            println "ID inválido."
            sc.nextLine()
            return null
        }

        Integer id = sc.nextInt()
        sc.nextLine()

        Vaga vaga = buscarVaga(id)

        if (vaga == null || vaga.empresa.cnpj != empresa.cnpj) {
            println "Vaga não encontrada para esta empresa."
            return null
        }

        return vaga
    }
}