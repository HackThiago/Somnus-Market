# **Somnus-Market**

*Simulação de um carrinho de compras em Java.*

___

## **Relatório**

*Explicação da arquitetura das classes do projeto, especificando onde e porque foram utilizados os conceitos de herança, polimorfismo, SOLID e Design Patterns.*

___

### [**Cliente**](/src/br/com/letscode/model/cliente/Cliente.java)

Um carrinho de compras pode ter um comprador: é a classe [**Cliente**](/src/br/com/letscode/model/cliente/Cliente.java). Esta classe não deve ser instanciada, pois um cliente precisa ser uma pessoa física ou jurídica, por isso é uma classe abstrata que é *mãe* das classes [**PessoaFísica**](/src/br/com/letscode/model/cliente/PessoaFisica.java) e [**PessoaJurídica**](/src/br/com/letscode/model/cliente/PessoaJuridica.java). Para facilitar a criação das entidades e a validação dos campos, a classe cliente possui o *design pattern* de *builder*, assim podemos instanciar um novo cliente sem precisar criar uma classe específica para cada tipo de cliente:

```java
Cliente cliente = new Cliente
    .Builder(ClienteTipo.PESSOA_JURIDICA)
    .withNome("João")
    .withDocumento("123.456.789-00")
    .build();
```

A validação do campo de documento, CPF para pessoas físicas e CNPJ para pessoas jurídicas, é feita pelo método **validarDocumento**, um método abstrato que deve ser implementado nas classes filhas. No momento que o método para adicionar o documento é executado, a validação implementada nas classes filhas é chamada.
___

### [**Produto**](/src/br/com/letscode/model/produto/Produto.java)

A mesma lógica do builder usada no cliente é aplicada no produto:

```java
Produto produto = new Produto
    .Builder()
    .withId(UUID().randomUUID())
    .withNome("Bombom")
    .withTipo(ProdutoTipo.COMIDA)
    .withPreco(new BigDecimal(1.00))
    .build();
```

Os produtos com taxa ou frete têm os valores *taxa* e *frete* diferente de zero, ou seja, são setados no *builder*.
___

### [**Carrinho**](/src/br/com/letscode/model/produto/Carrinho.java)

___

Um carrinho possui uma lista de produtos, que é, na verdade, a associação de um produto com uma quantidade (um *Map*), e também pode ter um *Cliente*; são exemplos de *composição*. As responsabilidades do carrinho são as seguintes: *adicionar*, *remover*, ou *alterar* a quantidade de um item, *listar* o total de itens do carrinho, *calcular* o total do carrinho, e *adicionar* um cliente ao carrinho.

___

### [**Promoção**](/src/br/com/letscode/model/produto/Promocao.java)

Pode haver uma ou mais promoções associadas a algum dos tipos dos produtos do carrinho. Para que essas promoções sejam consideradas no preço total, a função de calcular o total do Carrinho busca todas as promoções existente no banco de dados, utilizando a classe intermediária para acessar a base de dados ([**PromocaoDAO**](/src/br/com/letscode/dao/PromocaoDAO.java)); depois que esses dados são recuperados o total é calculado.

___

### [**Base de dados**](/src/br/com/letscode/database/Database.java)

Para a persistência dos objetos, foram implementados duas formas: uma usando a memória ([MemoryDatabase](/src/br/com/letscode/database/MemoryDatabase.java)), outra usando o sistema de arquivos ([FileDatabase](/src/br/com/letscode/database/FileDatabase.java)), ambas implementam a interface [Database](/src/br/com/letscode/database/Database.java). Assim a forma de acessar o banco de dados fica mais *genérica*. O usuário pode definir qual o tipo da base de dados vai usar modificando um parâmetro no início do programa (por enquanto está *hard coded*, mas pode ser implementado futuramente).

As classes DAO instanciam a classe de base de dados e usam as caracterísicas do *generics* do Java para, não importa qual o tipo do atributo, gravá-lo no banco de dados.

___

### [**SOLID**](https://www.baeldung.com/solid-principles)

O projeto buscou utilizar os princípios do SOLID: *Single Responsibility Principle*, *Open-Closed Principle*, *Liskov Substitution Principle*, *Interface Segregation Principle* e *Dependency Inversion Principle*.

* *Single Responsibility Principle*: cada classe deve ter uma única responsabilidade.

* *Open-Closed Principle*: classes devem ser abertas para extensão e fechadas para modificação.

* *Liskov Substitution Principle*: classes devem ser substituíveis.

* *Interface Segregation Principle*: classes devem ser separadas em interfaces.

* *Dependency Inversion Principle*: classes devem ser independentes.

___

## **Como executar?**

Basta executar o comando abaixo a partir da pasta do projeto:

```cmd
& '[caminho da JDK]/bin/java.exe' '-cp' '[caminho para a pasta desse projeto]\bin' 'br.com.letscode.Aplicacao'
```
