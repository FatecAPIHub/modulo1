# Módulo 1 — API de Pessoas (Spring Boot 3) 🚀

Aplicação REST para gerenciamento de pessoas com paginação, filtro de ativos, logs centralizados no Graylog e execução via Docker Compose.

## Módulo 1 — Escopo do Projeto 🎯
- API REST utilizando Spring Boot (versão 3+) ☕
- CRUD de Pessoa (Criar, Ler, Atualizar e Deletar) com os seguintes critérios:
    - Retorno paginado, exibindo 10 itens por página 📄
    - Apenas pessoas com atributo ativo = true ✅
    - Banco de dados a escolha do desenvolvedor com tabela padrão:
        ID | NOME | DT_NASCIMENTO | ATIVO
- Logs da aplicação enviados para o Graylog 📊
- Docker Compose com todas as imagens necessárias (banco de dados, Graylog, aplicação, etc.) 🐳

## Recursos Principais 🔧
- CRUD de Pessoa (Criar, Ler, Atualizar, Deletar lógico) ✍️
- Paginação padrão: 10 itens por página 📚
- Retorno apenas de pessoas com ativo = true ✅
- Logs estruturados enviados ao Graylog 📈
- Execução containerizada (aplicação + banco + Graylog) 🧩

## Tecnologias 🧰
- Spring Boot (3+), Java, Maven ☕
- Docker e Docker Compose 🐳
- MongoDB para persistência 🍃
- Graylog para observabilidade 👀

## Pré-requisitos ⚙️
- Docker 24+ e Docker Compose 🐳
- JDK 21+ (para execução local sem Docker) ☕
- Maven 3.9+ (para build local) 🔨

## Início Rápido ⚡
```bash
# Clonar o repositório
git clone https://github.com/FatecAPIHub/modulo1
cd modulo1

# Subir tudo com Docker
docker compose up -d --build
```

Serviços:
- Graylog UI: http://localhost:9000 (usuário: admin, senha: admin) 🔐
- MongoDB: localhost:27017 🍃
- API: http://localhost:8080 🌐

## API 🛠️
Base path: /

Endpoints:
- POST / — cria uma pessoa ➕
- GET / — lista pessoas ativas (10 por página; paginação via query params) 📄
- PUT /{id} — atualiza pessoa por ID ✏️
- DELETE /{id} — desativa logicamente por ID 🗑️

Paginação:
- Parâmetros: page (0-based), size (default 10) ⚙️
- Somente registros com ativo = true são retornados ✅

Exemplos cURL:
```bash
# Criar
curl -X POST http://localhost:8080/ \
    -H "Content-Type: application/json" \
    -d '{"nome":"Maria","dt_nascimento":"1990-05-12","ativo":true}'

# Listar (página 0, 10 itens)
curl "http://localhost:8080/?page=0&size=10"

# Atualizar
curl -X PUT http://localhost:8080/123 \
    -H "Content-Type: application/json" \
    -d '{"nome":"Maria Silva"}'

# Deletar
curl -X DELETE http://localhost:8080/123
```

## Estrutura do Projeto 🗂️
```
src/                           - diretório raiz do código-fonte
└── main/                      - código principal da aplicação
    ├── java/br/com/fatec/modulo1/pessoa_api/  - pacote base Java
    │   ├── controller         - classes de controle (endpoints REST, recebem requisições)
    │   ├── dto                - objetos de transferência de dados (entrada/saída de APIs)
    │   ├── exceptions         - exceções personalizadas e handlers globais
    │   ├── logger             - configuração/utilitários de logging
    │   ├── model              - modelos/entidades de domínio (ex.: JPA)
    │   ├── repository         - interfaces de acesso a dados (ex.: Spring Data)
    │   └── services           - regras de negócio e orquestração (camada de serviço)
    └── resources/             - recursos não compilados do Java
        ├── static             - arquivos estáticos (CSS, JS, imagens)
        └── templates          - templates de visualização (ex.: Thymeleaf)
docker/openjdk/                - arquivos de Docker para imagem OpenJDK
client/                        - cliente da API (ex.: front-end ou scripts de consumo)
```

## Configuração 🧩
- application.properties: portas, DB, integração com Graylog ⚙️
- logback-spring.xml: layout dos logs, correlation id, envio para Graylog 📝

## Problemas Comuns ❗
- Porta 8080 ocupada: altere server.port em application.properties 🔀
- Graylog indisponível: verifique containers e a rede do docker compose 🧪
- Banco não acessível: confirme credenciais e mapeamentos no docker-compose.yml 🔐
- Logs não chegam no Graylog: valide configuração do logback e input do Graylog 📤
