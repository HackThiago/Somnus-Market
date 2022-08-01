package br.com.letscode.model.cliente;

public class ClienteBuilder {
    private Cliente cliente;

    public ClienteBuilder(ClienteTipo tipoCliente) {
        this.cliente = tipoCliente.getInstance();
    }

    public ClienteBuilder setDocumento(String documento) {
        this.cliente.setDocumento(documento);
        return this;
    }

    public ClienteBuilder setNome(String nome) {
        this.cliente.setNome(nome);
        return this;
    }

    public Cliente build() {
        return this.cliente;
    }
}
