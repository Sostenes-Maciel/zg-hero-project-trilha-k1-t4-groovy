import type {Candidato} from "../model/Candidato.ts";
import type {Empresa} from "../model/Empresa.ts";
import type {Vaga} from "../model/Vaga.ts";
import type {Curtida} from "../model/Curtida.ts";
import type {Match} from "../model/Match.ts";

export class BancodeDados {

    static candidatos: Candidato[] = [
        {
            nome: 'Sóstenes Maciel',
            email: 'sosmarques@hotmail.com',
            pais: 'Brasil',
            estado: 'PE',
            cep: '55299-300',
            descricao: 'Estagiário ZG.',
            competencias: ['Java', 'Groovy', 'Python'],
            idade: 29,
            cpf: '123.456.789-00'
        },
        {
            nome: 'José da Silva',
            email: 'jose@outlook.com',
            pais: 'Brasil',
            estado: 'SP',
            cep: '11222-333',
            descricao: 'Estudante de ciências da computação',
            competencias: ['Web design', 'Banco de dados', 'Javascript'],
            idade: 21,
            cpf: '111.222.333-49'
        },
        {
            nome: 'Paulo André',
            email: 'paulo@hotmail.com',
            pais: 'Brasil',
            estado: 'RJ',
            cep: '55210-300',
            descricao: 'Estagiário de TI.',
            competencias: ['Java', 'Gradle', 'Metodologias Ágeis'],
            idade: 33,
            cpf: '123.222.433-00'
        },
        {
            nome: 'Ana Julia',
            email: 'anajj@hotmail.com',
            pais: 'Brasil',
            estado: 'BA',
            cep: '88855-000',
            descricao: 'Recém formada em redes.',
            competencias: ['Python', 'Groovy', 'Web'],
            idade: 20,
            cpf: '333.444.789-00'
        },
        {
            nome: 'Vitor Pereira',
            email: 'vitor@hotmail.com',
            pais: 'Brasil',
            estado: 'GO',
            cep: '65432-300',
            descricao: 'Tutor acelera.',
            competencias: ['Java', 'Javascript', 'Angular'],
            idade: 40,
            cpf: '333.666.768-00'
        }
    ]

    static empresas: Empresa[] = [
        {
            nome: 'Tech Global',
            email: 'vagastech@gmail.com',
            estado: 'RJ',
            cep: '32133-77',
            descricao: 'Consultoria para TI internacional',
            competencias: ['Angular', 'Javascript', 'Typescript'],
            cnpj: '98.765.432/0001-11',
            pais: 'Brasil'
        },
        {
            nome: 'S.O.S enterprises',
            email: 'sosss@gmail.com',
            estado: 'Los Angeles',
            cep: '89076',
            descricao: 'Consultoria para TI internacional',
            competencias: ['Node', 'Java', 'Linux'],
            cnpj: '22.344.543/0001-11',
            pais: 'Estados Unidos'
        },
        {
            nome: 'BlueSky Technologies',
            email: 'contato@blueskytech.com',
            estado: 'California',
            cep: '90210',
            descricao: 'Desenvolvimento de plataformas digitais e sistemas web',
            competencias: ['Java', 'Spring Boot', 'MySQL'],
            cnpj: '45.678.901/0001-34',
            pais: 'Estados Unidos'
        },
        {
            nome: 'GreenCode Solutions',
            email: 'vagas@greencode.co.uk',
            estado: 'England',
            cep: 'SW1A 1AA',
            descricao: 'Consultoria em tecnologia e desenvolvimento de software',
            competencias: ['Python', 'Django', 'React'],
            cnpj: '56.789.012/0001-45',
            pais: 'Reino Unido'
        },
        {
            nome: 'Tokyo Digital Labs',
            email: 'jobs@tokyodigital.jp',
            estado: 'Tokyo',
            cep: '100-0001',
            descricao: 'Soluções de inteligência artificial e aplicações corporativas',
            competencias: ['JavaScript', 'TypeScript', 'AWS'],
            cnpj: '67.890.123/0001-56',
            pais: 'Japão'
        }
    ]

    static vagas: Vaga[] = []
    static curtidas: Curtida[] = []
    static matches: Match[] = []

    static inicializar(): void {
        const candidatosSalvos = localStorage.getItem('candidatos')
        const empresasSalvas = localStorage.getItem('empresas')
        const vagasSalvas = localStorage.getItem('vagas')

        if (candidatosSalvos) {
            this.candidatos = JSON.parse(candidatosSalvos)
        } else {
            this.salvarCandidatos()
        }

        if (empresasSalvas) {
            this.empresas = JSON.parse(empresasSalvas)
        } else {
            this.salvarEmpresas()
        }

        if (vagasSalvas) {
            this.vagas = JSON.parse(vagasSalvas)
        } else {
            this.vagas = [
                {
                    id: 0,
                    titulo: 'Desenvolvedor Full Stack',
                    descricao: 'Atuação no desenvolvimento de aplicações web, trabalhando tanto no backend quanto no frontend, integração com APIs, bancos de dados e manutenção das funcionalidades do sistema.',
                    pais: 'Brasil',
                    estado: 'PE',
                    competencias: ['Java', 'TypeScript', 'Git', 'SQL'],
                    empresa: this.empresas[0]
                },
                {
                    id: 1,
                    titulo: 'Desenvolvedor Java Júnior',
                    pais: 'Brasil',
                    estado: 'SP',
                    descricao: 'profissional em início de carreira focado em criar, corrigir e manter sistemas back-end utilizando a linguagem Java, sempre com o suporte de desenvolvedores mais experientes.Principais ResponsabilidadesDesenvolvimento de rotinas: Implementar pequenas funcionalidades e componentes definidos pela equipe sênior.Correção de bugs: Identificar e resolver falhas simples no código existente.Testes e depuração: Realizar testes unitários e validar se o código funciona corretamente.Documentação: Escrever registros básicos sobre as alterações e funcionalidades desenvolvidas.',
                    competencias: ['Java', 'Angular', 'React', 'English'],
                    empresa: this.empresas[1]
                },
                {
                    id: 2,
                    titulo: 'Desenvolvedor Backend Node.js',
                    descricao: 'Atuação no desenvolvimento e manutenção de aplicações backend, criação de APIs, integração com serviços e implementação de novas funcionalidades utilizando Node.js.',
                    pais: 'Brasil',
                    estado: 'PE',
                    competencias: ['Node.js', 'JavaScript', 'Git'],
                    empresa: this.empresas[2]

                },
                {
                    id: 3,
                    titulo: 'Desenvolvedor Java Spring Boot',
                    descricao: 'Desenvolvimento e manutenção de aplicações backend utilizando Java e Spring Boot, criação de APIs, integração com bancos de dados e implementação de novas funcionalidades.',
                    pais: 'Brasil',
                    estado: 'PE',
                    competencias: ['Java', 'Spring Boot', 'Git'],
                    empresa: this.empresas[3]
                },
                {
                    id: 4,
                    titulo: 'Desenvolvedor Python',
                    descricao: 'Desenvolvimento de aplicações e soluções utilizando Python, trabalhando na criação de funcionalidades, integração com serviços e manutenção do código da aplicação.',
                    pais: 'Brasil',
                    estado: 'PE',
                    competencias: ['Python', 'Git', 'SQL'],
                    empresa: this.empresas[4]
                },
                {
                    id: 5,
                    titulo: 'Desenvolvedor TypeScript',
                    descricao: 'Desenvolvimento de aplicações utilizando TypeScript e JavaScript, criação de funcionalidades, integração com APIs e manutenção da estrutura do projeto.',
                    pais: 'Brasil',
                    estado: 'PE',
                    competencias: ['TypeScript', 'JavaScript', 'Git'],
                    empresa: this.empresas[5]
                },

            ]

            this.salvarVagas()
        }
    }

    static cadastrarCandidato(candidato: Candidato): void {
        this.candidatos.push(candidato)

        try {
            this.salvarCandidatos()
        } catch (erro) {
            this.candidatos.pop()
            console.error('Erro ao salvar candidato:', erro)
            throw new Error('Não foi possível salvar o candidato.')
        }
    }

    static cadastrarEmpresa(empresa: Empresa): void {
        this.empresas.push(empresa)

        try {
            this.salvarEmpresas()
        } catch (erro) {
            this.empresas.pop()
            console.error('Erro ao salvar empresa:', erro)
            throw new Error('Não foi possível salvar a empresa.')
        }
        this.salvarEmpresas()
    }

    static cadastrarVaga(vaga: Vaga): void {
        this.vagas.push(vaga)

        try {
            this.salvarVagas()
        } catch (erro) {
            this.vagas.pop()
            console.error('Erro ao salvar vaga:', erro)
            throw new Error('Não foi possível salvar a vaga.')
        }
    }

    private static salvarCandidatos(): void {
        localStorage.setItem('candidatos', JSON.stringify(this.candidatos))
    }

    private static salvarEmpresas(): void {
        localStorage.setItem('empresas', JSON.stringify(this.empresas))
    }

    private static salvarVagas(): void {
        localStorage.setItem('vagas', JSON.stringify(this.vagas))
    }

    static excluirCandidato(cpf: string): void {
        const indice = this.candidatos.findIndex(
            candidato => candidato.cpf === cpf
        )

        if (indice === -1) {
            throw new Error('Candidato não encontrado.')
        }

        const candidatoRemovido = this.candidatos.splice(indice, 1)[0]

        try {
            this.salvarCandidatos()
        } catch (erro) {
            this.candidatos.splice(indice, 0, candidatoRemovido)
            console.error('Erro ao excluir candidato:', erro)
            throw new Error('Não foi possível excluir o candidato.')
        }
    }

    static excluirEmpresa(cnpj: string): void {
        const indice = this.empresas.findIndex(
            empresa => empresa.cnpj === cnpj
        )

        if (indice === -1) {
            throw new Error('Empresa não encontrada.')
        }

        const empresaRemovida = this.empresas.splice(indice, 1)[0]

        try {
            this.salvarEmpresas()
        } catch (erro) {
            this.empresas.splice(indice, 0, empresaRemovida)
            console.error('Erro ao excluir empresa:', erro)
            throw new Error('Não foi possível excluir a empresa.')
        }
    }

    static curtirVaga(
        candidato: Candidato,
        vaga: Vaga
    ): void {
        const matchExistente = this.matches.some(
            match =>
                match.candidato.cpf === candidato.cpf &&
                match.vaga.id === vaga.id
        )

        if (matchExistente) {
            return
        }

        const indiceCurtida = this.curtidas.findIndex(
            curtida =>
                curtida.candidato.cpf === candidato.cpf &&
                curtida.vaga?.id === vaga.id
        )

        if (indiceCurtida !== -1) {
            this.curtidas.splice(indiceCurtida, 1)
            return
        }

        this.curtidas.push({
            candidato,
            vaga
        })

        this.verificarMatch(candidato, vaga)
    }

    static curtirCandidato(
        empresa: Empresa,
        candidato: Candidato
    ): void {
        const matchExistente = this.matches.some(
            match =>
                match.empresa.cnpj === empresa.cnpj &&
                match.candidato.cpf === candidato.cpf
        )

        if (matchExistente) {
            return
        }

        const indiceCurtida = this.curtidas.findIndex(
            curtida =>
                curtida.empresa?.cnpj === empresa.cnpj &&
                curtida.candidato.cpf === candidato.cpf
        )

        if (indiceCurtida !== -1) {
            this.curtidas.splice(indiceCurtida, 1)
            return
        }

        this.curtidas.push({
            candidato,
            empresa
        })

        const vagasCurtidas = this.curtidas
            .filter(
                curtida =>
                    curtida.candidato.cpf === candidato.cpf &&
                    curtida.vaga?.empresa.cnpj === empresa.cnpj
            )
            .map(curtida => curtida.vaga)

        vagasCurtidas.forEach(vaga => {
            if (vaga) {
                this.verificarMatch(candidato, vaga)
            }
        })
    }

    static verificarMatch(
        candidato: Candidato,
        vaga: Vaga
    ): void {
        const empresa = vaga.empresa

        const candidatoCurtiuVaga = this.curtidas.some(
            curtida =>
                curtida.candidato.cpf === candidato.cpf &&
                curtida.vaga?.id === vaga.id
        )

        const empresaCurtiuCandidato = this.curtidas.some(
            curtida =>
                curtida.empresa?.cnpj === empresa.cnpj &&
                curtida.candidato.cpf === candidato.cpf
        )

        if (!candidatoCurtiuVaga || !empresaCurtiuCandidato) {
            return
        }

        const matchExistente = this.matches.some(
            match =>
                match.candidato.cpf === candidato.cpf &&
                match.empresa.cnpj === empresa.cnpj &&
                match.vaga.id === vaga.id
        )

        if (matchExistente) {
            return
        }

        this.matches.push({
            candidato,
            empresa,
            vaga
        })
    }

    static candidatoCurtiuVaga(
        candidato: Candidato,
        vaga: Vaga
    ): boolean {
        return this.curtidas.some(
            curtida =>
                curtida.candidato.cpf === candidato.cpf &&
                curtida.vaga?.id === vaga.id
        )
    }

    static candidatoTemMatchComVaga(
        candidato: Candidato,
        vaga: Vaga
    ): boolean {
        return this.matches.some(
            match =>
                match.candidato.cpf === candidato.cpf &&
                match.vaga.id === vaga.id
        )
    }

    static empresaCurtiuCandidato(
        empresa: Empresa,
        candidato: Candidato
    ): boolean {
        return this.curtidas.some(
            curtida =>
                curtida.empresa?.cnpj === empresa.cnpj &&
                curtida.candidato.cpf === candidato.cpf
        )
    }

    static empresaTemMatchComCandidato(
        empresa: Empresa,
        candidato: Candidato
    ): boolean {
        return this.matches.some(
            match =>
                match.empresa.cnpj === empresa.cnpj &&
                match.candidato.cpf === candidato.cpf
        )
    }
    static getMatchesDoCandidato(
        candidato: Candidato
    ): Match[] {
        return this.matches.filter(
            match => match.candidato.cpf === candidato.cpf
        )
    }

    static getMatchesDaEmpresa(
        empresa: Empresa
    ): Match[] {
        return this.matches.filter(
            match => match.empresa.cnpj === empresa.cnpj
        )
    }
    static getCurtidasPendentesDoCandidato(
        candidato: Candidato
    ): Curtida[] {
        return this.curtidas.filter(curtida => {
            if (
                !curtida.vaga ||
                curtida.candidato.cpf !== candidato.cpf
            ) {
                return false
            }

            return !this.matches.some(
                match =>
                    match.candidato.cpf === candidato.cpf &&
                    match.vaga.id === curtida.vaga!.id
            )
        })
    }

    static getCurtidasPendentesDaEmpresa(
        empresa: Empresa
    ): Curtida[] {
        return this.curtidas.filter(curtida => {
            if (
                !curtida.empresa ||
                curtida.empresa.cnpj !== empresa.cnpj
            ) {
                return false
            }

            return !this.matches.some(
                match =>
                    match.empresa.cnpj === empresa.cnpj &&
                    match.candidato.cpf === curtida.candidato.cpf
            )
        })
    }
}