package br.com.letscode;

import java.util.Scanner;

import br.com.letscode.dao.CarrinhoDAO;
import br.com.letscode.dao.ProdutoDAO;
import br.com.letscode.dao.PromocaoDAO;
import br.com.letscode.database.FileDatabase;
import br.com.letscode.database.MemoryDatabase;
import br.com.letscode.model.produto.Produto;
import br.com.letscode.model.produto.Promocao;
import br.com.letscode.model.system.Navigation;
import br.com.letscode.screens.ExitScreen;
import br.com.letscode.screens.ScreenInterface;
import br.com.letscode.screens.ScreensList;
import br.com.letscode.util.ConsoleUtil;

public class Aplicacao {
    private static void startDatabases() {
        ProdutoDAO.setDatabase(new FileDatabase<>(Produto.class, true));
        PromocaoDAO.setDatabase(new FileDatabase<>(Promocao.class, true));
        CarrinhoDAO.setDatabase(new MemoryDatabase<>());
    }

    public static void main(String[] args) throws Exception {
        startDatabases();

        ConsoleUtil.clearScreen();

        Navigation navigate = new Navigation(ScreensList.START, null);
        ScreenInterface screen;
        Scanner scanner = new Scanner(System.in);
        while (navigate.getScreen() != ScreensList.EXIT) {
            screen = navigate.getScreen().createInstance();
            navigate = screen.run(scanner, navigate.getArgs());
        }
        scanner.close();
        screen = new ExitScreen();
        screen.run(scanner, navigate.getArgs());
    }
}
