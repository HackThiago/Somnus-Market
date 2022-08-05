package br.com.letscode.model.cliente;

public class PessoaJuridica extends Cliente {

    @Override
    public boolean validarDocumento() {
        return getDocumento().matches("\\d{2}[.]?\\d{3}[.]?\\d{3}[/]?\\d{4}[-]?\\d{2}");
    }
}
