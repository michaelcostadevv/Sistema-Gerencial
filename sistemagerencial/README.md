🚀 Sistema Gerencial (ERP Backend)
API REST desenvolvida em Spring Boot para a gestão de um ERP comercial, focando em boas práticas de arquitetura, validações robustas, separação de responsabilidades com DTOs e segurança de dados.

🛠️ Tecnologias e Ferramentas Utilizadas
Java 21

Spring Boot 4.x (Web, Data JPA, Validation)

Hibernate ORM

PostgreSQL (Base de dados relacional)

Lombok (Redução de código boilerplate)

Maven (Gestão de dependências)

🏛️ Arquitetura e Boas Práticas
DTOs (Data Transfer Objects): Separação clara entre os modelos de persistência da base de dados e os dados expostos nas rotas da API.

Validações Ativas (@Valid): Validação de dados de entrada na borda (como obrigatoriedade de campos, limites de caracteres e formato de e-mail).

Tratamento Global de Erros (@RestControllerAdvice): Captura centralizada de exceções, devolvendo respostas HTTP padronizadas e limpas para o cliente.

Segurança de Credenciais: Configuração de base de dados isolada através de Variáveis de Ambiente (DB_PASSWORD), evitando a exposição de senhas no código-fonte.

⚙️ Como Executar o Projeto Localmente
Pré-requisitos
Ter o Java 21 instalado.

Ter o Maven configurado.

Ter uma instância do PostgreSQL a correr localmente.

1. Configurar a Base de Dados
Cria uma base de dados chamada erp_db no teu PostgreSQL:

SQL
CREATE DATABASE erp_db;
2. Configurar a Variável de Ambiente
Define a variável de ambiente no teu sistema ou diretamente nas configurações de execução do teu IDE (como o IntelliJ IDEA):

Nome: DB_PASSWORD

Valor: A senha do teu utilizador do PostgreSQL.

3. Clonar e Executar
Clona o repositório, abre-o no teu IDE preferido, certifica-te de que as dependências do Maven foram carregadas e executa a classe principal:SistemagerencialApplication.java

📌 Endpoints Principais
Clientes (/api/clients)
GET /api/clients - Lista todos os clientes registados.

POST /api/clients - Regista um novo cliente (com validação de dados).