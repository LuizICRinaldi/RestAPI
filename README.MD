# RestAPI

# Inicialização
- Antes de iniciar a aplicação, execute o comando para iniciar o serviço de bando de dados presente no Docker Compose: docker compose up -d
- Execute o comando: docker exec -it postgres bash para entrar na aplicação usando bash
- Acesse a ferramente de linha de comando psql com o user postgres com o comando: psql -U postgres
- Crie o banco de dados caso não exista com o comando: CREATE DATABASE rest_api IF NOT EXITS;

Concluído os passos anteriores, podemos executar a aplicação através da classe RestApiApplication.java

# Observações
- Troque o número da porta do banco de dados no campo "ports" presente no docker-compose.yml caso já esteja sendo utilizado por outra aplicação
- O arquivo request.http contém informações para fazer testes de requisições (necessário adicionar a extensão Rest Client no VS Code)