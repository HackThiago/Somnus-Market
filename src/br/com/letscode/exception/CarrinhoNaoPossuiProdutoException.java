package br.com.letscode.exception;

public class CarrinhoNaoPossuiProdutoException extends Exception {
    public CarrinhoNaoPossuiProdutoException(String errorMessage) {
        super(errorMessage);
    }
}
