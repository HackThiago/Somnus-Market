# Somnus-Market

Simulação de um carrinho de compras em Java.


## Relatório
___

*TODO: explicar arquitetura das classes do projeto, especificando onde e porque foram utilizados os conceitos de herança, polimorfismo, SOLID e Design Patterns.*

### **Cliente**
___

Um carrinho de compras precisa de um comprador: é a classe **Cliente**. Esta classe não deve ser instanciada, pois um cliente precisa ser uma pessoa física ou jurídica, por isso é uma classe abstrata que é *mãe* das classes **PessoaFísica** e **PessoaJurídica**. Para facilitar a criação das entidades e a validação dos campos, a classe cliente possui o *design pattern* de *builder*, assim podemos instanciar um novo cliente sem precisar criar uma classe específica para cada tipo de cliente:

```java
Cliente cliente = new Cliente
    .Builder(ClienteTipo.PESSOA_JURIDICA)
    .withNome("João")
    .withDocumento("123.456.789-00")
    .build();
```

A validação do campo de documento, CPF para pessoas físicas e CNPJ para pessoas jurídicas, é feita pelo método **validarDocumento**, um método abstrato que deve ser implementado nas classes filhas. No momento que o método para adicionar o documento é executado, a validação implementada nas classes filhas é chamada.

___
## Como executar?

Basta executar o comando abaixo a partir da pasta do projeto:

```cmd
& '[caminho da JDK]/bin/java.exe' '-cp' '[caminho para a pasta desse projeto]\bin' 'br.com.letscode.Aplicacao'
```
