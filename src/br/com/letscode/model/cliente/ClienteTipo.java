package br.com.letscode.model.cliente;

public enum ClienteTipo {
    PESSOA_FISICA, PESSOA_JURIDICA;

    public Cliente getInstance() {
        switch (this) {
            case PESSOA_FISICA:
                return new PessoaFisica();
            case PESSOA_JURIDICA:
                return new PessoaJuridica();
            default:
                return null;
        }
    }
}
