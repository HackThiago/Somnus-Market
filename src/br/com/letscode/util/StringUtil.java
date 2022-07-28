package br.com.letscode.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class StringUtil {
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public static String multiply(String str, int times) {
        String newStr = "";
        for (int i = 0; i < times; i++) {
            newStr = newStr.concat(str);
        }
        return newStr;
    }

    public static String blankSpaces(int quantity) {
        return multiply(" ", quantity);
    }

    public static String addBlankSpacesToAllLines(String s, int quantity) {
        String spaces = blankSpaces(quantity);
        s = spaces.concat(s);
        s = s.replaceAll("\n", ConsoleUtil.NEW_LINE + spaces);
        return s;
    }

    public static String centralize(String s, int lineWidth) {
        return blankSpaces((lineWidth - s.length()) / 2).concat(s);
    }

    public static String centralizeBlock(String s, int lineWidth) {
        String[] lines = s.split("\n");
        int maxLineLength = 0;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > maxLineLength) {
                maxLineLength = lines[i].length();
            }
        }
        int spacesQuantity = (lineWidth - maxLineLength) / 2;
        s = addBlankSpacesToAllLines(s, spacesQuantity);

        return s;
    }

    public static String[] addArgToList(String[] args, String newArg) {
        String[] newArgs = new String[args.length + 1];

        for (int i = 0; i < args.length; i++) {
            newArgs[i] = args[i];
        }

        newArgs[args.length] = newArg;

        return newArgs;
    }

    public static String[] removeArgFromList(String[] args, int index) {
        String[] newArgs = new String[args.length - 1];

        int pos = 0;
        for (int i = 0; i < args.length; i++) {
            if (i != index) {
                newArgs[pos++] = args[i];
            }
        }

        return newArgs;
    }

    public static String formatCurrencyBRL(BigDecimal amount) {
        return CURRENCY_FORMATTER.format(amount);
    }

    public static String formatCPF(String cpf) {
        return cpf.substring(0, 3)
                + "."
                + cpf.substring(3, 6)
                + "."
                + cpf.substring(6, 9)
                + "-"
                + cpf.substring(9);
    }

    public static String formatCNPJ(String cnpj) {
        return cnpj.substring(0, 2)
                + "."
                + cnpj.substring(2, 5)
                + "."
                + cnpj.substring(5, 8)
                + "/"
                + cnpj.substring(8, 12)
                + "-"
                + cnpj.substring(12);
    }

    public static boolean isParseableToDouble(String number){
        try{
            Double.parseDouble(number);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
}
