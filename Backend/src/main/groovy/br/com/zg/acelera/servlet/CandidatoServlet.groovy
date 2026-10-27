package br.com.zg.acelera.servlet

import br.com.zg.acelera.controller.CandidatoController
import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.service.GerenciadorDeVagasService
import com.google.gson.Gson

import jakarta.servlet.ServletException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@WebServlet(name = "CandidatoServlet", urlPatterns = "/api/candidatos")
class CandidatoServlet extends HttpServlet {

     CandidatoController candidatoController
     Gson gson


    @Override
    void init() throws ServletException {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")

        def competenciaDAO = new CompetenciaDAO(conexao)
        def candidatoDAO = new CandidatoDAO(conexao, competenciaDAO)
        def empresaDAO = new EmpresaDAO(conexao, competenciaDAO)
        def curtidaDAO = new CurtidaDAO(conexao)
        def matchDAO = new MatchDAO(conexao)
        def vagaDAO = new VagaDAO(conexao)

        def gerenciadorDeVagas = new GerenciadorDeVagasService(vagaDAO, competenciaDAO)

        def perfilService = new GerenciadorDePerfisService(
                candidatoDAO,
                competenciaDAO,
                empresaDAO,
                curtidaDAO,
                matchDAO,
                gerenciadorDeVagas
        )

        this.candidatoController = new CandidatoController(perfilService)
        this.gson = new Gson()
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        try {
            BufferedReader reader = request.getReader()

            Candidato novoCandidato = gson.fromJson(reader, Candidato.class)

            Map resultado = candidatoController.cadastrar(novoCandidato)

            response.setStatus(resultado.status as int)

            response.getWriter().write(gson.toJson(resultado))

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            response.getWriter().write(gson.toJson([status: 400, mensagem: "Erro ao processar o JSON: ${e.message}"]))
        }
    }
}