package br.com.zg.acelera.servlet

import br.com.zg.acelera.controller.VagaController
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDeVagasService
import com.google.gson.Gson
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@WebServlet("/api/vagas")
class VagaServlet extends HttpServlet {

    VagaController vagaController
    Gson gson

    @Override
    void init() {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")

        def vagaDAO = new VagaDAO(conexao)
        def competenciaDAO = new CompetenciaDAO(conexao)

        def vagaService = new GerenciadorDeVagasService(vagaDAO, competenciaDAO)

        this.vagaController = new VagaController(vagaService)
        this.gson = new Gson()
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            BufferedReader reader = req.getReader()
            Vaga novaVaga = gson.fromJson(reader, Vaga.class)

            Map resultado = vagaController.cadastrar(novaVaga)
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
            String cnpjParam = req.getParameter("cnpj")
            Map resultado

            if (cnpjParam != null && !cnpjParam.trim().isEmpty()) {
                resultado = vagaController.listarDaEmpresa(cnpjParam)
            } else {
                resultado = vagaController.listar()
            }

            enviarResposta(resp, resultado)
        } catch (Exception e) {
            e.printStackTrace()
            enviarResposta(resp, [status: 500, mensagem: "Erro interno: " + e.message])
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            BufferedReader reader = req.getReader()
            Vaga vagaAtualizada = gson.fromJson(reader, Vaga.class)

            Map resultado = vagaController.atualizar(vagaAtualizada)
            enviarResposta(resp, resultado)
        } catch (Exception e) {
            e.printStackTrace()
            enviarResposta(resp, [status: 500, mensagem: "Erro interno: " + e.message])
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            String idParam = req.getParameter("id")

            if (idParam == null || idParam.trim().isEmpty()) {
                enviarResposta(resp, [status: 400, mensagem: "O ID da vaga é obrigatório para exclusão."])
                return
            }

            Integer id = Integer.parseInt(idParam)
            Map resultado = vagaController.excluir(id)
            enviarResposta(resp, resultado)

        } catch (NumberFormatException ignored) {
            enviarResposta(resp, [status: 400, mensagem: "O ID da vaga deve ser um número válido."])
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
}