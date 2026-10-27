package br.com.zg.acelera.servlet

import br.com.zg.acelera.controller.EmpresaController
import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.service.GerenciadorDeVagasService

import com.google.gson.Gson
import jakarta.servlet.ServletException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@WebServlet("/api/empresas")
class EmpresaServlet extends HttpServlet {

    EmpresaController empresaController
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

        this.empresaController = new EmpresaController(perfilService)
        this.gson = new Gson()
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json")
        resp.setCharacterEncoding("UTF-8")

        try {
            BufferedReader reader = req.getReader()

            Empresa novaEmpresa = gson.fromJson(reader, Empresa.class)

            Map resultado = empresaController.cadastrar(novaEmpresa)

            resp.setStatus(resultado.status as int)
            resp.getWriter().write(gson.toJson(resultado))

        } catch (Exception e) {
            e.printStackTrace()
            resp.setStatus(500)
            def erro = [status: 500, mensagem: "Erro interno no Servlet: ${e.message}".toString()]
            resp.getWriter().write(gson.toJson(erro))
        }
    }
}