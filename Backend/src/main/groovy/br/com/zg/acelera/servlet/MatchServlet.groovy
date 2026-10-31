package br.com.zg.acelera.servlet

import br.com.zg.acelera.controller.MatchController
import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.service.GerenciadorDeVagasService
import com.google.gson.Gson
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@WebServlet("/api/matches")
class MatchServlet extends HttpServlet {

    MatchController matchController
    Gson gson

    @Override
    void init() {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")

        def competenciaDAO = new CompetenciaDAO(conexao)
        def candidatoDAO = new CandidatoDAO(conexao, competenciaDAO)
        def empresaDAO = new EmpresaDAO(conexao, competenciaDAO)
        def curtidaDAO = new CurtidaDAO(conexao)
        def matchDAO = new MatchDAO(conexao)
        def vagaDAO = new VagaDAO(conexao)

        def gerenciadorDeVagas = new GerenciadorDeVagasService(vagaDAO, competenciaDAO)

        def matchService = new GerenciadorDeMatchesService(matchDAO)

        def perfilService = new GerenciadorDePerfisService(
                candidatoDAO,
                competenciaDAO,
                empresaDAO,
                curtidaDAO,
                matchDAO,
                gerenciadorDeVagas
        )

        this.matchController = new MatchController(matchService, perfilService)
        this.gson = new Gson()
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            BufferedReader reader = req.getReader()
            Map payload = gson.fromJson(reader, Map.class)

            String autor = payload.autor
            Map resultado

            if (autor == "CANDIDATO") {
                String cpf = payload.cpfCandidato
                Integer idVaga = null

                if (payload.get("idVaga") != null) {
                    idVaga = Integer.valueOf(payload.get("idVaga").toString().replace(".0", ""))
                }

                resultado = matchController.curtirVaga(cpf, idVaga)

            } else if (autor == "EMPRESA") {
                String cnpj = payload.cnpjEmpresa
                String cpf = payload.cpfCandidato
                Integer idVaga = payload.idVaga ? payload.idVaga as Integer : null

                resultado = matchController.curtirCandidato(cnpj, cpf, idVaga)
            } else {
                resultado = [status: 400, mensagem: "Autor inválido. Informe 'CANDIDATO' ou 'EMPRESA' no JSON."]
            }

            enviarResposta(resp, resultado)

        } catch (Exception e) {
            e.printStackTrace()
            enviarResposta(resp, [status: 500, mensagem: "Erro interno: " + e.message])
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            String cpfParam = req.getParameter("cpf")
            String cnpjParam = req.getParameter("cnpj")
            Map resultado

            if (cpfParam != null && !cpfParam.trim().isEmpty()) {
                resultado = matchController.listarMatchesCandidato(cpfParam)
            } else if (cnpjParam != null && !cnpjParam.trim().isEmpty()) {
                resultado = matchController.listarMatchesEmpresa(cnpjParam)
            } else {
                resultado = [status: 400, mensagem: "Informe o ?cpf= ou ?cnpj= na URL para buscar os matches."]
            }

            enviarResposta(resp, resultado)

        } catch (Exception e) {
            e.printStackTrace()
            enviarResposta(resp, [status: 500, mensagem: "Erro interno: " + e.message])
        }
    }

    private static void configurarResposta(HttpServletResponse resp) {
        resp.setContentType("application/json")
        resp.setCharacterEncoding("UTF-8")
    }

    private void enviarResposta(HttpServletResponse resp, Map resultado) {
        resp.setStatus(resultado.status as int)
        resp.getWriter().write(gson.toJson(resultado))
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            BufferedReader reader = req.getReader()
            Map payload = gson.fromJson(reader, Map.class)

            String autor = payload.autor
            Map resultado

            if (autor == "EMPRESA") {
                String cnpj = payload.cnpjEmpresa
                String cpf = payload.cpfCandidato
                resultado = matchController.removerCurtidaEmpresa(cnpj, cpf)

            } else if (autor == "CANDIDATO") {
                String cpf = payload.cpfCandidato

                // Blindagem: Garante que o ID será um número inteiro perfeito, independente de como o Gson leu
                Integer idVaga = null
                if (payload.idVaga != null) {
                    idVaga = payload.idVaga.toString().toDouble().toInteger()
                }

                resultado = matchController.removerCurtidaCandidato(cpf, idVaga)
            } else {
                resultado = [status: 400, mensagem: "Autor inválido."]
            }

            enviarResposta(resp, resultado)

        } catch (Exception e) {
            e.printStackTrace()
            enviarResposta(resp, [status: 500, mensagem: "Erro interno: " + e.message])
        }
    }
}