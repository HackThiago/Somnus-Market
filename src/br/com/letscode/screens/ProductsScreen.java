package br.com.letscode.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import br.com.letscode.dao.ProdutoDAO;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.exception.ExitSignalException;
import br.com.letscode.exception.GoBackSignalException;
import br.com.letscode.exception.InvalidCommandException;
import br.com.letscode.model.produto.Produto;
import br.com.letscode.model.system.ConsolePosition;
import br.com.letscode.model.system.Message;
import br.com.letscode.model.system.MessageType;
import br.com.letscode.model.system.Navigation;
import br.com.letscode.util.ConsoleUtil;
import br.com.letscode.util.StringUtil;
import br.com.letscode.util.SystemInterfaceUtil;

public class ProductsScreen implements ScreenInterface {
    private static final int HEADER_LINES = 2;

    private static void draw(ConsolePosition consoleSize, Message message, String content, int page,
            int totalPages) {
        final String SCREEN_NAME = "Lista de produtos";
        final String SCREEN_HEADER = StringUtil.centralize(
                "########## Digite \\n ou \\p para navegar entre as páginas ##########",
                consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE;

        SystemInterfaceUtil.drawPaginationScreen(SCREEN_NAME, message, SCREEN_HEADER, content, consoleSize, page,
                totalPages);
    }

    public Navigation executeUserCommand(String userCommand, int totalPages, List<Produto> produtos, String[] args)
            throws InvalidCommandException {
        int page = Integer.parseInt(args[2]);
        args = StringUtil.removeArgFromList(args, 2);

        if (userCommand.toLowerCase().equals("\\n")) {
            if (page >= totalPages) {
                throw new InvalidCommandException("Não há mais páginas");
            }
            return new Navigation(ScreensList.PRODUCTS, StringUtil.addArgToList(args, String.valueOf(page + 1)));
        }
        if (userCommand.toLowerCase().equals("\\p")) {
            if (page <= 1) {
                throw new InvalidCommandException("Não há mais páginas");
            }
            return new Navigation(ScreensList.PRODUCTS, StringUtil.addArgToList(args, String.valueOf(page - 1)));
        }

        throw new InvalidCommandException("Comando inválido!");
    }

    public Navigation run(Scanner scanner, String[] args) {
        ConsolePosition consoleSize = new ConsolePosition(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        Message message = new Message("", null);

        List<Produto> productsList;
        try {
            productsList = new ArrayList<>(ProdutoDAO.listAll().values());
        } catch (DatabaseException e) {
            message.setText("Não foi possível buscar os produtos");
            message.setType(MessageType.ERROR);
            productsList = new ArrayList<>();
        }
        Collections.sort(productsList);

        String content = SystemInterfaceUtil.getProductsList(productsList, consoleSize.getColumn());
        int page = 1;
        int totalPages = (int) Math.ceil((double) content.split("\n").length
                / ((double) consoleSize.getRow() - (double) SystemInterfaceUtil.DEFAULT_LINES_PER_PAGE
                        - (double) HEADER_LINES));
        args = StringUtil.addArgToList(args, String.valueOf(page));

        while (true) {
            ConsoleUtil.clearScreen();
            draw(consoleSize, message, content, page, totalPages);

            String promptMessage = "Digite o comando que deseja executar: ";
            String userInput = "";
            try {
                userInput = SystemInterfaceUtil.getUserInput(scanner, consoleSize, promptMessage).strip();
            } catch (ExitSignalException e) {
                ConsoleUtil.clearScreen();
                return new Navigation(ScreensList.EXIT, args);
            } catch (GoBackSignalException e) {
                ConsoleUtil.clearScreen();
                String[] returnArgs = { args[0], args[1] };
                return new Navigation(ScreensList.MAIN, returnArgs);
            } catch (NoSuchElementException e) {
                // do nothing
            }
            System.out.print(ConsoleUtil.Attribute.RESET.getEscapeCode());

            try {
                Navigation commandReturn = executeUserCommand(userInput, totalPages, productsList, args);
                if (commandReturn.getScreen() != ScreensList.PRODUCTS) {
                    ConsoleUtil.clearScreen();
                    return commandReturn;
                }
                page = Integer.parseInt(commandReturn.getArg(2));
                args = StringUtil.removeArgFromList(args, 2);
                args = StringUtil.addArgToList(args, String.valueOf(page));

                message.setText("Comando executado com sucesso!");
                message.setType(MessageType.SUCCESS);
                continue;
            } catch (InvalidCommandException e) {
                message.setText(e.getMessage());
                message.setType(MessageType.ERROR);
                continue;
            }
        }
    }

}
