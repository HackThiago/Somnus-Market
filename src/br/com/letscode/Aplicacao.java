package br.com.letscode;

import java.util.Scanner;

import br.com.letscode.model.Navigation;
import br.com.letscode.screens.ExitScreen;
import br.com.letscode.screens.ScreenInterface;
import br.com.letscode.screens.ScreensList;
import br.com.letscode.util.ConsoleUtil;

public class Aplicacao {
    public static void main(String[] args) throws Exception {
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
