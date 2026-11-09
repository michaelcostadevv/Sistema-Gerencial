# Sistema Gerencial (ERP Backend)

API REST em Spring Boot para um ERP comercial: cadastro de **clientes**, **produtos** e **pedidos** com cálculo de total e baixa de estoque.

## Tecnologias

- Java 21 (records, `Stream.toList()`)
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- Hibernate / JPA
- PostgreSQL (produção) e H2 em memória (testes)
- Lombok
- JUnit 5, Mockito, AssertJ, MockMvc
- Maven

## Arquitetura

```
Controller  ->  Service  ->  Repository  ->  Banco
   (HTTP)     (regras de     (Spring Data
              negócio,         JPA)
            @Transactional)
```

- **Controller**: só lida com HTTP. Recebe DTOs, valida com `@Valid` e delega ao Service.
- **Service**: regras de negócio e controle de transação (`@Transactional`).
- **Repository**: acesso a dados com Spring Data JPA.
- **DTOs (records)**: a API nunca expõe nem recebe as entidades diretamente.
- **GlobalExceptionHandler** (`@RestControllerAdvice`): todos os erros saem no mesmo formato JSON e com o status HTTP correto.

## Decisões técnicas

| Decisão | Por quê |
|---|---|
| `BigDecimal` para valores monetários | `Double` tem erro de arredondamento binário |
| Preço e total calculados no servidor | O cliente da API não consegue alterar o valor do pedido |
| Preço "congelado" em `OrderItem.unitPrice` | Mudanças futuras de preço não alteram pedidos antigos |
| `@Transactional` na criação do pedido | Se um item falhar (ex.: sem estoque), nada é gravado (rollback) |
| `@Version` em `Product` | Lock otimista: evita que duas vendas simultâneas corrompam o estoque |
| `@EntityGraph` na listagem de pedidos | Evita o problema de N+1 queries |
| `open-in-view=false` | Nenhuma query escondida durante a serialização do JSON |
| Injeção por construtor | Dependências `final`, explícitas e fáceis de mockar em testes |
| `@Getter/@Setter` em vez de `@Data` nas entidades | `equals/hashCode/toString` do `@Data` causam problemas com relações JPA |
| Credenciais via variável de ambiente | Nenhuma senha no código-fonte |

## Como executar

Pré-requisitos: Java 21 e PostgreSQL rodando.

```sql
CREATE DATABASE erp_db;
```

Variáveis de ambiente:

| Variável | Obrigatória | Padrão |
|---|---|---|
| `DB_PASSWORD` | sim | — |
| `DB_USERNAME` | não | `postgres` |
| `DB_URL` | não | `jdbc:postgresql://localhost:5432/erp_db` |

```bash
./mvnw spring-boot:run
```

## Testes

```bash
./mvnw test
```

Não precisa de PostgreSQL: os testes usam H2 em memória.

- `ProductTest`: unitário, regra de estoque.
- `OrderServiceTest`: unitário com Mockito, cálculo de total, baixa de estoque e cenários de erro.
- `SistemagerencialApplicationTests`: integração ponta a ponta via HTTP (MockMvc), incluindo rollback e códigos de erro.

## Endpoints

### Clientes: `/api/clients`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/clients` | Lista clientes |
| GET | `/api/clients/{id}` | Busca por ID |
| POST | `/api/clients` | Cadastra (CPF/CNPJ único, 11 ou 14 dígitos) |

```json
{ "name": "Maria Silva", "cpfCnpj": "11122233344", "email": "maria@email.com", "phone": "11999999999" }
```

### Produtos: `/api/products`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/products` | Lista produtos |
| GET | `/api/products/{id}` | Busca por ID |
| POST | `/api/products` | Cadastra |

```json
{ "nome": "Monitor", "descricao": "24 polegadas", "preco": 899.90, "quantidadeEstoque": 10 }
```

### Pedidos: `/api/orders`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/orders` | Lista pedidos (mais recentes primeiro) |
| GET | `/api/orders/{id}` | Busca por ID |
| POST | `/api/orders` | Cria pedido, calcula total e baixa estoque |

```json
{ "clientId": 1, "items": [ { "productId": 1, "quantity": 2 } ] }
```

### Formato de erro

```json
{
  "timestamp": "2026-01-15T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados inválidos",
  "path": "/api/clients",
  "fieldErrors": { "cpfCnpj": "O CPF/CNPJ deve conter 11 ou 14 dígitos, apenas números" }
}
```

| Status | Quando |
|---|---|
| 400 | Validação falhou ou JSON malformado |
| 404 | Cliente, produto ou pedido não encontrado |
| 409 | CPF/CNPJ duplicado ou conflito de concorrência |
| 422 | Regra de negócio violada (ex.: estoque insuficiente) |
| 500 | Erro inesperado (detalhes só no log do servidor) |
