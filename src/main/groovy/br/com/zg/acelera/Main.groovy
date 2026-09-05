package br.com.zg.acelera

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.repository.BancodeDados
import java.util.Scanner

class Main {

    static void main(String[] args) {

        BancodeDados dados = new BancodeDados()
        Scanner sc = new Scanner(System.in)
        int opcao = 0

        println "Bem-vindo ao Linkertinder <3\n"

        while (opcao != 3) {
            println("1 - Listar candidatos")
            println("2 - Listar Empresas")
            println("3 - Sair")
            print "\nEscolha uma opção: "

            opcao = sc.nextInt()
            sc.nextLine()

            switch (opcao) {
                case 1:
                    println("--Candidatos Cadastrados--\n")

                    dados.candidatos.each { escrever ->
                        println "Nome: ${escrever.nome} | CPF:  ${escrever.cpf} | Estado: ${escrever.estado}"
                        println "Competências: ${escrever.competencias.join(', ')}"
                        println "-" * 60
                    }
                    break

                case 2:
                    print("--Empresas Cadastradas--\n")

                    dados.empresas.each { escrever ->
                        println "Nome: ${escrever.nome} | CNPJ: ${escrever.cnpj} | País: ${escrever.pais}"
                        println "Competências desejadas: ${escrever.competencias.join(', ')}"
                        println "-" * 60
                    }
                    break

                case 3:
                    println("Saindo do Linketinder. Até logo!")
                    break

                default:
                    println("Opção inválida! tente novamente.")
            }

        }

    }

}




