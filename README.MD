# RestAPI

## Visão Geral
Este projeto consiste no desenvolvimento de uma API RESTful para um sistema de gestão de tarefas colaborativas. A aplicação permite que usuários autenticados criem, editem, atribuam e concluam tarefas de forma eficiente. O objetivo principal é demonstrar a aplicação de boas práticas de Arquitetura de Software, incluindo modularização, padrões arquiteturais, testes automatizados e documentação abrangente.

## Requisitos
* Java 21
* Docker e Docker Compose
* Maven 3.6.3 ou posterior

## Tecnologias
* Spring Boot
* Docker
* PostgreSQL
* JWT 
* Swagger/OpenAPI

## Decisões Arquiteturais
* A escolha de Java com Spring Boot se deu pela robustez, vasta comunidade, maturidade do ecossistema e ferramentas de desenvolvimento que agilizam a construção de aplicações. Spring Boot simplifica a configuração e o deploy, permitindo focar na lógica de negócio.

* A arquitetura MVC foi selecionada por sua familiaridade no ecossistema Spring e sua eficácia em organizar aplicações web.

## Modelagem de Dados
Mapeamento das entidades `Task`, `Token` e `User` para tabelas no banco de dados

## Banco de Dados
O PostgreSQL foi escolhido devido à sua robustez, conformidade com SQL, alta performance e extensibilidade. Para um sistema de gestão de tarefas que envolve relações bem definidas entre usuários e tarefas, um banco de dados relacional é a opção mais adequada, garantindo integridade referencial e transações ACID.

## Testes Automatizados
* Foram realizados testes unitários na camada de `controller` usando **Mocks** para as camadas de `service` e `repository`.
* Os testem fazem cobertura mínima de 60% do código da API. 

## Fluxo de Requisições (Endpoints)
Documentação da API em `localhost:8080/swagger-ui.html`

### Usuários
* **POST /users** → Criar um novo usuário
* **GET /users/{id}** → Obter informações de um usuário específico
* **PUT /users/{id}** → Atualizar informações do usuário
* **DELETE /users/{id}** → Remover um usuário (**soft delete**)

### Tarefas
* **POST /tasks** → Criar uma nova tarefa
* **GET /tasks/{id}** → Obter detalhes de uma tarefa
* **GET /tasks?assignedTo={userId}** → Listar todas as tarefas atribuídas a um usuário
* **PUT /tasks/{id}** → Atualizar informações da tarefa (título, descrição, status)
* **DELETE /tasks/{id}** → Remover uma tarefa

### Autenticação
* **POST /auth/register** → Registro de usuários
* **POST /auth/login** → Login de usuários, retornando um token **JWT** para autenticação nas demais requisições
* **POST /auth/logout** → Logout do usuário

## Mermaid
![Diagrama Mermaid](https://github.com/LuizICRinaldi/RestAPI/blob/3ba9c593fbf7d8dabce3c5e184be0ec30609138c/mermaid-diagram.png?raw=true)

## Dependências
Presentes no arquivo `pom.xml`

## Exceções Customizadas
Presentes na pasta `exception`

## Inicialização
- Clonar repositório `git clone https://github.com/LuizICRinaldi/RestAPI.git`
- Entrar no diretório `cd /RestAPI`
- Iniciar Docker `docker compose up`
- Executar o comando em uma janela de terminal (no diretório completo) `./mvnw spring-boot:run`

## Observações
* Em caso de porta indisponível ao tentar iniciar o Docker, verifique se a respectiva porta está disponível ou altere o campo **`ports`** no arquivo **`docker-compose.yml`**
* Arquivo `request.http` contendo a estrutra das requisições (**extensão REST Client Visual Studio Code**)
