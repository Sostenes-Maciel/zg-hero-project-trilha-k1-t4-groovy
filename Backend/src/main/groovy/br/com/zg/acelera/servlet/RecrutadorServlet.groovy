package br.com.zg.acelera.servlet

import br.com.zg.acelera.controller.RecrutadorController
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.RecrutadorDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.service.GerenciadorDeRecrutadoresService
import com.google.gson.Gson
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import java.time.LocalDate

@WebServlet(name = "RecrutadorServlet",
        urlPatterns = ["/api/recrutadores", "/api/recrutadores/login"])
class RecrutadorServlet extends HttpServlet {

    RecrutadorController recrutadorController
    Gson gson

    @Override
    void init() {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")
        this.recrutadorController = new RecrutadorController(
                new GerenciadorDeRecrutadoresService(
                        new RecrutadorDAO(conexao),
                        new EmpresaDAO(conexao)
                )
        )
        this.gson = new Gson()
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        configurarResposta(response)
        try {
            Map payload = gson.fromJson(request.getReader(), Map)
            boolean login = request.getServletPath().endsWith("/login")
            Map resultado = login
                    ? recrutadorController.login(payload.email, payload.senha)
                    : recrutadorController.cadastrar(recrutador(payload), payload.empresaId)
            if (login && resultado.status == 200) {
                request.getSession(true).setAttribute("recrutador", resultado.recrutador)
            }
            if (resultado.recrutador) resultado.recrutador = dtoPublico(resultado.recrutador)
            enviarResposta(response, resultado)
        } catch (Exception e) {
            enviarResposta(response, [status: 400, mensagem: "Requisição inválida."])
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        configurarResposta(response)
        try {
            Integer id = request.getParameter("id")?.toInteger()
            String empresaId = request.getParameter("empresaId")
            enviarResposta(response, recrutadorController.inativar(id, empresaId))
        } catch (Exception e) {
            enviarResposta(response, [status: 400, mensagem: "ID e empresaId são obrigatórios."])
        }
    }

    private static Recrutador recrutador(Map payload) {
        new Recrutador(
                empresaId: payload.empresaId,
                nome: payload.nome,
                areaAtuacao: payload.areaAtuacao,
                nivelPermissao: payload.nivelPermissao,
                descricao: payload.descricao,
                email: payload.email,
                cpf: payload.cpf,
                dataNascimento: payload.dataNascimento ? LocalDate.parse(payload.dataNascimento) : null,
                senhaHash: payload.senha ?: payload.senhaHash
        )
    }

    private static void configurarResposta(HttpServletResponse response) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
    }

    private void enviarResposta(HttpServletResponse response, Map resultado) {
        response.status = resultado.status as int
        response.writer.write(gson.toJson(resultado))
    }

    private static Map dtoPublico(Recrutador recrutador) {
        [
                id: recrutador.id,
                empresaId: recrutador.empresaId,
                nome: recrutador.nome,
                areaAtuacao: recrutador.areaAtuacao,
                nivelPermissao: recrutador.nivelPermissao,
                descricao: recrutador.descricao,
                email: recrutador.email,
                ativo: recrutador.ativo
        ]
    }
}
