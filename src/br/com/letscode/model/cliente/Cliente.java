package br.com.letscode.model.cliente;

import br.com.letscode.exception.QuantidadeInvalidaException;
import br.com.letscode.model.Carrinho;
import br.com.letscode.model.produto.Produto;

public abstract class Cliente {
    private String documento;
    private String nome;
    private Carrinho carrinho;

    public abstract boolean validarDocumento();

    public void adicionarNoCarrinho(Produto produto) throws QuantidadeInvalidaException{
        carrinho.adicionar(produto);
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
}
