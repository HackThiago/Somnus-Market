package br.com.letscode.screens;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import br.com.letscode.dao.PromocaoDAO;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.exception.ExitSignalException;
import br.com.letscode.exception.GoBackSignalException;
import br.com.letscode.exception.InvalidCommandException;
import br.com.letscode.model.produto.ProdutoTipo;
import br.com.letscode.model.produto.Promocao;
import br.com.letscode.model.system.ConsolePosition;
import br.com.letscode.model.system.Message;
import br.com.letscode.model.system.MessageType;
import br.com.letscode.model.system.Navigation;
import br.com.letscode.util.ConsoleUtil;
import br.com.letscode.util.StringUtil;
import br.com.letscode.util.SystemInterfaceUtil;

public class ProductTypesScreen implements ScreenInterface {
    private static final int HEADER_LINES = 2;

    private static void draw(ConsolePosition consoleSize, Message message, String content, int page,
            int totalPages) {
        final String SCREEN_NAME = "Lista de tipos de produto";
        final String SCREEN_HEADER = StringUtil.centralize(
                "########## Digite \\n ou \\p para navegar entre as páginas ##########", consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE
                + StringUtil.centralize("- Comandos disponiveis -", consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE
                + StringUtil.centralizeBlock("PROMOCAO {TIPO_PRODUTO} {PORCENTAGEM_DESCONTO}"
                        + ConsoleUtil.NEW_LINE
                        + "Exemplo: PROMOCAO COMIDA 20"
                        + ConsoleUtil.NEW_LINE,
                        consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE;

        SystemInterfaceUtil.drawPaginationScreen(SCREEN_NAME, message, SCREEN_HEADER, content, consoleSize, page,
                totalPages);
    }

    public Navigation executeUserCommand(String userCommand, int totalPages, List<ProdutoTipo> productTypesList,
            String[] args)
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

        String[] commandOperands = userCommand.strip().split(" ");

        switch (commandOperands[0].toUpperCase()) {
            case "PROMOCAO":
                if (commandOperands.length != 3) {
                    throw new InvalidCommandException("Comando inválido!");
                }

                boolean found = false;
                for (ProdutoTipo type : ProdutoTipo.values()) {
                    if (type.name().toUpperCase().equals(commandOperands[1].toUpperCase())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new InvalidCommandException("Tipo de produto inválido!");
                }

                ProdutoTipo productType = ProdutoTipo.valueOf(commandOperands[1].toUpperCase());
                int discount;
                try {
                    discount = Integer.parseInt(commandOperands[2]);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Porcentagem inválida!");
                }
                if (discount < 0 || discount > 100) {
                    throw new InvalidCommandException("Porcentagem inválida! Digite um valor entre 0 e 100");
                }

                Promocao promotion = new Promocao(
                        BigDecimal.valueOf(discount).setScale(2).divide(BigDecimal.valueOf(100), RoundingMode.CEILING));
                try {
                    PromocaoDAO.save(productType, promotion);
                } catch (DatabaseException e) {
                    throw new InvalidCommandException("Erro ao tentar acessar o banco de dados.");
                }

                return new Navigation(ScreensList.PRODUCT_TYPES, args);
            default:
                throw new InvalidCommandException("Comando inválido!");
        }
    }

    public Navigation run(Scanner scanner, String[] args) {
        ConsolePosition consoleSize = new ConsolePosition(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        Message message = new Message("", null);

        List<ProdutoTipo> productTypesList = new ArrayList<>();
        for (ProdutoTipo productType : ProdutoTipo.values()) {
            productTypesList.add(productType);
        }

        String content = null;
        try {
            content = SystemInterfaceUtil.getProductTypesList(productTypesList, consoleSize.getColumn());
        } catch (DatabaseException e1) {
            message.setText("Não foi possível buscar os tipos de produtos no banco de dados.");
            message.setType(MessageType.ERROR);
        }
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
                return new Navigation(ScreensList.MAIN, StringUtil.removeArgFromList(args, 2));
            } catch (NoSuchElementException e) {
                // do nothing
            }
            System.out.print(ConsoleUtil.Attribute.RESET.getEscapeCode());

            try {
                Navigation commandReturn = executeUserCommand(userInput, totalPages, productTypesList, args);
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
