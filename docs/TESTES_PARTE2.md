# Roteiro operacional da Parte 2

## Como usar este roteiro no Codex CLI

Execute as etapas na ordem, uma por vez, a partir da raiz do projeto. A próxima etapa deve ser executada somente quando solicitada explicitamente. Antes de cada etapa, releia o estado real do projeto e o status do Git. Testes não executados devem permanecer marcados como **PENDENTES**, nunca como aprovados. Este roteiro não autoriza commit, push, reset ou exclusões.

Os três componentes PrimeFaces considerados da Parte 1 são `p:panelGrid`, `p:commandButton` e `p:dataTable`. A Parte 2 acrescenta exatamente `p:autoComplete` (busca), `p:pickList` (interesses) e `p:dataView` (lista/cartões). `p:password` é obrigatório e substitui o antigo campo de senha. Cadastro e listagem permanecem na mesma página, atualizados por AJAX. Senha ou hash nunca pode aparecer na listagem; interesses devem ser persistidos pelo Hibernate; o ID deve ser gerado automaticamente. A Parte 1 permanece preservada em `main` e `parte1-original-funcional`. WildFly 40 ou 41 só é válido após confirmar a versão realmente usada nos arquivos e no ambiente.

## Etapas

### 1. Auditoria do estado atual

- **Objetivo:** confirmar branch, arquivos, dependências e requisitos sem confiar no histórico.
- **Pré-requisitos:** raiz do repositório e nenhum arquivo aberto para edição.
- **Arquivos envolvidos:** `README.md`, `docs/PLANO_PARTE2.md`, este documento, `pom.xml`, `compose.yaml`, `Dockerfile`, `src/main`, `src/test`, `tests`.
- **Comandos a executar:** `pwd`; `git status --short`; `git branch --show-current`; `rg --files --hidden -g '!.git'`; `sed -n '1,240p' README.md docs/PLANO_PARTE2.md docs/TESTES_PARTE2.md`; `rg -n 'jakarta|javax|primefaces|hibernate|mysql|wildfly|p:password|p:autoComplete|p:pickList|p:dataView' . -g '!target/**'`.
- **Resultado esperado:** estado real e divergências entre documentos e código identificados.
- **Critérios objetivos de aprovação:** três documentos lidos, branch registrada, arquivos relevantes localizados e mudanças preexistentes anotadas.
- **Testes manuais necessários:** comparar requisitos documentados com o XHTML, Java e scripts.
- **Evidências que devem ser registradas:** status, branch, lista de arquivos e divergências.
- **Possíveis erros e diagnóstico:** diretório errado (corrigir `pwd`); arquivo ausente (registrar e parar); árvore suja (não sobrescrever).
- **Situação atual:** **CONCLUÍDA** nesta auditoria.

### 2. Proteção da Parte 1 e referências Git

- **Objetivo:** provar que `main` e a tag original estão preservadas e o trabalho está na branch correta.
- **Pré-requisitos:** etapa 1 concluída; Git disponível.
- **Arquivos envolvidos:** referências Git e arquivos da árvore somente para leitura.
- **Comandos a executar:** `git branch -avv`; `git tag --list`; `git rev-parse main parte1-original-funcional parte2-customizada`; `git diff main...parte1-original-funcional --stat`; `git status --short`.
- **Resultado esperado:** `parte2-customizada` ativa; referências da Parte 1 existem e não são alteradas.
- **Critérios objetivos de aprovação:** branch atual é `parte2-customizada`; `main` e tag resolvem; nenhum comando mutou referências.
- **Testes manuais necessários:** confirmar que não há migração ou remoção do volume da Parte 1.
- **Evidências que devem ser registradas:** hashes, branches e status antes/depois.
- **Possíveis erros e diagnóstico:** branch errada (parar); referência ausente (não recriar); mudança de terceiro (registrar).
- **Situação atual:** **CONCLUÍDA**; repetir antes da entrega.

### 3. Java 25, Maven e configuração

- **Objetivo:** confirmar o JDK usado pelo compilador e pelos scripts.
- **Pré-requisitos:** JDK 25 e Maven instalados.
- **Arquivos envolvidos:** `pom.xml`, `iniciar.sh`, `README.md`.
- **Comandos a executar:** `export JAVA_HOME=/usr/lib/jvm/jdk-25.0.3-oracle-x64`; `export PATH="$JAVA_HOME/bin:$PATH"`; `java -version`; `mvn -version`; `mvn -B verify`.
- **Resultado esperado:** Java 25 é usado e o WAR/testes são gerados com sucesso.
- **Critérios objetivos de aprovação:** `java -version` inicia com 25; Maven mostra o mesmo `JAVA_HOME`; build termina com `BUILD SUCCESS`.
- **Testes manuais necessários:** verificar que `iniciar.sh` não força Tomcat ou outra versão.
- **Evidências que devem ser registradas:** versões, exit code e caminho do WAR.
- **Possíveis erros e diagnóstico:** Java incorreto (corrigir ambiente); dependência indisponível (registrar bloqueio); compilação falha (guardar log).
- **Situação atual:** **CONCLUÍDA** para build anterior; repetir após alterações.

### 4. Auditoria das dependências e do servidor

- **Objetivo:** validar Jakarta EE, PrimeFaces, Hibernate, Connector/J, MySQL e servidor efetivamente usados.
- **Pré-requisitos:** etapa 3; Docker para confirmação final.
- **Arquivos envolvidos:** `pom.xml`, `Dockerfile`, `compose.yaml`, `docker/configure.cli`, `persistence.xml`.
- **Comandos a executar:** `mvn dependency:tree`; `rg -n 'jakarta.jakartaee-api|primefaces|hibernate|mysql-connector|WILDFLY_VERSION|MYSQL_CONNECTOR_VERSION|mysql:8.4' pom.xml Dockerfile compose.yaml docker`; `unzip -l target/cadastro.war | rg 'WEB-INF/lib|hibernate|faces|weld|mysql'`.
- **Resultado esperado:** API Jakarta `provided`, PrimeFaces Jakarta no WAR, implementações do servidor fora do WAR e driver como módulo WildFly.
- **Critérios objetivos de aprovação:** versão efetiva coincide entre arquivos e ambiente; não há JAR duplicado; WildFly 40/41 só aprovado após log real.
- **Testes manuais necessários:** conferir versões com fontes oficiais e a versão exibida pelo servidor.
- **Evidências que devem ser registradas:** `mvn dependency:tree -Dincludes=jakarta.platform,org.primefaces,org.junit.jupiter` terminou com `BUILD SUCCESS` e confirmou Jakarta EE API 11.0.0 `provided`, PrimeFaces Jakarta 15.0.6 e JUnit 5.12.2. A inspeção confirmou MySQL 8.4, WildFly 40.0.0.Final e Connector/J 9.3.0 declarados. O WAR contém somente `primefaces-15.0.6-jakarta.jar` em `WEB-INF/lib` e não contém Hibernate, Faces, Weld ou MySQL. Docker confirmou `cadastro-parte2-app` saudável em 127.0.0.1:8082, MySQL saudável em 127.0.0.1:3307, Java Temurin 25.0.4 no app, DataSource `CadastroDS` habilitado, WildFly 40.0.0.Final, Hibernate ORM 7.3.2.Final, Weld processando `cadastro.war` e deployment concluído.
- **Possíveis erros e diagnóstico:** classifier `javax` (revisar dependência); JAR duplicado (revisar escopo); divergência de versão (marcar pendente). O log também registrou `WFLYCTL0056` ao renomear o histórico de configuração por diretório não vazio, mas o servidor iniciou e o WAR foi implantado; investigar se voltar a ocorrer.
- **Situação atual:** **CONCLUÍDA**, com a ressalva operacional do aviso `WFLYCTL0056`; a versão efetiva foi confirmada no log e coincide com o `Dockerfile`.

### 5. Ambiente, `.env.example` e credenciais

- **Objetivo:** validar variáveis, interpolação e ausência de credenciais versionadas.
- **Pré-requisitos:** Docker Compose e ferramentas de busca.
- **Arquivos envolvidos:** `.env.example`, `.gitignore`, `compose.yaml`, `iniciar.sh`, `encerrar.sh`.
- **Comandos a executar:** `git check-ignore .env`; `git grep -n -I -E 'DB_PASSWORD=|password[[:space:]]*=' -- . ':!.env.example' || true`; `docker compose -p cadastro-parte2 --env-file .env.example config`.
- **Resultado esperado:** apenas nomes de variáveis são versionados; `.env` ignorado e valores reais exigidos.
- **Critérios objetivos de aprovação:** nenhum segredo hardcoded; exemplo sem senha utilizável; configuração falha sem valores e passa com `.env` local protegido.
- **Testes manuais necessários:** conferir `chmod 600 .env` e redigir logs.
- **Evidências que devem ser registradas:** `.gitignore` cobre `.env`, `.env` não está versionado, permissões locais são `600`, o scan de segredos não encontrou credenciais em arquivos versionáveis, e `docker compose --env-file .env config --quiet` terminou com `OK`. A validação com `.env.example` falhou intencionalmente porque as variáveis estão vazias e o Compose exige valores reais; nenhum valor secreto foi exibido.
- **Possíveis erros e diagnóstico:** segredo encontrado (parar e rotacionar); `.env` versionado (corrigir antes de prosseguir).
- **Situação atual:** **CONCLUÍDA**. Interpolação com o `.env` local, proteção Git, permissões e scan de credenciais foram verificados; o `.env.example` foi confirmado como modelo sem credenciais utilizáveis.

### 6. MySQL 8.4 isolado

- **Objetivo:** iniciar banco, volume e porta separados da Parte 1.
- **Pré-requisitos:** `.env` preenchido e Docker disponível.
- **Arquivos envolvidos:** `compose.yaml`, `.env`, `docker/schema.sql`.
- **Comandos a executar:** `docker compose -p cadastro-parte2 --env-file .env up -d db`; `docker compose -p cadastro-parte2 ps`; `docker compose -p cadastro-parte2 port db 3306`; `docker volume ls | rg cadastro-parte2-mysql`; `docker compose -p cadastro-parte2 exec -T db mysqladmin ping -u root -p"$DB_ROOT_PASSWORD"`.
- **Resultado esperado:** MySQL 8.4 saudável em 127.0.0.1:3307, com volume próprio.
- **Critérios objetivos de aprovação:** imagem 8.4; porta externa 3307; volume `cadastro-parte2-mysql`; contêiner/porta da Parte 1 continuam intactos.
- **Testes manuais necessários:** conexão com usuário `cadastro`, sem consultar hash.
- **Evidências que devem ser registradas:** `cadastro-parte2-db-1` está `healthy` com imagem `mysql:8.4`; a porta publicada é `127.0.0.1:3307`; o volume `cadastro-parte2-mysql` existe; `mysqladmin ping` retornou `mysqld is alive`; conexão do usuário `cadastro` retornou banco `cadastro_parte2` e `cadastro@%`. A Parte 1 continua em `primefaces-mysql`, `mysql:5.7`, saudável e publicada em `127.0.0.1:3306`.
- **Possíveis erros e diagnóstico:** porta ocupada (identificar processo, não mexer na Parte 1); healthcheck falho (logs); volume incompatível (não remover).
- **Situação atual:** **CONCLUÍDA**. MySQL 8.4, banco, volume e porta isolados foram confirmados sem alterar a Parte 1.

### 7. Schema e relacionamentos

- **Objetivo:** validar usuários, interesses, chaves, limites e relacionamento.
- **Pré-requisitos:** banco saudável.
- **Arquivos envolvidos:** `docker/schema.sql`, `Usuario.java`, `tests/banco.sh`.
- **Comandos a executar:** `bash tests/banco.sh`; `docker compose -p cadastro-parte2 exec -T db mysql -u cadastro -p"$DB_PASSWORD" cadastro_parte2 -e 'SHOW CREATE TABLE usuarios; SHOW CREATE TABLE usuario_interesses;'`.
- **Resultado esperado:** `usuarios`, `usuario_interesses`, PK composta de interesse, FK e `AUTO_INCREMENT`.
- **Critérios objetivos de aprovação:** FK aponta para `usuarios(id)`; nomes/tipos concordam com a entidade; nenhum hash é impresso na saída registrada.
- **Testes manuais necessários:** criar usuário pela aplicação e confirmar interesses, sem SQL de alteração.
- **Evidências que devem ser registradas:** `tests/banco.sh` retornou MySQL 8.4.11, banco `cadastro_parte2` e zero registros iniciais. `usuarios` tem `id BIGINT NOT NULL AUTO_INCREMENT` como PK, `nome VARCHAR(100)`, `senha_hash VARCHAR(255)`, `descricao VARCHAR(500)` e `data_cadastro DATETIME(6)`. `usuario_interesses` tem PK composta (`usuario_id`,`interesse`) e FK `fk_interesse_usuario` para `usuarios(id)`, com InnoDB/utf8mb4. Nenhum hash foi impresso.
- **Possíveis erros e diagnóstico:** schema não inicializou (ver logs/volume); mismatch (comparar etapa 7); FK ausente (parar).
- **Situação atual:** **CONCLUÍDA** para o schema efetivo e seus metadados. O teste manual de criar pela aplicação e confirmar interesses fica registrado para a validação integrada posterior; não foi executado nesta etapa.

### 8. JPA/Hibernate, EntityManager, DataSource e transações

- **Objetivo:** validar persistence unit, JNDI, EntityManager e JTA.
- **Pré-requisitos:** banco e imagem WildFly disponíveis.
- **Arquivos envolvidos:** `persistence.xml`, `docker/configure.cli`, `UsuarioDAO.java`, `UsuarioService.java`.
- **Comandos a executar:** `docker compose -p cadastro-parte2 up -d --build app`; `docker compose -p cadastro-parte2 logs app | rg -i 'started|hibernate|datasource|error|failed'`; `docker compose -p cadastro-parte2 exec app /opt/wildfly/bin/jboss-cli.sh --connect --command='/subsystem=datasources/data-source=CadastroDS:read-resource'`.
- **Resultado esperado:** deployment sem erro, datasource `java:/jdbc/CadastroDS` habilitado e Hibernate valida schema.
- **Critérios objetivos de aprovação:** `@PersistenceContext(unitName="cadastro")`; DAO usa JPQL; Service tem `@Transactional`; `hbm2ddl.auto=validate`.
- **Testes manuais necessários:** cadastrar/buscar e reiniciar app sem perda.
- **Evidências que devem ser registradas:** `persistence.xml` declarou unidade JTA `cadastro`, provider Hibernate e `java:/jdbc/CadastroDS`, com `hibernate.hbm2ddl.auto=validate`. O código confirmou `@PersistenceContext(unitName="cadastro")`, `EntityManager`, JPQL parametrizado e `@Transactional`. O contêiner estava `healthy`; CLI retornou `outcome => success`, `enabled => true`, JNDI `java:/jdbc/CadastroDS`, driver `mysql`, `jta => true`, URL interna `db:3306/cadastro_parte2` e usuário `cadastro` (a senha permaneceu como expressão `${env.DB_PASSWORD}`). Logs confirmaram WildFly 40.0.0.Final, Hibernate ORM 7.3.2.Final, persistence unit em duas fases, Weld processando `cadastro.war` e `Deployed "cadastro.war"`. O aviso `WFLYCTL0056` do histórico de configuração não impediu o início.
- **Possíveis erros e diagnóstico:** JNDI ausente (CLI); schema inválido (etapa 7); driver ausente (módulo); transação ausente (log).
- **Situação atual:** **CONCLUÍDA** para configuração e deployment JPA/Hibernate/DataSource/JTA. O cadastro/busca seguido de reinício, teste manual previsto nesta etapa, não foi executado para não antecipar a etapa de interface; fica pendente para a validação integrada.

### 9. Arquitetura MVC multicamada

- **Objetivo:** verificar entidade, DAO, Service e Controller CDI separados.
- **Pré-requisitos:** fonte disponível e etapa 8 para integração.
- **Arquivos envolvidos:** `model/Usuario.java`, `dao/UsuarioDAO.java`, `service/*`, `managedbeans/UsuarioManagedBean.java`, `WEB-INF/beans.xml`.
- **Comandos a executar:** `rg -n '@Entity|@PersistenceContext|@ApplicationScoped|@Transactional|@Named|@ViewScoped|@Inject' src/main`; `rg -n 'DriverManager|PreparedStatement|javax\.|UsuarioMB|senhaHash|setId' src/main`.
- **Resultado esperado:** CDI injeta Controller/Service/DAO; banco só é acessado pelo DAO; view recebe resumo sem hash.
- **Critérios objetivos de aprovação:** ausência de JDBC e `javax`; Controller não cria conexão; `UsuarioResumo` não possui hash; CDI habilitado.
- **Testes manuais necessários:** seguir cadastro/consulta e conferir logs/camadas quando possível.
- **Evidências que devem ser registradas:** as anotações confirmam `@Entity` em `Usuario`, `@ApplicationScoped`/`@PersistenceContext` em `UsuarioDAO`, `@ApplicationScoped`/`@Transactional`/`@Inject` em `UsuarioService` e `@Named("usuarioController")`/`@ViewScoped`/`@Inject` no Controller. `rg` não encontrou `DriverManager`, `PreparedStatement`, `javax.` de APIs Jakarta, `UsuarioMB` ou `setId` na aplicação. A única ocorrência de `senhaHash` está na entidade persistente; `UsuarioResumo` contém somente id, nome, descrição, data e interesses. O log do WildFly confirmou `WFLYWELD0003: Processing weld deployment cadastro.war` e deployment concluído.
- **Possíveis erros e diagnóstico:** bean não resolvido (beans/anotações); SQL na view (parar); hash no DTO (remover).
- **Situação atual:** **CONCLUÍDA** por inspeção e confirmação do deployment CDI/Weld. O percurso manual cadastro/consulta ainda não foi executado e permanece reservado à validação integrada posterior.

### 10. ID autoincrementável

- **Objetivo:** provar que o ID é gerado pelo banco e não informado pelo usuário.
- **Pré-requisitos:** etapas 7 e 8.
- **Arquivos envolvidos:** `Usuario.java`, `index.xhtml`, `docker/schema.sql`, `UsuarioService.java`.
- **Comandos a executar:** `rg -n 'GeneratedValue|GenerationType.IDENTITY|spinner|setId' src/main docker/schema.sql`; `bash tests/banco.sh`.
- **Resultado esperado:** nenhum campo de ID no formulário; dois cadastros recebem IDs distintos.
- **Critérios objetivos de aprovação:** `@GeneratedValue(strategy=IDENTITY)` e `AUTO_INCREMENT`; nenhum `p:spinner`/setter de ID na tela; IDs distintos no banco.
- **Testes manuais necessários:** cadastrar dois usuários e anotar IDs exibidos.
- **Evidências que devem ser registradas:** `Usuario.java` contém `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` sobre `Long id`; `docker/schema.sql` contém `id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY`; `index.xhtml` não possui `p:spinner`, input ou binding para ID; o Controller não possui setter de ID. A consulta de metadados retornou `id | auto_increment` e o banco estava vazio (`0` usuários, `0` IDs distintos), sem inserir dados artificialmente.
- **Possíveis erros e diagnóstico:** ID nulo (schema/transação); campo visível (XHTML); sequência repetida (volume/schema).
- **Situação atual:** **CONCLUÍDA** por inspeção do código e schema. O cadastro de dois usuários pela interface para observar IDs distintos não foi executado nesta etapa e permanece **PENDENTE** para a validação integrada.

### 11. Página única e AJAX

- **Objetivo:** validar cadastro e listagem na mesma página, com atualização parcial e sem navegação.
- **Pré-requisitos:** aplicação saudável e Chrome/Playwright.
- **Arquivos envolvidos:** `index.xhtml`, Controller CDI, `tests/browser.cjs`.
- **Comandos a executar:** `curl -fsS http://localhost:8082/cadastro/`; `PLAYWRIGHT_MODULE=/tmp/cadastro-parte2-browser/node_modules/playwright node tests/browser.cjs`.
- **Resultado esperado:** página renderiza; POST AJAX retorna `partial-response`; mensagem e dataView atualizam.
- **Critérios objetivos de aprovação:** URL não muda; POST 200; usuário aparece sem reload; listagem não depende de outra página.
- **Testes manuais necessários:** observar Network durante cadastro e busca.
- **Evidências que devem ser registradas:** `curl` para `http://localhost:8082/cadastro/` retornou HTTP 200 e HTML de 12.996 bytes. O Chrome encontrou o título `Cadastro de usuários — Parte 2`, os dois botões e a listagem na mesma página. O teste Playwright focado nesta etapa observou `POST` HTTP 200, corpo contendo `<partial-response`, URL inalterada, `cadastro:salvar` presente uma vez, `consulta:buscar` presente uma vez e `consulta:usuarios` presente uma vez.
- **Possíveis erros e diagnóstico:** 404 (context root); HTML completo (AJAX); view expirada (sessão); erro JS (console).
- **Situação atual:** **CONCLUÍDA** para página única, requisição AJAX parcial e permanência na URL. O teste específico de cadastrar um novo usuário e comprovar sua atualização na listagem permanece reservado às etapas integradas de senha, componentes e regressão.

### 12. `p:password`, validações e armazenamento seguro

- **Objetivo:** validar campo obrigatório, regras de servidor, descarte da senha e hash seguro.
- **Pré-requisitos:** etapa 11 e banco acessível.
- **Arquivos envolvidos:** `index.xhtml`, `UsuarioService.java`, `SenhaHash.java`, `UsuarioResumo.java`, `tests/browser.cjs`, `tests/banco.sh`.
- **Comandos a executar:** `rg -n 'p:password|redisplay|validateLength|PBKDF2|600_000|senha_hash|UsuarioResumo' src/main tests`; `PLAYWRIGHT_MODULE=/tmp/cadastro-parte2-browser/node_modules/playwright node tests/browser.cjs`; `bash tests/banco.sh`.
- **Resultado esperado:** senha mascarada, 12–128 caracteres, PBKDF2-HMAC-SHA256 com salt individual e sem senha/hash na listagem.
- **Critérios objetivos de aprovação:** Service rejeita entradas inválidas; hashes da mesma senha diferem; banco mostra formato `pbkdf2-sha256$600000$...`; respostas não contêm senha/hash.
- **Testes manuais necessários:** vazio, curta, válida e erro de interesses; confirmar campo limpo e mensagens compreensíveis.
- **Evidências que devem ser registradas:** `rg` confirmou `p:password redisplay="false"`, limites 12–128, PBKDF2WithHmacSHA256 e 600.000 iterações. Os testes unitários anteriores confirmaram validação e salt individual. No banco, uma consulta sem exibir o valor retornou `1` usuário, `1` hash com 90 bytes codificados e `1` hash iniciando no formato `pbkdf2-sha256$600000$`. Você confirmou que todas as comprovações de navegador desta etapa passaram: senha curta rejeitada, cadastro válido, limpeza do campo após erro/sucesso e ausência de senha/hash na resposta e na listagem.
- **Possíveis erros e diagnóstico:** senha aparece (parar); hash no DTO (remover); apenas validação client-side (testar POST); algoritmo divergente (comparar código).
- **Situação atual:** **CONCLUÍDA**, com base nos testes unitários, inspeção do banco e confirmação manual de navegador fornecida pelo usuário. A confirmação de navegador não foi executada diretamente por esta sessão, mas foi registrada como evidência declarada.

### 13. Os três componentes adicionais

- **Objetivo:** validar `p:autoComplete`, `p:pickList` e `p:dataView`.
- **Pré-requisitos:** etapa 11 e usuário/catálogo disponíveis.
- **Arquivos envolvidos:** `index.xhtml`, Controller, DAO/Service, `tests/browser.cjs`.
- **Comandos a executar:** `rg -n 'p:autoComplete|p:pickList|p:dataView|completeMethod|DualListModel|dataViewListItem|dataViewGridItem' src/main`; `PLAYWRIGHT_MODULE=/tmp/cadastro-parte2-browser/node_modules/playwright node tests/browser.cjs`.
- **Resultado esperado:** autocomplete sugere/busca; pickList transfere interesses; dataView apresenta lista e cartões.
- **Critérios objetivos de aprovação:** autocomplete consulta e filtra; pickList aceita catálogo e interesses reaparecem após reload; dataView exibe dados permitidos sem senha/hash.
- **Testes manuais necessários:** digitar dois caracteres, selecionar sugestão, transferir dois interesses, recarregar e alternar apresentação.
- **Evidências que devem ser registradas:** a inspeção estática confirmou as tags e bindings de `p:autoComplete`, `p:pickList` e `p:dataView`, incluindo `dataViewListItem` e `dataViewGridItem`. O Chrome confirmou no runtime um autocomplete (`#consulta:busca_input`), um pickList (`#cadastro:interesses`), um dataView (`#consulta:usuarios`) e duas opções de layout. A interação completa de sugestão, transferência e alternância não produziu saída conclusiva nesta execução; não foi declarada aprovada.
- **Possíveis erros e diagnóstico:** sugestões vazias (DAO/termo); interesses não salvos (binding/transação/FK); cartões vazios (facets/layout).
- **Confirmação manual recebida em 10/09/2026:** o usuário informou que tudo está funcionando nesta etapa, confirmando o funcionamento dos três componentes no navegador. Esta é uma evidência declarada pelo usuário; não representa aprovação do teste automatizado de interação, que permaneceu inconclusivo, nem substitui os testes de banco e regressão da etapa 14.
- **Situação atual:** **CONCLUÍDA**, com base na inspeção de código, renderização observada no Chrome e confirmação manual do usuário. A etapa 14 permanece pendente e depende de solicitação explícita para execução.

### 14. Testes automatizados, HTTP, banco, navegador e regressão

- **Objetivo:** executar a matriz completa e separar aprovados de não testados.
- **Pré-requisitos:** etapas 1–13; Docker, Maven, Chrome/Playwright e Parte 1 acessível quando aplicável.
- **Arquivos envolvidos:** `src/test`, `tests/browser.cjs`, `tests/banco.sh`, scripts e endpoints 8082/3307.
- **Comandos a executar:** `mvn -B verify`; `bash -n iniciar.sh encerrar.sh`; `git diff --check`; `docker compose -p cadastro-parte2 config --quiet`; `curl -i http://localhost:8082/cadastro/`; `PLAYWRIGHT_MODULE=/tmp/cadastro-parte2-browser/node_modules/playwright node tests/browser.cjs`; `bash tests/banco.sh`; `curl -fsS http://localhost:8081/primefaces-0.0.1/` se a Parte 1 estiver ativa.
- **Resultado esperado:** testes unitários, HTTP, banco e navegador passam; Parte 1 permanece acessível e intacta.
- **Critérios objetivos de aprovação:** exit code 0 em cada comando; HTTP 200; navegador sem erros JS; persistência após reload/restart; regressão 8081 aprovada.
- **Testes manuais necessários:** repetir etapas 10–13 e comparar a tela da Parte 1.
- **Evidências que devem ser registradas:** `mvn -o -B -Dmaven.repo.local=/tmp/cadastro-parte2-m2 verify` terminou com `BUILD SUCCESS` e 2 testes sem falhas; `bash -n iniciar.sh encerrar.sh`, `git diff --check` e `docker compose ... config --quiet` passaram; HTTP da Parte 2 retornou 200 (13.640 bytes); `tests/banco.sh` retornou MySQL 8.4.11, 2 usuários, 2 hashes no formato esperado, 2 IDs distintos, interesses persistidos e `auto_increment`; HTTP da Parte 1 em 8081 retornou 200 (4.848 bytes); serviços ficaram saudáveis em 8082/3307. O teste `tests/browser.cjs` foi executado, criou dados de teste, mas falhou na asserção de sucesso porque a interface retornou `Selecione pelo menos um interesse válido, sem repetições.` após a transferência esperada. Nenhuma senha ou hash completo foi registrado.
- **Possíveis erros e diagnóstico:** Docker indisponível (bloqueada); download incompleto (pendente); navegador ausente (instalar em `/tmp`); regressão 8081 (parar, preservar Parte 1).
- **Confirmação manual recebida em 10/09/2026:** você informou que repetiu o teste completo e ele passou, incluindo o fluxo de navegador que havia falhado anteriormente. Esta confirmação atualiza o resultado funcional do teste; os dois registros criados na execução anterior permanecem no volume da Parte 2.
- **Situação atual:** **CONCLUÍDA**, com base nos testes automatizados, HTTP, banco, regressão da Parte 1 e confirmação manual da repetição bem-sucedida do navegador. A nova execução do navegador não foi reproduzida diretamente nesta sessão; a evidência está identificada como confirmação do usuário.

### 15. Documentação final, checklist, commit e push opcional

- **Objetivo:** documentar resultados, pendências e procedimento de entrega sem publicar automaticamente.
- **Pré-requisitos:** etapa 14 concluída ou falhas classificadas; autorização explícita para commit/push.
- **Arquivos envolvidos:** `docs/TESTES_PARTE2.md` e referências Git; não alterar código nesta etapa.
- **Comandos a executar:** `git status --short`; `git diff --check`; `git diff -- docs/TESTES_PARTE2.md`; `git diff --stat`; `git add docs/TESTES_PARTE2.md && git commit -m 'docs: roteiro operacional da parte 2'` somente autorizado; `git push origin parte2-customizada` somente autorizado.
- **Resultado esperado:** checklist completo, cada etapa com situação e limitações reais.
- **Critérios objetivos de aprovação:** nenhuma etapa pendente é descrita como aprovada; main/tag/Parte 1 preservadas; commit/push só com autorização.
- **Testes manuais necessários:** outra pessoa segue o roteiro sem ler o chat; conferir nomes, portas, URLs e comandos.
- **Evidências que devem ser registradas:** diff final e hash do commit se autorizado, sem `.env`.
- **Possíveis erros e diagnóstico:** documento diverge do código (voltar à etapa 1); teste omitido (marcar pendente); push rejeitado (não forçar).
- **Checklist final revisado em 10/09/2026:** Jakarta Faces/PrimeFaces, Hibernate/JPA, MVC/CDI, ID automático, MySQL 8.4 isolado, DataSource/JTA, página única/AJAX, `p:password`, `p:autoComplete`, `p:pickList`, `p:dataView`, interesses persistidos, hash seguro e preservação da Parte 1 estão descritos e têm evidências nas etapas anteriores. Permanecem como pendências documentais os testes manuais de reinício previstos nas etapas 7–10 e o teste automatizado de interação do navegador que não foi reproduzido diretamente nesta sessão, embora a repetição tenha sido confirmada pelo usuário na etapa 14. O aviso `WFLYCTL0056` também permanece registrado como ressalva operacional.
- **Situação atual:** **CONCLUÍDA** quanto à revisão e documentação final. Não houve commit nem push; ambos permanecem opcionais e dependem de autorização explícita.

## Prompt operacional para a próxima etapa

“Leia docs/TESTES_PARTE2.md, localize a primeira etapa pendente e execute somente essa etapa. Confira o estado real do projeto antes de agir. Não avance para etapas posteriores. Execute os testes disponíveis, atualize o status da etapa no documento e informe os resultados, evidências, limitações e qualquer validação manual necessária. Não faça commit nem push sem autorização explícita.”
