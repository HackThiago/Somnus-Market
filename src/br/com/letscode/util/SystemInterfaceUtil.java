package br.com.letscode.util;

import java.util.Scanner;

import br.com.letscode.exception.ExitSignalException;
import br.com.letscode.exception.GoBackSignalException;
import br.com.letscode.model.system.ConsolePosition;
import br.com.letscode.model.system.Message;

public class SystemInterfaceUtil {
    public static final int DEFAULT_CONSOLE_WIDTH = 108;

    public static final String SYSTEM_LOGO = "   _____                                              __  ___              __          __ "
            + ConsoleUtil.NEW_LINE
            + "  / ___/ ____   ____ ___   ____ ___   __  __ _____   /  |/  /____ _ _____ / /__ ___   / /_"
            + ConsoleUtil.NEW_LINE
            + "  \\__ \\ / __ \\ / __ `__ \\ / __ `__ \\ / / / // ___/  / /|_/ // __ `// ___// //_// _ \\ / __/"
            + ConsoleUtil.NEW_LINE
            + " ___/ // /_/ // / / / / // / / / / // /_/ /(__  )  / /  / // /_/ // /   / ,<  /  __// /_  "
            + ConsoleUtil.NEW_LINE
            + "/____/ \\____//_/ /_/ /_//_/ /_/ /_/ \\__,_//____/  /_/  /_/ \\__,_//_/   /_/|_| \\___/ \\__/  ";

    public static final String WELCOME_STRING = " ____                            _           _                     "
            + ConsoleUtil.NEW_LINE
            + "| __ )  ___ _ __ ___      __   _(_)_ __   __| | ___     __ _  ___  " + ConsoleUtil.NEW_LINE
            + "|  _ \\ / _ \\ '_ ` _ \\ ____\\ \\ / / | '_ \\ / _` |/ _ \\   / _` |/ _ \\ " + ConsoleUtil.NEW_LINE
            + "| |_) |  __/ | | | | |_____\\ V /| | | | | (_| | (_) | | (_| | (_) |" + ConsoleUtil.NEW_LINE
            + "|____/ \\___|_| |_| |_|      \\_/ |_|_| |_|\\__,_|\\___/   \\__,_|\\___/ ";

    public static final int DEFAULT_LINES_PER_PAGE = 10;

    public static String getHeader(String screenName, ConsolePosition pos) {
        final String HEADER_START_TEXT = "Sommus Market";
        final String time = TimeUtil.nowString();
        final int consoleMiddleRow = pos.getColumn() / 2;
        final int screenNameStartPadding = consoleMiddleRow - HEADER_START_TEXT.length() - (screenName.length() / 2);
        final int dateStartPadding = pos.getColumn() - screenNameStartPadding - time.length() - screenName.length()
                - HEADER_START_TEXT.length();

        return ConsoleUtil.Attribute.FCOL_GREEN.getEscapeCode()
                + ConsoleUtil.Attribute.REVERSE.getEscapeCode()
                + HEADER_START_TEXT
                + StringUtil.blankSpaces(screenNameStartPadding)
                + screenName
                + StringUtil.blankSpaces(dateStartPadding)
                + time
                + ConsoleUtil.Attribute.RESET.getEscapeCode()
                + ConsoleUtil.NEW_LINE;
    }

    public static String getMessage(Message message, int consoleWidth) {
        String format = ConsoleUtil.Attribute.BRIGHT.getEscapeCode();

        String formatBGColor;
        switch (message.getType()) {
            case SUCCESS:
                formatBGColor = ConsoleUtil.Attribute.FCOL_GREEN.getEscapeCode();
                break;
            case ERROR:
                formatBGColor = ConsoleUtil.Attribute.FCOL_RED.getEscapeCode();
                break;
            case WARNING:
                formatBGColor = ConsoleUtil.Attribute.FCOL_YELLOW.getEscapeCode();
                break;
            case INFO:
                formatBGColor = ConsoleUtil.Attribute.FCOL_BLUE.getEscapeCode();
                break;
            default:
                formatBGColor = ConsoleUtil.Attribute.REVERSE.getEscapeCode();
                break;
        }
        format = format.concat(formatBGColor);

        return format
                + ConsoleUtil.Attribute.REVERSE.getEscapeCode()
                + StringUtil.centralize(message.getText(), consoleWidth)
                + StringUtil.blankSpaces((consoleWidth / 2) - (message.getText().length() / 2))
                + ConsoleUtil.Attribute.RESET.getEscapeCode()
                + ConsoleUtil.NEW_LINE;
    }

    public static void drawInfoScreen(String screenName, Message message, String content, ConsolePosition consoleSize) {
        ConsoleUtil.scrollScreen();

        System.out.print(SystemInterfaceUtil.getHeader(screenName, consoleSize));

        ConsoleUtil.skipLines(1);
        if (message.getText().length() > 0) {
            System.out.print(getMessage(message, consoleSize.getColumn()));
        } else {
            ConsoleUtil.skipLines(1);
        }

        ConsoleUtil.skipLines(1);

        System.out.print(content + ConsoleUtil.NEW_LINE);
        ConsoleUtil.skipLines(3);
    }

    public static void drawPaginationScreen(String screenName, Message message, String header, String content,
            ConsolePosition consoleSize, int currentPage, int totalPages) {
        ConsoleUtil.scrollScreen();

        System.out.print(SystemInterfaceUtil.getHeader(screenName, consoleSize));

        ConsoleUtil.skipLines(1);
        if (message.getText().length() > 0) {
            System.out.print(getMessage(message, consoleSize.getColumn()));
        } else {
            ConsoleUtil.skipLines(1);
        }

        ConsoleUtil.skipLines(1);

        System.out.print(header + ConsoleUtil.NEW_LINE);
        ConsoleUtil.skipLines(2);

        int linesPerPage = (int) Math.ceil((double) content.split("\n").length / (double) totalPages);
        int startLine = linesPerPage * (currentPage - 1);
        int endLine = linesPerPage * currentPage;
        if (endLine > content.split("\n").length) {
            endLine = content.split("\n").length;
        }
        String[] lines = content.split("\n");
        for (int i = startLine; i < endLine; i++) {
            System.out.print(lines[i] + ConsoleUtil.NEW_LINE);
        }

        ConsoleUtil.skipLines(1);
        System.out.print(StringUtil.centralize("Página " + currentPage + "/" + totalPages + ConsoleUtil.NEW_LINE,
                consoleSize.getColumn()));
    }

    public static String getUserInput(Scanner scanner, ConsolePosition consoleSize, String message)
            throws ExitSignalException, GoBackSignalException {
        ConsoleUtil.cursorTo(consoleSize.getRow(), 1);
        System.out.print(ConsoleUtil.Attribute.REVERSE.getEscapeCode()
                + message
                + StringUtil.blankSpaces(consoleSize.getColumn() - message.length()));
        ConsoleUtil.cursorTo(consoleSize.getRow(), message.length() + 1);

        String userInput = scanner.nextLine();
        String userInputUpper = userInput.strip().toUpperCase();

        if (userInputUpper.equals("\\EXIT")) {
            throw new ExitSignalException("The user has sent the exit signal");
        }
        if (userInputUpper.equals("\\BACK") || userInputUpper.equals("\\B")) {
            throw new GoBackSignalException("The user has sent the go back signal");
        }

        return userInput;
    }
}
