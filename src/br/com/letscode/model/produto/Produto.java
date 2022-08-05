package br.com.letscode.model.produto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Produto implements Serializable, Comparable<Produto> {
    private UUID ID;
    private String nome;
    private BigDecimal preco;
    private ProdutoTipo tipo;
    private BigDecimal frete;
    private BigDecimal taxa;

    public Produto() {
        this.preco = BigDecimal.ZERO;
        this.frete = BigDecimal.ZERO;
        this.taxa = BigDecimal.ZERO;
    }

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public ProdutoTipo getTipo() {
        return tipo;
    }

    public void setTipo(ProdutoTipo tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getFrete() {
        return frete;
    }

    public void setFrete(BigDecimal frete) {
        this.frete = frete;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ID == null) ? 0 : ID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Produto other = (Produto) obj;
        if (ID == null) {
            if (other.ID != null)
                return false;
        } else if (!ID.equals(other.ID))
            return false;
        return true;
    }

    @Override
    public int compareTo(Produto obj) {
        if (this == obj) {
            return 0;
        }
        if (obj == null) {
            return 1;
        }
        if (!this.tipo.equals(obj.getTipo())) {
            return this.tipo.compareTo(obj.getTipo());
        }
        return this.nome.compareTo(obj.getNome());
    }

    @Override
    public String toString() {
        return "Produto [ID=" + ID + ", nome=" + nome + ", preco=" + preco + ", tipo=" + tipo + ", frete=" + frete
                + ", taxa=" + taxa + "]";
    }

    public static class Builder {
        private Produto produto;

        public Builder() {
            this.produto = new Produto();
        }

        public Builder withID(UUID ID) {
            this.produto.setID(ID);
            return this;
        }

        public Builder withNome(String nome) {
            this.produto.setNome(nome);
            return this;
        }

        public Builder withPreco(BigDecimal preco) {
            this.produto.setPreco(preco);
            return this;
        }

        public Builder withTipo(ProdutoTipo tipo) {
            this.produto.setTipo(tipo);
            return this;
        }

        public Builder withFrete(BigDecimal frete) {
            this.produto.setFrete(frete);
            return this;
        }

        public Builder withTaxa(BigDecimal taxa) {
            this.produto.setTaxa(taxa);
            return this;
        }

        public Produto build() {
            this.validate();
            return this.produto;
        }

        private void validate() {
            if (this.produto.preco.compareTo(BigDecimal.ZERO) < 0
                    || this.produto.frete.compareTo(BigDecimal.ZERO) < 0
                    || this.produto.taxa.compareTo(BigDecimal.ZERO) < 0) {
                final String EXCEPTION_MESSAGE = "Informações do produto inválidas: ".concat(produto.toString());
                throw new IllegalStateException(EXCEPTION_MESSAGE);
            }
        }
    }

}
