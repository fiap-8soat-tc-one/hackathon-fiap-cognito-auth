# AWS Lambda com Autenticação Cognito 🚀

## 📘 Visão Geral
Este repositório contém uma função AWS Lambda escrita em **Java**, empacotada com **Docker**, que gerencia autenticação de usuários usando **Amazon Cognito**.

Essa função é usada no contexto do Hackaton da FIAP para viabilizar autenticação e autorização segura de usuários. A Lambda é exposta por meio do **API Gateway**, permitindo que frontends ou clientes autentiquem usuários de forma segura e escalável.

---

## 🔐 Por que usar Autenticação com Cognito?

O **Amazon Cognito** oferece um serviço robusto de autenticação com:

- ✅ **Segurança**: Autenticação e autorização com padrões da indústria
- ✅ **Escalabilidade**: Gerencia milhões de usuários com gestão integrada
- ✅ **Flexibilidade**: Suporta múltiplos fluxos de autenticação e provedores de identidade
- ✅ **Conformidade**: Ajuda a atender requisitos de segurança e privacidade

---

## ☕ Por que usar Java na Lambda?

Embora a AWS Lambda suporte múltiplas linguagens, o Java traz os seguintes benefícios:

- 🔒 **Tipagem forte**: Menor risco de erros em tempo de execução
- 🧰 **Ecossistema maduro**: Acesso a bibliotecas corporativas e ferramentas como Maven e Spring
- ⚙️ **Performance consistente**: Especialmente útil em workloads computacionais mais pesados
- 📦 **Empacotamento com Docker**: Evita problemas de cold start e permite melhor controle do ambiente

---

## 🐳 Vantagens de rodar Lambda com Docker

- 📦 **Ambiente personalizado**: Controle total sobre bibliotecas, runtime e dependências
- 💼 **Adoção corporativa**: Ideal para equipes que já utilizam Java e Docker
- 🧪 **Testabilidade**: Pode ser testado localmente com `sam local` ou `docker run`
- 🔁 **Portabilidade**: O mesmo container pode ser usado em outros ambientes (ECS, Fargate, etc)

---

## 🧪 Estrutura do Projeto

```bash
.
└── HackatonFiapCognitoAuth/
    └── src/
        └── Dockerfile               # Define imagem Java 21 com Lambda
        └── pom.xml                 # Build Maven com dependências AWS
        └── main/java/auth/
            └── LambdaHandler.java  # Código Java principal

---

## 🔄 Fluxo de Funcionamento

1. O frontend faz uma requisição HTTP POST ao endpoint da Lambda via API Gateway.
2. A Lambda valida credenciais contra o pool de usuários do Cognito
3. Se bem-sucedido, retorna o JWT


---

## 📎 Exemplo de Resposta JSON
```json
{
  "AccessToken": "eyJz9sdfn....",
  "IdToken": "eyJ0eXAiOi...",
  "RefreshToken": "eyJjdHkiOi...",
  "ExpiresIn": 3600
}
```

---

## 🧰 O que é o AWS SAM?

O **AWS Serverless Application Model (SAM)** é uma ferramenta open-source da AWS que facilita o desenvolvimento, teste e deployment de aplicações serverless. Ele permite definir a infraestrutura como código (IaC) com uma sintaxe simplificada baseada em CloudFormation e executar Lambdas localmente com Docker.

### 📌 Vantagens do SAM
- Criação de APIs, Lambdas, DynamoDB, S3 e outros recursos com poucas linhas de YAML
- Permite **testes locais** com `sam local invoke` e `sam local start-api`
- Deploy simplificado com `sam deploy`
- Integração com CI/CD e outras ferramentas AWS

### 🧪 Instalação do SAM
```bash
# No macOS (Homebrew)
brew tap aws/tap
brew install aws-sam-cli

# No Ubuntu/Linux
curl -Lo sam-install.sh https://github.com/aws/aws-sam-cli/releases/latest/download/install
chmod +x sam-install.sh && ./sam-install.sh

# Verifique a instalação
sam --version
```

### ▶️ Rodando a aplicação localmente
```bash
# Build do projeto (compila e prepara Docker)
sam build

# Executa a API localmente (via API Gateway emulado)
sam local start-api

# Testa função individual com evento de entrada
sam local invoke "LambdaHandler"
```

---

## 🐳 O que é Docker?

**Docker** é uma plataforma para desenvolver, empacotar e executar aplicações em contêineres. Ele permite isolar a aplicação do sistema operacional do host, garantindo consistência entre ambientes de desenvolvimento e produção.

### 🔧 Por que usar Docker com Lambda
- Facilita testes locais
- Evita problemas de ambiente/desempenho
- Permite empacotar dependências nativas e bibliotecas Java
- Possibilita simular exatamente o ambiente da AWS

### 💻 Como instalar o Docker

#### Windows ou macOS
- Baixe e instale pelo site oficial: [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)

#### Ubuntu Linux
```bash
sudo apt update
sudo apt install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
```
(⚠️ Faça logout/login após rodar `usermod`)

### ▶️ Rodar Lambda manualmente com Docker
```bash
# Build da imagem localmente
docker build -t lambda-java-presigned-url .

# Executa a função Lambda local com entrada via stdin
echo '{}' | docker run -i lambda-java-presigned-url
```

---

## ✅ Requisitos
- AWS CLI configurado
- Docker instalado
- AWS SAM CLI para build e deploy (opcional)

---

## 🚀 Deploy com SAM
```bash
sam build
sam deploy --guided
```

---

## ✉️ Contato
Para dúvidas ou sugestões, entre em contato com o time técnico responsável pelo Hackaton FIAP.

