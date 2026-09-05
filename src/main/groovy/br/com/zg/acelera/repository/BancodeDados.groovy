package br.com.zg.acelera.repository

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa


class BancodeDados {

    List<Candidato> candidatos = []
    List<Empresa> empresas = []

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
}
