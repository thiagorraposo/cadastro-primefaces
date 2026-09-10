# Cadastro — Parte 2

Projeto acadêmico MVC: Jakarta Faces + PrimeFaces, CDI, Service transacional, DAO e entidade Jakarta Persistence/Hibernate. Cadastro e listagem ficam em uma página, sem login, edição ou exclusão. Requer Docker Compose v2, Maven e JDK 25; funciona pelo terminal, sem IntelliJ Ultimate.

## Executar

Na branch `parte2-customizada`:

```bash
cp .env.example .env # somente se .env ainda não existir
# Preencha DB_PASSWORD e DB_ROOT_PASSWORD com senhas distintas.
chmod 600 .env
./iniciar.sh
```

Abra http://localhost:8082/cadastro/. O script compila e testa com Java 25 (padrão `/usr/lib/jvm/jdk-25.0.3-oracle-x64`, substituível por JAVA_HOME), constrói a imagem e aguarda a saúde dos dois serviços. A primeira execução baixa dependências e imagens. O WildFly é instalado na imagem; não precisa existir no host. Um .env local pode já estar preparado com valores aleatórios durante a verificação; não o substitua com banco já inicializado.

```bash
./encerrar.sh
# Diagnóstico (não publique logs/configurações com segredos):
docker compose -p cadastro-parte2 ps
docker compose -p cadastro-parte2 logs --tail=100 app
```

Encerrar usa `stop`, preservando o volume. Não use `down -v`. A Parte 1 mantém Tomcat, contêiner `primefaces-mysql`, porta 3306 e seu volume; os scripts novos não controlam MySQL local nem Tomcat. O banco da Parte 2 tem volume `cadastro-parte2-mysql`, porta loopback 3307 e schema `cadastro_parte2`. O app publica apenas loopback 8082; a porta administrativa do WildFly não é publicada. O HTTP e JDBC sem TLS são exclusivos deste ambiente local isolado, não uma configuração para hospedagem pública.

O SQL de `docker/schema.sql` roda apenas no primeiro uso do volume. Hibernate valida o schema, sem apagar/recriar dados. Alterar senhas no .env depois disso não altera contas já criadas no MySQL. Não apague o volume para resolver divergências; ajuste as credenciais locais existentes.

## Organização e versões

- `model/Usuario`: entidade, IDENTITY e coleção persistida em `usuario_interesses`.
- `dao/UsuarioDAO`: consultas JPQL parametrizadas; busca literal por parte do nome, até dez sugestões.
- `service/UsuarioService`: regras e transações JTA. `SenhaHash`: PBKDF2-HMAC-SHA256, 600.000 iterações, salt aleatório de 16 bytes e chave de 32 bytes.
- `service/UsuarioResumo`: dados de listagem sem hash; Controller CDI ViewScoped mantém estado da tela e trata falhas.
- `index.xhtml`: formulários independentes para cadastrar e buscar; AJAX atualiza mensagens e listagem. `p:autoComplete`, `p:pickList` e `p:dataView` são os três componentes adicionais. O seletor nativo de layout do dataView alterna lista/cartões.
- Java 25; WildFly padrão 40.0.0.Final (EE 11, Hibernate 7.3.2); PrimeFaces 15.0.6 com classifier jakarta; Connector/J 9.3.0; MySQL 8.4. Tags de imagens Java/MySQL acompanham patches das respectivas linhas.

Jakarta EE tem escopo provided. O WAR contém PrimeFaces; não inclui Hibernate, Mojarra, Weld ou driver MySQL. O driver é instalado como módulo WildFly. A senha é descartada antes da renderização, inclusive em validação malsucedida, e nunca é listada. Não há migração automática de dados ou senhas da Parte 1.

Fontes e planejamento: [docs/PLANO_PARTE2.md](docs/PLANO_PARTE2.md). Roteiro e resultados: [docs/TESTES_PARTE2.md](docs/TESTES_PARTE2.md).
