# Plano da Parte 2

Inspeção inicial em 09/09/2026: árvore limpa, branch `parte2-customizada`, nenhum AGENTS.md encontrado no projeto ou ancestrais. `main` = 3b9a6f0cc666a0b1a256486e6ceeb9817f0b28b6; objeto da tag anotada `parte1-original-funcional` = 9f5562967f07ab2c2684a828886fe55a02964f3e. Não modificar essas referências, executar push ou remover volumes.

O projeto original contém pom.xml (WAR, Faces 2.1.13/PrimeFaces 5.1), Connect JDBC, Usuario, UsuarioManagedBean, index.xhtml/listagem.xhtml, compose MySQL 5.7 e scripts vinculados ao Tomcat e MySQL local. Java 25 está instalado; WildFly não foi encontrado em ~/Ferramentas.

1. Substituir javax/JDBC por Jakarta EE 11 (provided), entidade JPA, DAO com EntityManager, Service transacional e Controller CDI. ID IDENTITY, interesses persistidos, validação no servidor e PBKDF2-HMAC-SHA256 com salt aleatório e 600.000 iterações.
2. Reunir cadastro e listagem na index.xhtml: password sem redisplay, autoComplete para busca por nome, pickList de interesses e dataView lista/cartões, com AJAX. Sem login, edição ou exclusão.
3. Isolar a execução em Docker Compose `cadastro-parte2`, MySQL 8.4 e volume próprio; WildFly padrão 40.0.0.Final em Java 25. Publicar somente loopback 8082/3307; não controlar Tomcat/MySQL local. Credenciais apenas em .env ignorado. Hibernate/Faces/CDI fornecidos pelo servidor, driver JDBC instalado no servidor.
4. Atualizar iniciar.sh/encerrar.sh e documentação. Executar testes de regras/hash, build, inspeção do WAR, configuração Docker, implantação e testes HTTP/navegador/banco quando disponíveis. Registrar resultados reais e limitações.

Fontes oficiais consultadas antes da fixação:
- https://www.wildfly.org/news/2026/05/21/WildFly-40-is-released/ — EE 11, Hibernate 7.3.2 e recomendação de Java 25; certificação EE citada em Java 17/21, distinta da recomendação de execução.
- https://github.com/primefaces/primefaces — variante Jakarta compatível com Faces 4+.
- https://dev.mysql.com/doc/connector-j/en/connector-j-versions.html — compatibilidade de Connector/J com MySQL e Java.
- https://hub.docker.com/_/mysql — imagem oficial e inicialização por variáveis.
- https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html — PBKDF2-HMAC-SHA256, 600.000 iterações.

A Parte 1 permanece recuperável pela tag e main; nenhum dado será migrado ou copiado automaticamente.
