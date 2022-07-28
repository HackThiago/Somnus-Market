package br.com.letscode.error;

public class ExitSignalException extends Exception {
    public ExitSignalException(String errorMessage) {
        super(errorMessage);
    }
}
