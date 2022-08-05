package br.com.letscode.model.cliente;

public class PessoaFisica extends Cliente {

    @Override
    public boolean validarDocumento() {
        return getDocumento().matches("\\d{3}[.]?\\d{3}[.]?\\d{3}[-]?\\d{2}");
    }
}
