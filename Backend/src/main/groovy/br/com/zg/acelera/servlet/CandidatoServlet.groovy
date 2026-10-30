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

@WebServlet(name = "CandidatoServlet", urlPatterns = ["/api/candidatos", "/api/candidatos/login"])
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
        configurarResposta(response)
        String caminho = request.getServletPath()

        try {
            if (caminho != null && caminho.endsWith("/login")) {
                processarLogin(request, response)
            } else {
                processarCadastro(request, response)
            }
        } catch (Exception e) {
            enviarResposta(response, [status: 400, mensagem: "Erro ao processar a requisição: ${e.message}".toString()])
        }
    }

    private void processarCadastro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader()
        request.setCharacterEncoding("UTF-8")
        Candidato novoCandidato = gson.fromJson(reader, Candidato.class)
        Map resultado = candidatoController.cadastrar(novoCandidato)
        enviarResposta(response, resultado)
    }

    private void processarLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader()
        request.setCharacterEncoding("UTF-8")
        Map<String, String> credenciais = gson.fromJson(reader, Map.class)

        String email = credenciais.get("email")
        String cpf = credenciais.get("cpf")
        Map resultado = candidatoController.login(email, cpf)

        enviarResposta(response, resultado)
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json")
        resp.setCharacterEncoding("UTF-8")

        try {
            String cpfParam = req.getParameter("cpf")

            if (cpfParam != null && !cpfParam.trim().isEmpty()) {
                def candidato = candidatoController.buscarPorCpf(cpfParam)

                if (candidato) {
                    resp.setStatus(200)
                    resp.getWriter().write(gson.toJson(candidato))
                } else {
                    resp.setStatus(404)
                    def erro = [status: 404, mensagem: "Candidato não encontrado."]
                    resp.getWriter().write(gson.toJson(erro))
                }
            } else {
                def listaCandidatos = candidatoController.listar()

                resp.setStatus(200)
                resp.getWriter().write(gson.toJson(listaCandidatos))
            }

        } catch (Exception e) {
            e.printStackTrace()
            resp.setStatus(500)
            def erro = [status: 500, mensagem: "Erro interno: ${e.message}".toString()]
            resp.getWriter().write(gson.toJson(erro))
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            BufferedReader reader = req.getReader()
            Candidato candidatoAtualizado = gson.fromJson(reader, Candidato.class)

            Map resultado = candidatoController.atualizar(candidatoAtualizado)
            enviarResposta(resp, resultado)

        } catch (Exception e) {
            e.printStackTrace()
            enviarResposta(resp, [status: 500, mensagem: "Erro interno: ${e.message}".toString()])
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        configurarResposta(resp)
        try {
            String cpf = req.getParameter("cpf")

            if (cpf == null || cpf.trim().isEmpty()) {
                enviarResposta(resp, [status: 400, mensagem: "O CPF é obrigatório para exclusão."])
                return
            }

            Map resultado = candidatoController.excluir(cpf)
            enviarResposta(resp, resultado)

        } catch (Exception e) {
            e.printStackTrace()
            enviarResposta(resp, [status: 500, mensagem: "Erro interno: ${e.message}".toString()])
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