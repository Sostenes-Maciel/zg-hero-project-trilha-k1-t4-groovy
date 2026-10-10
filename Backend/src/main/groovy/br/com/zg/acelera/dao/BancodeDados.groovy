package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Curtidas
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Match
import br.com.zg.acelera.model.Vaga

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


class BancodeDados {

    static List<Candidato> candidatos = []
    static List<Empresa> empresas = []

    static List<Vaga> vagas = []
    static List<Curtidas> curtidas = []
    static List<Match> matches = []

    BancodeDados() {

        candidatos.add(new Candidato(
                nome: "Sóstenes Maciel", idade: 29, email: "sosmarques@hotmail.com", cpf: "123.456.789-00",
                estado: "PE", cep: "55299-300", descricao: "Estagiário ZG.", competencias: ["Java, Groovy, Python"]
        ))
        candidatos.add(new Candidato(
                nome: "José da Silva", idade: 21, email: "jose@outlook.com", cpf: "111.222.333-49",
                estado: "SP", cep: 11222 - 333, descricao: "Estudante de ciẽncias da computação", competencias: ["Web design, Banco de dados, Javascript"]
        ))
        candidatos.add(new Candidato(
                nome: "Paulo André", idade: 33, email: "paulo@hotmail.com", cpf: "123.222.433-00",
                estado: "RJ", cep: "55210-300", descricao: "Estágiário de TI.", competencias: ["Java, Gradle, Metodologias Ágeis"]
        ))
        candidatos.add(new Candidato(
                nome: "Ana Julia", idade: 20, email: "anajj@hotmail.com", cpf: "333.444.789-00",
                estado: "BA", cep: "88855-000", descricao: "Recém formada em redes.", competencias: ["Python, Groovy, Web"]
        ))
        candidatos.add(new Candidato(
                nome: "Vitor Pereira ", idade: 40, email: "vitor@hotmail.com", cpf: "333.666.768-00",
                estado: "GO", cep: "65432-300", descricao: "Tutor acelera.", competencias: ["Java, Javascript, Angular"]
        ))


        //----EMPRESAS----

        empresas.add(new Empresa(
                nome: "Tech Global", email: "vagastech@gmail.com", estado: "RJ", cep: "32133-77",
                descricao: "Consultoria para TI internacional", competencias: ["Angular, JAvascript, Typescript"],
                cnpj: "98.765.432/0001-11", pais: "Brasil"
        ))
        empresas.add(new Empresa(
                nome: "S.O.S enterprises", email: "sosss@gmail.com", estado: "Los Angeles", cep: "89076",
                descricao: "Consultoria para TI internacional", competencias: ["Node, Java, Linux"],
                cnpj: "22.344.543/0001-11", pais: "Estados Unidos"
        ))
        empresas.add(new Empresa(
                nome: "BlueSky Technologies", email: "contato@blueskytech.com", estado: "California", cep: "90210",
                descricao: "Desenvolvimento de plataformas digitais e sistemas web", competencias: ["Java", "Spring Boot", "MySQL"],
                cnpj: "45.678.901/0001-34", pais: "Estados Unidos"
        ))

        empresas.add(new Empresa(
                nome: "GreenCode Solutions", email: "vagas@greencode.co.uk", estado: "England", cep: "SW1A 1AA",
                descricao: "Consultoria em tecnologia e desenvolvimento de software", competencias: ["Python", "Django", "React"],
                cnpj: "56.789.012/0001-45", pais: "Reino Unido"
        ))

        empresas.add(new Empresa(
                nome: "Tokyo Digital Labs", email: "jobs@tokyodigital.jp", estado: "Tokyo", cep: "100-0001",
                descricao: "Soluções de inteligência artificial e aplicações corporativas", competencias: ["JavaScript", "TypeScript", "AWS"],
                cnpj: "67.890.123/0001-56", pais: "Japão"
        ))


    }

    void cadastrarCandidato(Candidato candidato) {
        candidatos.add(candidato)
    }

    void cadastrarEmpresa(Empresa empresa) {
        empresas.add(empresa)
    }

    static class CompetenciaDAO {

        void cadastrar(String nome) {
            String sql = """
                INSERT INTO competencia (nome)
                VALUES (?)
            """

            try (Connection conexao = ConexaoBD.conectar();
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setString(1, nome)
                stmt.executeUpdate()
            }
        }

        List<String> listarTodos() {
            String sql = """
                SELECT nome
                FROM competencia
                ORDER BY nome
            """

            List<String> competencias = []

            try (Connection conexao = ConexaoBD.conectar();
                 PreparedStatement stmt = conexao.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    competencias.add(rs.getString("nome"))
                }
            }

            return competencias
        }

        void atualizar(String nomeAtual, String novoNome) {
            String sql = """
                UPDATE competencia
                SET nome = ?
                WHERE nome = ?
            """

            try (Connection conexao = ConexaoBD.conectar();
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setString(1, novoNome)
                stmt.setString(2, nomeAtual)
                stmt.executeUpdate()
            }
        }

        void excluir(String nome) {
            String sql = """
                DELETE FROM competencia
                WHERE nome = ?
            """

            try (Connection conexao = ConexaoBD.conectar();
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setString(1, nome)
                stmt.executeUpdate()
            }
        }
    }
}
