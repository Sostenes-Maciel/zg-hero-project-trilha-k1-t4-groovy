package br.com.zg.acelera

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.dao.CompetenciaDAO
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

        GerenciadorDePerfisService perfilService = new GerenciadorDePerfisService(conexao)
        GerenciadorDeVagasService vagaService = new GerenciadorDeVagasService(new VagaDAO(conexao), new CompetenciaDAO(conexao))
        GerenciadorDeMatchesService matchService = new GerenciadorDeMatchesService(new MatchDAO(conexao))

        CandidatoController candidatoController = new CandidatoController(perfilService)
        EmpresaController empresaController = new EmpresaController(perfilService)
        VagaController vagaController = new VagaController(vagaService)
        MatchController matchController = new MatchController(matchService, perfilService)

        MenuView menu = new MenuView(candidatoController, empresaController, vagaController, matchController)
        menu.iniciar()
    }
}