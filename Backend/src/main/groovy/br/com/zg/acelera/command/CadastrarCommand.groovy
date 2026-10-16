package br.com.zg.acelera.command

import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.service.GerenciadorDePerfisService

class CadastrarCommand implements ICommand {

    private final GerenciadorDePerfisService gerenciador
    private final Scanner sc

    CadastrarCommand(GerenciadorDePerfisService gerenciador, Scanner sc) {
        this.gerenciador = gerenciador
        this.sc = sc
    }

    @Override
    void executar() {
        gerenciador.cadastroNovo(sc)
    }
}