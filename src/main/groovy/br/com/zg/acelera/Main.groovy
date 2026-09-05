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

        }

    }


}


