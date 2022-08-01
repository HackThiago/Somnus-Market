package br.com.letscode.model.produto;

import java.io.Serializable;
import java.math.BigDecimal;

public class Promocao implements Serializable {
    private BigDecimal porcentagemDesconto;

    public Promocao() {
    }

    public Promocao(BigDecimal porcentagemDesconto) {
        this.porcentagemDesconto = porcentagemDesconto;
    }

    public BigDecimal getPorcentagemDesconto() {
        return porcentagemDesconto;
    }

    public void setPorcentagemDesconto(BigDecimal porcentagemDesconto) {
        this.porcentagemDesconto = porcentagemDesconto;
    }
}
