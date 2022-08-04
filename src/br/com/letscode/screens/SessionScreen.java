package br.com.letscode.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

import br.com.letscode.dao.CarrinhoDAO;
import br.com.letscode.dao.ProdutoDAO;
import br.com.letscode.exception.CarrinhoNaoPossuiProdutoException;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.exception.ExitSignalException;
import br.com.letscode.exception.GoBackSignalException;
import br.com.letscode.exception.InvalidCommandException;
import br.com.letscode.exception.QuantidadeInvalidaException;
import br.com.letscode.model.produto.Carrinho;
import br.com.letscode.model.produto.Produto;
import br.com.letscode.model.system.ConsolePosition;
import br.com.letscode.model.system.Message;
import br.com.letscode.model.system.MessageType;
import br.com.letscode.model.system.Navigation;
import br.com.letscode.util.ConsoleUtil;
import br.com.letscode.util.StringUtil;
import br.com.letscode.util.SystemInterfaceUtil;

public class SessionScreen implements ScreenInterface {
    private static final int HEADER_LINES = 10;

    private static void draw(
            ConsolePosition consoleSize,
            Message message,
            String screenContent,
            Carrinho session,
            int currentPage,
            int totalPages) {

        String sessionTotal;
        try {
            sessionTotal = StringUtil.formatCurrencyBRL(session.calcularTotal());
        } catch (DatabaseException e) {
            sessionTotal = "Ocorreu um erro ao processar a sessão de compras";
        }
        final String SCREEN_NAME = "Sessão de compras";
        final String SCREEN_HEADER = StringUtil.centralize("########## Carrinho de compras ##########",
                consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE
                + StringUtil.centralize("Total atual: " + sessionTotal, consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE
                + StringUtil.centralize("- Comandos disponiveis -", consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE
                + StringUtil.centralizeBlock("ADICIONAR {CODIGO_PRODUTO} {QUANTIDADE}"
                        + ConsoleUtil.NEW_LINE
                        + "REMOVER {CODIGO_PRODUTO} {QUANTIDADE}"
                        + ConsoleUtil.NEW_LINE
                        + "ALTERAR {CODIGO_PRODUTO} {QUANTIDADE}"
                        + ConsoleUtil.NEW_LINE
                        + "CHECKOUT", consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE
                + StringUtil.centralize("Digite \\n ou \\p para navegar entre as páginas", consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE
                + ConsoleUtil.NEW_LINE
                + StringUtil.centralize("Lista de produtos do mercado", consoleSize.getColumn());

        SystemInterfaceUtil.drawPaginationScreen(SCREEN_NAME, message, SCREEN_HEADER, screenContent, consoleSize,
                currentPage, totalPages);
    }

    public Navigation executeUserCommand(
            List<Produto> productsList,
            String userCommand,
            int totalPages,
            String[] args)
            throws InvalidCommandException {

        UUID sessionID = UUID.fromString(args[2]);
        Carrinho session;
        try {
            session = CarrinhoDAO.get(sessionID);
        } catch (DatabaseException e) {
            throw new InvalidCommandException("Ocorreu um erro com a sessão de compras atual.");
        }
        int page = Integer.parseInt(args[3]);

        if (userCommand.toLowerCase().equals("\\n")) {
            if (page >= totalPages) {
                throw new InvalidCommandException("Não há mais páginas");
            }
            args = StringUtil.removeArgFromList(args, 3);
            return new Navigation(ScreensList.SESSION, StringUtil.addArgToList(args, String.valueOf(page + 1)));
        }
        if (userCommand.toLowerCase().equals("\\p")) {
            if (page <= 1) {
                throw new InvalidCommandException("Não há mais páginas");
            }
            args = StringUtil.removeArgFromList(args, 3);
            return new Navigation(ScreensList.SESSION, StringUtil.addArgToList(args, String.valueOf(page - 1)));
        }

        String[] commandOperands = userCommand.strip().split(" ");

        if (commandOperands.length == 1 && commandOperands[0].toUpperCase().equals("CHECKOUT")) {
            try {
                CarrinhoDAO.save(sessionID, session);
            } catch (DatabaseException e) {
                throw new InvalidCommandException("Erro ao tentar realizar o checkout.");
            }
            args = StringUtil.removeArgFromList(args, 3);
            return new Navigation(ScreensList.CHECKOUT, args);
        }

        if (commandOperands.length != 3) {
            throw new InvalidCommandException("Comando inválido!");
        }

        Produto product;
        try {
            int productCode = Integer.parseInt(commandOperands[1]);
            product = productsList.get(productCode - 1);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidCommandException("Código de produto inválido!");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(commandOperands[2]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Quantidade inválida!");
        }

        try {
            switch (commandOperands[0].toUpperCase()) {
                case "ADICIONAR":
                    session.adicionar(product, quantity);
                    break;
                case "REMOVER":
                    session.remover(product, quantity);
                    break;
                case "ALTERAR":
                    session.alterarQuantidade(product, quantity);
                    break;
                default:
                    throw new InvalidCommandException("Comando inválido!");
            }
        } catch (QuantidadeInvalidaException e) {
            throw new InvalidCommandException("Quantidade inválida!");
        } catch (CarrinhoNaoPossuiProdutoException e) {
            throw new InvalidCommandException("Produto não está incluído no carrinho!");
        }

        return new Navigation(ScreensList.SESSION, args);
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

        UUID sessionID;
        try {
            sessionID = UUID.fromString(args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            sessionID = UUID.randomUUID();
            args = StringUtil.addArgToList(args, sessionID.toString());
        }

        Carrinho session;
        try {
            session = CarrinhoDAO.get(sessionID);
            if (session == null) {
                session = new Carrinho();
                CarrinhoDAO.save(sessionID, session);
            }
        } catch (DatabaseException e1) {
            message.setText("Não foi possível retomar a sessão de compras anterior.");
            message.setType(MessageType.WARNING);
            session = new Carrinho();
        }

        int totalPages;
        int page;
        try {
            page = Integer.parseInt(args[3]);
        } catch (ArrayIndexOutOfBoundsException e) {
            page = 1;
            args = StringUtil.addArgToList(args, String.valueOf(page));
        }

        while (true) {
            String screenContent = StringUtil.centralizeBlock(
                    SystemInterfaceUtil.getSessionProductsList(session, productsList, consoleSize.getColumn()),
                    consoleSize.getColumn());
            totalPages = (int) Math.ceil((double) screenContent.split("\n").length
                    / ((double) consoleSize.getRow() - (double) SystemInterfaceUtil.DEFAULT_LINES_PER_PAGE
                            - (double) HEADER_LINES));

            ConsoleUtil.clearScreen();
            draw(consoleSize, message, screenContent, session, page, totalPages);

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
                Navigation commandReturn = executeUserCommand(productsList, userInput, totalPages, args);
                if (commandReturn.getScreen() != ScreensList.SESSION) {
                    ConsoleUtil.clearScreen();
                    return commandReturn;
                }
                page = Integer.parseInt(commandReturn.getArg(3));
                args = StringUtil.removeArgFromList(args, 3);
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