package br.com.zg.acelera.command

import br.com.zg.acelera.interfaces.ICommand
import br.com.zg.acelera.view.MenuView
import java.util.Scanner

class GerenciarEmpresasCommand implements ICommand {

    private final MenuView menuView
    private final Scanner sc

    GerenciarEmpresasCommand(MenuView menuView, Scanner sc) {
        this.menuView = menuView
        this.sc = sc
    }

    @Override
    void executar() {
        menuView.gerenciarEmpresas(sc)
    }
}