package br.com.letscode.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

import br.com.letscode.dao.CarrinhoDAO;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.exception.ExitSignalException;
import br.com.letscode.exception.GoBackSignalException;
import br.com.letscode.model.cliente.Cliente;
import br.com.letscode.model.cliente.ClienteTipo;
import br.com.letscode.model.produto.Carrinho;
import br.com.letscode.model.system.ConsolePosition;
import br.com.letscode.model.system.Message;
import br.com.letscode.model.system.MessageType;
import br.com.letscode.model.system.Navigation;
import br.com.letscode.util.ConsoleUtil;
import br.com.letscode.util.StringUtil;
import br.com.letscode.util.SystemInterfaceUtil;

public class CheckoutScreen implements ScreenInterface {
    private static void draw(
            Carrinho session,
            ConsolePosition consoleSize,
            Message message,
            List<Integer> highlitedLines) {

        String sessionTotal;
        try {
            sessionTotal = StringUtil.formatCurrencyBRL(session.calcularTotal());
        } catch (DatabaseException e) {
            sessionTotal = "?";
        }
        final String SCREEN_NAME = "Checkout";
        final String SCREEN_CONTENT = StringUtil.centralize(
                "Total da compra: " + sessionTotal,
                consoleSize.getColumn())
                + StringUtil.multiply(ConsoleUtil.NEW_LINE, 2)
                + StringUtil.centralize(
                        "########## Vamos precisar das seguintes informações do cliente: ##########",
                        consoleSize.getColumn())
                + StringUtil.multiply(ConsoleUtil.NEW_LINE, 4)
                + StringUtil.centralizeBlock("1. Documento do cliente (CPF ou CNPJ)"
                        + ConsoleUtil.NEW_LINE
                        + "2. Nome do cliente"
                        + ConsoleUtil.NEW_LINE,
                        consoleSize.getColumn())
                + ConsoleUtil.NEW_LINE;

        String screenContent = "";
        String[] screenContentLines = SCREEN_CONTENT.split("\n");
        for (int i = 0; i < screenContentLines.length; i++) {
            String line = screenContentLines[i];
            if (highlitedLines.contains(i)) {
                line = ConsoleUtil.Attribute.REVERSE.getEscapeCode()
                        + line
                        + ConsoleUtil.Attribute.RESET.getEscapeCode();
            }
            screenContent += line + ConsoleUtil.NEW_LINE;
        }

        SystemInterfaceUtil.drawInfoScreen(SCREEN_NAME, message, screenContent, consoleSize);
    }

    private static boolean validateAnswer(int formStep, String answer) {
        switch (formStep) {
            case 1:
                if (answer.matches("\\d{3}[.]?\\d{3}[.]?\\d{3}[-]?\\d{2}")
                        || answer.matches("\\d{2}[.]?\\d{3}[.]?\\d{3}[/]?\\d{4}[-]?\\d{2}")) {
                    return true;
                }
                break;
            case 2:
            case 3:
                return true;
            default:
                break;
        }
        return false;
    }

    public Navigation run(Scanner scanner, String[] args) {
        ConsolePosition consoleSize = new ConsolePosition(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        Message message = new Message("", MessageType.ERROR);

        UUID sessionID;
        Carrinho session;
        try {
            sessionID = UUID.fromString(args[2]);
            session = CarrinhoDAO.get(sessionID);
        } catch (ArrayIndexOutOfBoundsException | DatabaseException e) {
            sessionID = UUID.randomUUID();
            session = new Carrinho();
            message.setText("Não foi possível recuperar os dados sessão de compras atual.");
        }

        Navigation navigate = new Navigation();
        int formStep = 1;
        List<Integer> highlitedLines = new ArrayList<Integer>();
        highlitedLines.add(0);

        String clientDocument = null;
        String clientName = null;
        ClienteTipo clientType = null;

        while (true) {
            String promptMessage = "";
            switch (formStep) {
                case 1:
                    promptMessage = "Digite o ocumento do cliente (CPF ou CNPJ): ";
                    highlitedLines.set(0, 6);
                    break;
                case 2:
                    promptMessage = "Digite o nome completo do cliente: ";
                    highlitedLines.set(0, 7);
                    break;
                default:
                    promptMessage = "Tecle enter para continuar.";
                    highlitedLines.set(0, 1);
                    break;
            }

            ConsoleUtil.clearScreen();
            draw(session, consoleSize, message, highlitedLines);

            String userInput = "";
            try {
                userInput = SystemInterfaceUtil.getUserInput(scanner, consoleSize, promptMessage).strip();
            } catch (ExitSignalException e) {
                ConsoleUtil.clearScreen();
                return new Navigation(ScreensList.EXIT, args);
            } catch (GoBackSignalException e) {
                ConsoleUtil.clearScreen();
                return new Navigation(ScreensList.MAIN, args);
            } catch (NoSuchElementException e) {
                // do nothing
            }
            System.out.print(ConsoleUtil.Attribute.RESET.getEscapeCode());

            if (!validateAnswer(formStep, userInput)) {
                message.setText("Resposta inválida!");
                System.out.println(userInput);
                continue;
            }

            message.setText("");

            switch (formStep) {
                case 1:
                    if (userInput.toUpperCase().matches("\\d{3}[.]?\\d{3}[.]?\\d{3}[-]?\\d{2}")) {
                        clientType = ClienteTipo.PESSOA_FISICA;
                    } else {
                        clientType = ClienteTipo.PESSOA_JURIDICA;
                    }
                    clientDocument = userInput;
                    formStep++;
                    continue;
                case 2:
                    clientName = userInput.strip();

                    Cliente client = new Cliente.Builder(clientType)
                            .withDocumento(clientDocument)
                            .withNome(clientName)
                            .build();

                    session.setCliente(client);

                    // finaliza a sessão
                    try {
                        CarrinhoDAO.delete(sessionID);
                        message.setText("Compra efetuada com sucesso!");
                        message.setType(MessageType.SUCCESS);
                    } catch (DatabaseException e) {
                        message.setText("Não foi possível finalizar a sessão de compras atual.");
                        message.setType(MessageType.ERROR);
                    }
                    message.setText(message.getText().concat(" Pressione enter para retornar à página inicial."));

                    formStep++;
                    continue;
                case 3:
                    formStep++;
                default:
                    break;
            }
            break;
        }

        ConsoleUtil.clearScreen();
        navigate.setScreen(ScreensList.MAIN);
        navigate.setArgs(StringUtil.removeArgFromList(args, 2));
        return navigate;
    }

}