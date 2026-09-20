import type {Candidato} from "../model/Candidato.ts";
import type {Empresa} from "../model/Empresa.ts";
import type {Vaga} from "../model/Vaga.ts";

export class BancodeDados {

    static candidatos: Candidato[] = [
        {
            nome: 'Sóstenes Maciel',
            email: 'sosmarques@hotmail.com',
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
}