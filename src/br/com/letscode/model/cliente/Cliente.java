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

    @Override
    public String toString() {
        return "Cliente [documento=" + documento + ", nome=" + nome + "]";
    }

    public static class Builder {
        private Cliente cliente;

        public Builder(ClienteTipo tipoCliente) {
            this.cliente = tipoCliente.getInstance();
        }

        public Builder withDocumento(String documento) {
            this.cliente.setDocumento(documento.replaceAll("[.-]", ""));
            return this;
        }

        public Builder withNome(String nome) {
            this.cliente.setNome(nome);
            return this;
        }

        public Cliente build() {
            this.validate();
            return this.cliente;
        }

        private void validate() {
            if (this.cliente.documento == null
                    || !this.cliente.validarDocumento()) {
                final String EXCEPTION_MESSAGE = "Informações do cliente inválidas: ".concat(cliente.toString());
                throw new IllegalStateException(EXCEPTION_MESSAGE);
            }
        }
    }
}
