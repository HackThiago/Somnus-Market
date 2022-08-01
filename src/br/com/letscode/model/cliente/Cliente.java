package br.com.letscode.model.cliente;

public abstract class Cliente {
    private String documento;
    private String nome;

    public abstract boolean validarDocumento();

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
