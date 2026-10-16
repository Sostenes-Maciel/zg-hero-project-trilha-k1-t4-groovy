package br.com.zg.acelera.command

import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.service.GerenciadorDePerfisService
import java.util.Scanner

class GerenciarVagasCommand implements ICommand {

    private final GerenciadorDePerfisService gerenciador
    private final Scanner sc

    GerenciarVagasCommand(GerenciadorDePerfisService gerenciador, Scanner sc) {
        this.gerenciador = gerenciador
        this.sc = sc
    }

    @Override
    void executar() {
        gerenciador.gerenciarVagas(sc)
    }
}