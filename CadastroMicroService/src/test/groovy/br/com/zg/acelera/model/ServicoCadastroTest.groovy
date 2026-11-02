package br.com.zg.acelera.model

import br.com.zg.acelera.model.CadastroInvalidoException
import br.com.zg.acelera.model.RepositorioCadastro
import br.com.zg.acelera.model.ServicoCadastro
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertThrows

class ServicoCadastroTest {
    @Test
    void deveCadastrarCandidatoComCamposExistentesNoBanco() {
        Map<String, Object> captured = [:]
        RepositorioCadastro repositorio = new RepositorioCapturador(
                { Map<String, Object> candidato -> captured.candidate = candidato },
                { Map<String, Object> ignored -> }
        )
        ServicoCadastro servico = new ServicoCadastro(repositorio)
        Map resultado = servico.cadastrarCandidato([
                cpf           : '12345678900',
                nome          : 'Batatiudo Exemplo',
                email         : 'batatiudo@example.com',
                pais          : 'Brasil',
                estado        : 'SP',
                 cep           : '01000000',
                 descricao     : 'Perfil de candidato',
                 idade         : 25,
                 competencias  : [' Java ', 'Groovy', 'Java'],
                 senha         : 'nao persistida'
        ])

        assertEquals('candidato', resultado.tipo)
        assertEquals('12345678900', resultado.identificador)
        int idadeCalculada = captured.candidate.idade as int
        assertEquals(25, idadeCalculada)
        assertEquals(
                ['cpf', 'nome', 'email', 'pais', 'estado', 'cep', 'descricao', 'idade', 'competencias'] as Set,
                captured.candidate.keySet()
        )
        assertEquals(['Java', 'Groovy'], captured.candidate.competencias)
    }

    @Test
    void deveCadastrarEmpresaComCamposExistentesNoBanco() {
        Map<String, Object> captured = [:]
        RepositorioCadastro repositorio = new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> empresa -> captured.company = empresa }
        )
        ServicoCadastro servico = new ServicoCadastro(repositorio)

        Map resultado = servico.cadastrarEmpresa([
                cnpj    : '12345678000199',
                nome    : 'Tapetes Exemplo',
                email   : 'contato@example.com',
                pais    : 'Brasil',
                 estado  : 'SP',
                 cep     : '01000000',
                 descricao: 'Fabricante de tapetes',
                 competencias: ['Producao', 'Logistica']
        ])

        assertEquals('empresa', resultado.tipo)
        assertEquals('12345678000199', resultado.identificador)
        assertEquals(
                ['cnpj', 'nome', 'email', 'pais', 'estado', 'cep', 'descricao', 'competencias'] as Set,
                captured.company.keySet()
        )
        assertEquals(['Producao', 'Logistica'], captured.company.competencias)
    }

    @Test
    void deveRejeitarIdadeForaDoIntervaloPermitido() {
        RepositorioCadastro repositorio = new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        )
        ServicoCadastro servico = new ServicoCadastro(repositorio)

        CadastroInvalidoException excecao = assertThrows(CadastroInvalidoException) {
            servico.cadastrarCandidato(dadosCandidato(idade: 17))
        }
        assertEquals('idade deve estar entre 18 e 120 anos.', excecao.message)
    }

    @Test
    void deveRemoverEspacosDosCamposAntesDePersistir() {
        Map<String, Object> captured = [:]
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> candidato -> captured.candidate = candidato },
                { Map<String, Object> ignored -> }
        ))

        servico.cadastrarCandidato(dadosCandidato(nome: '  Nome Completo  ', email: ' pessoa@example.com '))

        assertEquals('Nome Completo', captured.candidate.nome)
        assertEquals('pessoa@example.com', captured.candidate.email)
    }

    @Test
    void deveIgnorarSenhaEOutrosCamposQueNaoExistemNaTabela() {
        Map<String, Object> captured = [:]
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> candidato -> captured.candidate = candidato },
                { Map<String, Object> ignored -> }
        ))

        servico.cadastrarCandidato(dadosCandidato(senha: 'senha-secreta', campoExtra: 'nao persistir'))

        boolean senhaPersistida = captured.candidate.containsKey('senha') as boolean
        boolean campoExtraPersistido = captured.candidate.containsKey('campoExtra') as boolean
        assertFalse(senhaPersistida)
        assertFalse(campoExtraPersistido)
    }

    @ParameterizedTest
    @ValueSource(strings = ['cpf', 'nome', 'email', 'pais', 'estado', 'cep', 'descricao', 'idade'])
    void deveRejeitarCampoObrigatorioAusenteOuVazio(String campo) {
        Map<String, Object> payload = dadosCandidato()
        payload.remove(campo)
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarCandidato(payload)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ['', 'nao-e-email', 'pessoa@', '@exemplo.com', 'pessoa @exemplo.com'])
    void deveRejeitarEmailInvalido(String email) {
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarCandidato(dadosCandidato(email: email))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ['cpf', 'nome', 'email', 'pais', 'estado', 'cep'])
    void deveRejeitarTextoMaiorQueLimiteDaColuna(String campo) {
        Map<String, Object> payload = dadosCandidato()
        int limite = [
                cpf: 14, nome: 255, email: 255, pais: 100, estado: 100, cep: 20
        ][campo]
        payload[campo] = 'a' * (limite + 1)
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarCandidato(payload)
        }
    }

    @Test
    void deveRejeitarCnpjMaiorQueLimiteDaColuna() {
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarEmpresa(dadosEmpresa(cnpj: '1' * 19))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ['cnpj', 'nome', 'email', 'pais', 'estado', 'cep', 'descricao'])
    void deveRejeitarCampoObrigatorioAusenteNaEmpresa(String campo) {
        Map<String, Object> payload = dadosEmpresa()
        payload.remove(campo)
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarEmpresa(payload)
        }
    }

    @Test
    void deveRejeitarEmailInvalidoDaEmpresa() {
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarEmpresa(dadosEmpresa(email: 'email-invalido'))
        }
    }

    @Test
    void deveRejeitarCamposQueNaoForemTexto() {
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarCandidato(dadosCandidato(nome: 123))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ['17', '121', '-1'])
    void deveRejeitarIdadeForaDosLimitesDoFormulario(String idade) {
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarCandidato(dadosCandidato(idade: idade as Integer))
        }
    }

    @Test
    void deveRejeitarIdadeFracionaria() {
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignored -> },
                { Map<String, Object> ignored -> }
        ))

        assertThrows(CadastroInvalidoException) {
            servico.cadastrarCandidato(dadosCandidato(idade: 25.5))
        }
    }

    @Test
    void devePropagarFalhaDoRepositorioSemRetornarSucesso() {
        ServicoCadastro servico = new ServicoCadastro(new RepositorioCapturador(
                { Map<String, Object> ignorado -> throw new IllegalStateException('falha de persistencia') },
                { Map<String, Object> ignored -> }
        ))

        IllegalStateException excecao = assertThrows(IllegalStateException) {
            servico.cadastrarCandidato(dadosCandidato())
        }

        assertEquals('falha de persistencia', excecao.message)
    }

    private static Map<String, Object> dadosCandidato(Map<String, Object> substituicoes = [:]) {
        [
                cpf           : '12345678900',
                nome          : 'Batatiudo Exemplo',
                email         : 'batatiudo@example.com',
                pais          : 'Brasil',
                estado        : 'SP',
                cep           : '01000000',
                descricao     : 'Perfil de candidato',
                idade         : 25
        ] + substituicoes
    }

    private static Map<String, Object> dadosEmpresa(Map<String, Object> substituicoes = [:]) {
        [
                cnpj    : '12345678000199',
                nome    : 'Tapetes Exemplo',
                email   : 'contato@example.com',
                pais    : 'Brasil',
                estado  : 'SP',
                cep     : '01000000',
                descricao: 'Fabricante de tapetes'
        ] + substituicoes
    }

    private static class RepositorioCapturador implements RepositorioCadastro {
        private final Closure insercaoCandidato
        private final Closure insercaoEmpresa

        RepositorioCapturador(Closure insercaoCandidato, Closure insercaoEmpresa) {
            this.insercaoCandidato = insercaoCandidato
            this.insercaoEmpresa = insercaoEmpresa
        }

        @Override
        void inserirCandidato(Map<String, Object> candidato) {
            insercaoCandidato.call(candidato)
        }

        @Override
        void inserirEmpresa(Map<String, Object> empresa) {
            insercaoEmpresa.call(empresa)
        }
    }
}
