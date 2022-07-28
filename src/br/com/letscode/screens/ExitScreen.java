package br.com.letscode.screens;

import java.util.Scanner;

import br.com.letscode.model.Navigation;
import br.com.letscode.util.ConsoleUtil;
import br.com.letscode.util.StringUtil;
import br.com.letscode.util.SystemInterfaceUtil;

public class ExitScreen implements ScreenInterface {
    private static final String EXIT_MESSAGE = "Muito obrigado por utilizar o Sommus Market!!!";

    private static void draw() {
        ConsoleUtil.clearScreen();
        ConsoleUtil.scrollScreen();

        System.out.print(ConsoleUtil.NEW_LINE
                + StringUtil.centralize(EXIT_MESSAGE,
                        SystemInterfaceUtil.DEFAULT_CONSOLE_WIDTH)
                + ConsoleUtil.NEW_LINE);

        System.out.print(ConsoleUtil.Attribute.FCOL_GREEN.getEscapeCode()
                + ConsoleUtil.Attribute.BLINK.getEscapeCode()
                + StringUtil.addBlankSpacesToAllLines(SystemInterfaceUtil.SYSTEM_LOGO, 8)
                + ConsoleUtil.NEW_LINE
                + ConsoleUtil.Attribute.RESET.getEscapeCode() + ConsoleUtil.NEW_LINE);
    }

    public Navigation run(Scanner scanner, String[] args) {
        draw();

        return new Navigation(null, null);
    }
}
