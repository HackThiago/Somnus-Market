package br.com.letscode.model;

import java.util.HashMap;
import java.util.Map;

import br.com.letscode.exception.CarrinhoNaoPossuiProdutoException;
import br.com.letscode.exception.QuantidadeInvalidaException;
import br.com.letscode.model.produto.Produto;

public class Carrinho {

    private Map<Produto, Integer> produtos;

    Carrinho() {
        produtos = new HashMap<>();
    }

    public void adicionar(Produto produto) throws QuantidadeInvalidaException {
        adicionar(produto, 1);
    }

    public void adicionar(Produto produto, int quantidade) throws QuantidadeInvalidaException {
        if (quantidade <= 0) {
            throw new QuantidadeInvalidaException("Quantidade não pode ser negativa!");
        }

        if (produtos.containsKey(produto)) {
            quantidade += produtos.get(produto);
        }

        produtos.put(produto, quantidade);
    }

    public void remover(Produto produto) throws QuantidadeInvalidaException, CarrinhoNaoPossuiProdutoException{
        remover(produto, 1);
    }

    public void remover(Produto produto, int quantidade) throws QuantidadeInvalidaException, CarrinhoNaoPossuiProdutoException {
        if (quantidade <= 0) {
            throw new QuantidadeInvalidaException("Quantidade não pode ser negativa!");
        }

        if (!produtos.containsKey(produto)) {
            throw new CarrinhoNaoPossuiProdutoException("Produto não encontrado no carrinho.");
        }

        if (produtos.get(produto) <= quantidade) {
            produtos.remove(produto);
            return;
        }

        int novaQuantidade = produtos.get(produto) - quantidade;
        produtos.put(produto, novaQuantidade);
    }
}
