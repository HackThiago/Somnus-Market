package br.com.letscode.screens;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

import br.com.letscode.dao.ProdutoDAO;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.exception.ExitSignalException;
import br.com.letscode.exception.GoBackSignalException;
import br.com.letscode.model.produto.Produto;
import br.com.letscode.model.produto.ProdutoTipo;
import br.com.letscode.model.system.ConsolePosition;
import br.com.letscode.model.system.Message;
import br.com.letscode.model.system.MessageType;
import br.com.letscode.model.system.Navigation;
import br.com.letscode.util.ConsoleUtil;
import br.com.letscode.util.StringUtil;
import br.com.letscode.util.SystemInterfaceUtil;

public class CreateProductScreen implements ScreenInterface {
    private static void draw(ConsolePosition consoleSize, Message message, List<Integer> highlitedLines) {
        final String SCREEN_NAME = "Cadastrar produto";
        final String SCREEN_CONTENT = StringUtil.centralize(
                "########## Vamos precisar das seguintes informações do produto: ##########",
                consoleSize.getColumn())
                + StringUtil.multiply(ConsoleUtil.NEW_LINE, 4)
                + StringUtil.centralizeBlock("1. Tipo do produto"
                        + ConsoleUtil.NEW_LINE
                        + "2. Nome do produto"
                        + ConsoleUtil.NEW_LINE
                        + "3. Preço do produto"
                        + ConsoleUtil.NEW_LINE
                        + "4. Taxa do produto"
                        + ConsoleUtil.NEW_LINE
                        + "5. Frete do produto"
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
                for (ProdutoTipo tipoProduto : ProdutoTipo.values()) {
                    if (tipoProduto.name().toUpperCase().equals(answer.toUpperCase())) {
                        return true;
                    }
                }
                break;
            case 2:
                if (answer.length() > 0) {
                    return true;
                }
                break;
            case 3, 4, 5:
                try {
                    Double price = Double.parseDouble(answer);
                    if (price >= 0) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // do nothing
                }
                break;
            default:
                break;
        }
        return false;
    }

    public Navigation run(Scanner scanner, String[] args) {
        ConsolePosition consoleSize = new ConsolePosition(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        Message message = new Message("", MessageType.ERROR);
        Navigation navigate = new Navigation();
        int formStep = 1;
        List<Integer> highlitedLines = new ArrayList<Integer>();
        highlitedLines.add(0);

        ProdutoTipo tipoProduto = null;
        String nomeProduto = null;
        BigDecimal precoProduto = null;
        BigDecimal taxaProduto = null;
        BigDecimal freteProduto = null;

        while (true) {
            String promptMessage = "";
            switch (formStep) {
                case 1:
                    promptMessage = "Digite o tipo do produto: ";
                    highlitedLines.set(0, 4);
                    break;
                case 2:
                    promptMessage = "Digite o nome do produto: ";
                    highlitedLines.set(0, 5);
                    break;
                case 3:
                    promptMessage = "Digite o preço do produto: ";
                    highlitedLines.set(0, 6);
                    break;
                case 4:
                    promptMessage = "Digite o total das taxas do produto: ";
                    highlitedLines.set(0, 7);
                    break;
                case 5:
                    promptMessage = "Digite o total do frete do produto: ";
                    highlitedLines.set(0, 8);
                    break;
                default:
                    promptMessage = "";
                    break;
            }

            ConsoleUtil.clearScreen();
            draw(consoleSize, message, highlitedLines);

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
                    tipoProduto = ProdutoTipo.valueOf(userInput.toUpperCase());
                    formStep++;
                    continue;
                case 2:
                    nomeProduto = userInput;
                    formStep++;
                    continue;
                case 3:
                    precoProduto = new BigDecimal(Double.parseDouble(userInput));
                    formStep++;
                    continue;
                case 4:
                    taxaProduto = new BigDecimal(Double.parseDouble(userInput));
                    formStep++;
                    continue;
                case 5:
                    freteProduto = new BigDecimal(Double.parseDouble(userInput));
                    formStep++;
                default:
                    break;
            }
            break;
        }

        Produto produto = new Produto.Builder()
                .withID(UUID.randomUUID())
                .withTipo(tipoProduto)
                .withNome(nomeProduto)
                .withPreco(precoProduto)
                .withTaxa(taxaProduto)
                .withFrete(freteProduto)
                .build();

        try {
            ProdutoDAO.save(produto.getID(), produto);
        } catch (DatabaseException e) {
            message.setText("Não foi possível criar o produto");
            message.setType(MessageType.ERROR);
        }

        ConsoleUtil.clearScreen();
        navigate.setScreen(ScreensList.PRODUCTS);
        navigate.setArgs(args);
        return navigate;
    }

}
