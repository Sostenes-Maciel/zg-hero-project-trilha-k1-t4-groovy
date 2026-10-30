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

@WebServlet(name = "EmpresaServlet", urlPatterns = ["/api/empresas", "/api/empresas/login"]) class EmpresaServlet extends HttpServlet {

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
        Empresa novaEmpresa = gson.fromJson(reader, Empresa.class)
        Map resultado = empresaController.cadastrar(novaEmpresa)
        enviarResposta(response, resultado)
    }

    private void processarLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader()
        request.setCharacterEncoding("UTF-8")
        Map<String, String> credenciais = gson.fromJson(reader, Map.class)

        String email = credenciais.get("email")
        String cnpj = credenciais.get("cnpj")

        Map resultado = empresaController.login(email, cnpj)

        enviarResposta(response, resultado)
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json")
        resp.setCharacterEncoding("UTF-8")

        try {
            String cnpjParam = req.getParameter("cnpj")

            if (cnpjParam != null && !cnpjParam.trim().isEmpty()) {
                def empresa = empresaController.buscarPorCnpj(cnpjParam)

                if (empresa) {
                    resp.setStatus(200)
                    resp.getWriter().write(gson.toJson(empresa))
                } else {
                    resp.setStatus(404)
                    def erro = [status: 404, mensagem: "Empresa não encontrada."]
                    resp.getWriter().write(gson.toJson(erro))
                }
            } else {
                def listaEmpresas = empresaController.listar()

                resp.setStatus(200)
                resp.getWriter().write(gson.toJson(listaEmpresas))
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
            Empresa empresaAtualizada = gson.fromJson(reader, Empresa.class)

            Map resultado = empresaController.atualizar(empresaAtualizada)
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
            String cnpj = req.getParameter("cnpj")

            if (cnpj == null || cnpj.trim().isEmpty()) {
                enviarResposta(resp, [status: 400, mensagem: "O CNPJ é obrigatório para exclusão."])
                return
            }

            Map resultado = empresaController.excluir(cnpj)
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