package br.com.letscode.screens;

import java.util.NoSuchElementException;
import java.util.Scanner;

import br.com.letscode.exception.ExitSignalException;
import br.com.letscode.exception.GoBackSignalException;
import br.com.letscode.model.system.ConsolePosition;
import br.com.letscode.model.system.Message;
import br.com.letscode.model.system.MessageType;
import br.com.letscode.model.system.Navigation;
import br.com.letscode.util.ConsoleUtil;
import br.com.letscode.util.StringUtil;
import br.com.letscode.util.SystemInterfaceUtil;

public class MainScreen implements ScreenInterface {
    private static void draw(ConsolePosition consoleSize, Message message) {
        final String SCREEN_NAME = "Home";
        final String SCREEN_CONTENT = StringUtil.centralize("########## Digite a opção desejada: ##########",
                consoleSize.getColumn())
                + StringUtil.multiply(ConsoleUtil.NEW_LINE, 4)
                + StringUtil.centralizeBlock("1. Iniciar sessão de compras"
                        + ConsoleUtil.NEW_LINE
                        + "2. Listar produtos"
                        + ConsoleUtil.NEW_LINE
                        + "3. Cadastrar produto"
                        + ConsoleUtil.NEW_LINE
                        + "4. Listar tipos de produto"
                        + ConsoleUtil.NEW_LINE,
                        consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE;

        SystemInterfaceUtil.drawInfoScreen(SCREEN_NAME, message, SCREEN_CONTENT, consoleSize);
    }

    public Navigation run(Scanner scanner, String[] args) {
        ConsolePosition consoleSize = new ConsolePosition(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        Message message = new Message("", MessageType.ERROR);
        Navigation navigate = new Navigation();

        while (true) {
            ConsoleUtil.clearScreen();
            draw(consoleSize, message);
            int userInput = 0;
            try {
                userInput = Integer.parseInt(
                        SystemInterfaceUtil.getUserInput(scanner, consoleSize, "Digite o número da opção desejada: "));
            } catch (NumberFormatException e) {
                message.setText("Opção inválida!");
                continue;
            } catch (ExitSignalException e) {
                ConsoleUtil.clearScreen();
                return new Navigation(ScreensList.EXIT, args);
            } catch (GoBackSignalException e) {
                ConsoleUtil.clearScreen();
                return new Navigation(ScreensList.START, args);
            } catch (NoSuchElementException e) {
                // do nothing
            }

            System.out.print(ConsoleUtil.Attribute.RESET.getEscapeCode());

            switch (userInput) {
                case 1:
                    navigate.setScreen(ScreensList.SESSION);
                    break;
                case 2:
                    navigate.setScreen(ScreensList.PRODUCTS);
                    break;
                case 3:
                    navigate.setScreen(ScreensList.CREATE_PRODUCT);
                    break;
                case 4:
                    navigate.setScreen(ScreensList.PRODUCT_TYPES);
                    break;
                default:
                    message.setText("Opção inválida!");
                    continue;
            }
            break;
        }

        ConsoleUtil.clearScreen();
        navigate.setArgs(args);
        return navigate;
    }
}
