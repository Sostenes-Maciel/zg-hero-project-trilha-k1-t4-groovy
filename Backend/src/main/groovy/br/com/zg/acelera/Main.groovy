package br.com.zg.acelera

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.service.GerenciadorDeVagasService
import br.com.zg.acelera.view.MenuView

class Main {
    static void main(String[] args) {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")

        CompetenciaDAO competenciaDAO = new CompetenciaDAO(conexao)

        CandidatoDAO candidatoDAO = new CandidatoDAO(conexao, competenciaDAO)
        EmpresaDAO empresaDAO = new EmpresaDAO(conexao)
        VagaDAO vagaDAO = new VagaDAO(conexao)
        CurtidaDAO curtidaDAO = new CurtidaDAO(conexao)
        MatchDAO matchDAO = new MatchDAO(conexao)

        GerenciadorDeVagasService vagaService = new GerenciadorDeVagasService(vagaDAO, competenciaDAO)
        GerenciadorDeMatchesService matchService = new GerenciadorDeMatchesService(matchDAO)
        GerenciadorDePerfisService perfilService = new GerenciadorDePerfisService(
                candidatoDAO, competenciaDAO, empresaDAO, curtidaDAO, matchDAO, vagaService
        )

        CandidatoController candidatoController = new CandidatoController(perfilService)
        EmpresaController empresaController = new EmpresaController(perfilService)
        VagaController vagaController = new VagaController(vagaService)
        MatchController matchController = new MatchController(matchService, perfilService)

        MenuView menu = new MenuView(candidatoController, empresaController, vagaController, matchController)
        menu.iniciar()
    }
}