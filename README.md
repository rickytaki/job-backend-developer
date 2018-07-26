# Intelipost: Teste prático para Backend Developer

### Live

Este projeto está rodando na AWS em uma EC2, em um container Docker usando ECS, ECR, Elastic Cache, RDS e uma CDN no link: [ch.rickytaki.com/users](ch.rickytaki.com/users). Se colocado atrás de um Load Balancer, é possível aumentar a quantidade de serviços rodando de forma simples na AWS.

#### Create POST

[ch.rickytaki.com/users/create](ch.rickytaki.com/users/create)

Está público, sem restrições de autenticação ou autorização.

JSON Exemplo: 

```
{
    "email": "teste@intelipost.com",
    "name": "test",
    "password": "testando@123",
    "age": 33,
    "street": "test street",
    "number": 33,
    "zipCode": "015000-010"
}
```


#### Find By name GET

[ch.rickytaki.com/users/findByName/NOME](ch.rickytaki.com/users/findByName/NOME)
  
Substitua NOME pelo nome procurado e envie email e password no header, se estiver Postman, bast selecionar GET e enviar basic auth com email e password.
  
Exemplo caso tenha criado o usuario do create acima: [ch.rickytaki.com/users/findByName/test]().

#### Find By Email GET

[ch.rickytaki.com/users/findByEmail/EMAIL](ch.rickytaki.com/users/findByEmail/EMAIL)

Substitua EMAIL pelo email do usuário procurado e envie email e password no header, se estiver Postman, bast selecionar GET e enviar basic auth com email e password.
  
Exemplo caso tenha criado o usuario do create acima: [ch.rickytaki.com/users/findByName/teste@intelipost.com]().

### Rodando localmente

Para rodar localmente, você deve ter o [Docker](https://docs.docker.com/install/) e o [Docker-Compose](https://docs.docker.com/compose/install/) instalado na sua máquina.

Após instalar, no diretório raiz do projeto, rode o comando:

```
docker-compose up
```
Ou com a flag -d (--detach) caso não queira ver mensagens de log.
```
docker-compose up -d
```

E pronto! Você agora tem um Redis, um postgres e o serviço rodando localmente na sua máquina na porta ```8080```, dentro de uma network privada criada pelo docker-compose. Você pode acessar pelos links: 

```
localhost:8080/users/create
localhost:8080/users/findByName/NOME
localhsot:8080/users/findByEmail/EMAIL
```
Lembrando de seguir as mesmas regras expostas acima.

Para derrubar tudo, rode o comando:
```
docker-compose down
```
Se você deu CTRL+C por que estava no modo attached, mesmo assim rode o comando para derrubar a rede e containers.

### Executando testes unitários separadamente
Necessário ter a [jdk8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) e o [maven](https://maven.apache.org/install.html) instalados na sua máquina.

Para executar teste unitários, rode no raiz do projeto o comando:
```
mvn test
```
Você verá ao final o resultado dos testes passando
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
```

### Executando testes de integração separadamente
Necessário ter a [jdk8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) e o [maven](https://maven.apache.org/install.html) instalados na sua máquina.

Para rodar os testes de integração, primeiro rode os comandos:
```
docker-compose up db
docker-compose up redis
```

Depois de subir o postgres e o redis, rode o seguinte comando na raiz do projeto:
```
mvn failsafe:integration-test
```

### Executando todos os testes
Necessário ter a [jdk8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) e o [maven](https://maven.apache.org/install.html) instalados na sua máquina.

Rode o seguinte comando na raiz do projeto:
```
mvn verify
```

### Resposta a pergunta:
Para descobrir os possíveis gargalos, deve-se analisar a infra:
- Hardware do servidor: Os recursos estão provisionados de acordo com a necessidade do sistema!? É possível clonar a infra e rodar um stress test? É possível distribuir a aplicação em múltiplos servidores?
- Latência da rede: Os servidores e banco de dados estão em regiões distantes? Ou então estão longe da localização dos usuários alvo?
- Aplicação: É distribuida? Tem Cache? Expõe algum tipo de ListAll mesmo que paginado? Existe algum serviço requisitando informações ao mesmo tempo do pico de requests?
- As colunas do BD estão indexadas?

Além disso, deve-se analisar a arquitetura do sistema e, partindo do pressuposto de algo legado e deficitário, propus e implementei o seguinte:

- JDBC template ao invés de JPA;
- Undertow ao invés de Jetty ou Tomcat;
- Cache Redis na nuvem para que seja possivel escalar verticalmente;
- CDN /(CloudFlare);
- Containers rodando em configuração de Cluster para que aumentar o numero de instâncias seja rápido e trivial;
- Banco de dados, cache e aplicação na mesma região /(US Virginia pois o free tier da AWS nao permite BR).
- Back e front desacoplado, permitindo escalar qualquer um dos dois separadamente.

#### Observações

- Uso de Cache implica em consistência eventual e consequentemente menor segurança, até onde isso é aceitável? Quais os requisitos;
- Não foi implementado token /(Ex.: JWT) por haver apenas UMA requisição e mesmo com tokens, seria necessário o envio de credenciais no login para obter o access token, causando apenas overhead;
- Caso fosse uma aplicação mais complexa e/ou distribuída mas com o mesmo volume de acessos, deveríamos partir para um auth server que teria por responsabilidade apenas conceder o jwt e cada serviço teria seu mapper para então obter os claims do payload /(caso autenticação seja um issue) e orquestrar as multiplas instancias de cada serviço por um service discovery /(Ex.: Eureka) e carregando as configurações de um config server;
- Poderíamos considerar o autoScaling, automatizando a replica de instancias nos momentos de pico e aumentar o numero de nós de cache, mas isso seria financeiramente adequado ao projeto?;
- O banco de dados está publico apenas para se algum avaliador do teste querer fazer alguma query, em produção, limitaria o acesso do security group apenas para a vpc.
- Foi utilizado pathParam e não queryParam a fim de que a CDN cacheie o resultado
- Todos os testes são testes unitários com a exceção da pasta IT /(Assim nomeada seguindo o padrão do failsafe, o que dispensa maiores configurações) sendo a única suite a carregar todo o contexto do Spring e o servidor.

### Stack

* [Java 8](https://www.java.com/pt_BR/download/)
* [Lombok](https://projectlombok.org/)
* [Orika](https://orika-mapper.github.io/orika-docs/)
* [Spring Boot](https://projects.spring.io/spring-boot/)
* [Spring Security](https://projects.spring.io/spring-security/)
* [Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)
* [Undertow](http://undertow.io/)
* [PostgreSQL](https://www.postgresql.org/)
* [Redis](https://redis.io/)
* [H2 DB](http://www.h2database.com/html/main.html)
* [Junit](https://junit.org/junit5/)
* [Mockito](http://site.mockito.org)


----------------------------------------------------------------------------------------------------------------

Este é o teste usado por nós aqui da [Intelipost](http://www.intelipost.com.br) para avaliar tecnicamente os candidatos a nossas vagas de Backend. Se você estiver participando de um processo seletivo para nossa equipe, certamente em algum momento receberá este link, mas caso você tenha chego aqui "por acaso", sinta-se convidado a desenvolver nosso teste e enviar uma mensagem para nós nos e-mails `stefan.rehm@intelipost.com.br` e `gustavo.hideyuki@intelipost.com.br`.

Aqui na Intelipost nós aplicamos este mesmo teste para as vagas em todos os níveis, ou seja, um candidato a uma vaga de backend júnior fará o mesmo teste de um outro candidato a uma vaga de backend sênior, mudando obviamente o nosso critério de avaliação do resultado do teste. 

Nós fazemos isso esperando que as pessoas mais iniciantes entendam qual o modelo de profissional que temos por aqui e que buscamos para o nosso time. Portanto, se você estiver se candidatando a uma vaga mais iniciante, não se assuste, e faça o melhor que você puder!

## Instruções

Você deverá criar um `fork` deste projeto, e desenvolver em cima do seu fork. Use o *README* principal do seu repositório para nos contar como foi resolver seu teste, as decisões tomadas, como você organizou e separou seu código, e principalmente as instruções de como rodar seu projeto, afinal a primeira pessoa que irá rodar seu projeto será um programador frontend de nossa equipe, e se você conseguir explicar para ele como fazer isso, você já começou bem!

Lembre-se que este é um teste técnico e não um concurso público, portanto, não existe apenas uma resposta correta. Mostre que você é bom e nos impressione, mas não esqueça do objetivo do projeto. 

Nós não definimos um tempo limite para resolução deste teste, o que vale para nós e o resultado final e a evolução da criação do projeto até se atingir este resultado, mas acreditamos que este desafio pode ser resolvido em cerca de 16 horas de codificação.

## Um pouco sobre a Intelipost

A Intelipost é uma startup de tecnologia que está revolucionando a logística no Brasil, um mercado de R$ 300B por ano com muitas ineficiências e desafios. Temos um sistema inovador que gerencia a logística para empresas do e-commerce. Já recebemos R$11 milhões de investimento até o momento, e em pouquissimo tempo já estamos colhendo grandes resultados: Em 2016 fomos selecionados como uma empresa [Promessas Endeavor](https://ecommercenews.com.br/noticias/parcerias-comerciais/intelipost-e-selecionada-pelo-promessas-endeavor/), também [ganhamos a competição IBM Smartcamp](https://www.ibm.com/blogs/robertoa/2016/11/intelipost-e-nazar-vencem-o-ibm-smartcamp-brasil-2016/), com foco de Big Data e data analysis, o que nos rendeu a [realização de um Hackathon sobre Blockchain junto a IBM](https://www.ibm.com/blogs/robertoa/2017/09/intelipost-e-ibm-realizam-o-primeiro-hackathon-de-blockchain-em-startup-do-brasil/), e em 2017 [fomos selecionados pela Oracle para sermos acelerados por eles no programa Oracle Startup Cloud Accelerator](https://www.oracle.com/br/corporate/pressrelease/oracle-anuncia-startups-selecionadas-programa-oracle-startup-cloud-accelerator-sao-paulo-20170804.html).

Tecnicamente, o nosso maior desafio hoje é estar preparado para atender a todos os nossos clientes, que além de muitos, são grandes em número de requisições (Americanas, Submarino, Shoptime, Lojas Renner, Boticário, Livraria Cultura, Magazine Luize, etc), totalizando mais de meio bilhão de requisições por mês.

Para isso, organizamos nosso sistema em micro serviços na AWS com Docker e Kubernetes, utilizando Java 8, Spring 4 (principalmente spring-boot), PostgreSQL, ElasticSearch e Redis. Temos um frontend para acesso dos clientes desenvolvido Vue.JS e mobile apps utilizando o framework Ionic.

## O desafio

Como você pode ver, nosso maior desafio está na manutenção e otimização de aplicações que estejam prontas para atender um altíssimo volume de dados e transações, por este motivo, todos os membros da nossa equipe estão altamente comprometidos com estes objetivos, de robustez, escalabilidade e performance, e é exatamente isso que esperamos de você através da resolução destes dois desafios:

1) Imagine que hoje tenhamos um sistema de login e perfis de usuários. O sistema conta com mais de 10 milhões de usuários, sendo que temos um acesso concorrente de cerca de 5 mil usuários. Hoje a tela inicial do sistema se encontra muito lenta. Nessa tela é feita uma consulta no banco de dados para pegar as informações do usuário e exibi-las de forma personalizada. Quando há um pico de logins simultâneos, o carregamento desta tela fica demasiadamente lento. Na sua visão, como poderíamos iniciar a busca pelo problema, e que tipo de melhoria poderia ser feita?

2) Com base no problema anterior, gostaríamos que você codificasse um novo sistema de login para muitos usuários simultâneos e carregamento da tela inicial. Lembre-se que é um sistema web então teremos conteúdo estático e dinâmico. Leve em consideração também que na empresa existe um outro sistema que também requisitará os dados dos usuários, portanto, este sistema deve expor as informações para este outro sistema de alguma maneira.

### O que nós esperamos do seu teste

* O código deverá ser hospedado em algum repositório público. Diversos quesitos serão avaliados aqui, como organização do código, sequencialidade de commits, nomeação de classes e métodos, etc.
* O código deverá estar pronto para ser executado e testado, portanto, caso exista algum requisito, este deve estar completamente documentado no README do seu projeto.
* Esperamos também alguma explicação sobre a solução, que pode ser em comentários no código, um texto escrito ou até um vídeo narrativo explicando a abordagem utilizada. 
* Você deverá utilizar a nossa stack de tecnologias descritas no início deste documento (Java 8 + Spring boot + Spring MVC).

### O que nós ficaríamos felizes de ver em seu teste

* Testes
* Processo de build e deploy documentado
* Ver o código rodando live (em qualquer serviço por aí)

### O que nós não gostaríamos

* Descobrir que não foi você quem fez seu teste
* Ver commits grandes, sem muita explicação nas mensagens em seu repositório 

## O que avaliaremos de seu teste

* Histórico de commits do git
* As instruções de como rodar o projeto
* Organização, semântica, estrutura, legibilidade, manutenibilidade do seu código
* Alcance dos objetivos propostos
* Escalabilidade da solução adotada 
