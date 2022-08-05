package br.com.letscode.model.produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.letscode.dao.PromocaoDAO;
import br.com.letscode.exception.CarrinhoNaoPossuiProdutoException;
import br.com.letscode.exception.DatabaseException;
import br.com.letscode.exception.QuantidadeInvalidaException;
import br.com.letscode.model.cliente.Cliente;

public class Carrinho {
    private Map<Produto, Integer> produtos;
    private Cliente cliente;

    private static final int QUANTIDADE_CASAS_DECIMAIS = 2;
    private static final RoundingMode MODO_ARREDONDAMENTO = RoundingMode.HALF_UP;

    public Carrinho() {
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

    public void remover(Produto produto) throws QuantidadeInvalidaException, CarrinhoNaoPossuiProdutoException {
        remover(produto, 1);
    }

    public void remover(Produto produto, int quantidade)
            throws QuantidadeInvalidaException, CarrinhoNaoPossuiProdutoException {
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

    public void alterarQuantidade(Produto produto, int quantidade)
            throws QuantidadeInvalidaException, CarrinhoNaoPossuiProdutoException {
        if (quantidade <= 0) {
            throw new QuantidadeInvalidaException("Quantidade não pode ser negativa!");
        }

        if (!produtos.containsKey(produto)) {
            throw new CarrinhoNaoPossuiProdutoException("Produto não encontrado no carrinho.");
        }

        produtos.put(produto, quantidade);
    }

    public Map<Produto, Integer> listar() {
        return produtos;
    }

    public BigDecimal calcularTotal() throws DatabaseException {
        Map<ProdutoTipo, Promocao> promocoes;
        try {
            promocoes = PromocaoDAO.listAll();
        } catch (DatabaseException e) {
            throw new DatabaseException("Erro ao tentar acessar o banco de dados.", e);
        }

        BigDecimal totalCarrinho = BigDecimal.ZERO;
        for (Entry<Produto, Integer> produtoComQuantidade : produtos.entrySet()) {
            BigDecimal precoProduto = produtoComQuantidade.getKey().getPreco();
            BigDecimal freteProduto = produtoComQuantidade.getKey().getFrete();
            BigDecimal taxaProduto = produtoComQuantidade.getKey().getTaxa();

            ProdutoTipo tipoProduto = produtoComQuantidade.getKey().getTipo();
            BigDecimal promocaoTipoProduto = promocoes.containsKey(tipoProduto)
                    ? promocoes.get(tipoProduto).getPorcentagemDesconto()
                    : BigDecimal.ZERO;

            BigDecimal totalProduto = precoProduto.multiply(BigDecimal.ONE.subtract(promocaoTipoProduto))
                    .add(freteProduto)
                    .add(taxaProduto);

            totalCarrinho = totalCarrinho
                    .add(totalProduto.multiply(BigDecimal.valueOf(produtoComQuantidade.getValue())));
        }

        return totalCarrinho.setScale(QUANTIDADE_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
