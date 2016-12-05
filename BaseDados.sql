CREATE TABLE utilizador(
	username 	VARCHAR2(30) NOT NULL 
		CONSTRAINT userMinLen CHECK(LENGTH(username) > 5)
		CONSTRAINT userUnique PRIMARY KEY,
	password 	VARCHAR2(30) NOT NULL
		CONSTRAINT passMinLen CHECK(LENGTH(password) > 5)
);

CREATE TABLE leilao(
	username 	VARCHAR(20) NOT NULL,
	id_leilao 	NUMBER(10,2) NOT NULL
		CONSTRAINT pk_idLeilao PRIMARY KEY,
	cod_artigo 	NUMBER(13) NOT NULL
		CONSTRAINT notValidCod CHECK(LENGTH(TO_CHAR(cod_artigo)) = 13),
	titulo 		VARCHAR2(30) NOT NULL,
	descricao 	VARCHAR2(100) NOT NULL
		CONSTRAINT descMinLen CHECK(LENGTH(descricao) > 10),
	preco_maximo NUMBER(6,2) NOT NULL,
	deadline 	DATE NOT NULL,
	FOREIGN KEY(username) REFERENCES utilizador(username)
)

CREATE SEQUENCE LEILAO_ID
START WITH 1
INCREMENT BY 1
MINVALUE 1;

CREATE TABLE mensagens(
	username 	VARCHAR2(30) NOT NULL,
	id_leilao 	NUMBER(6) NOT NULL,
	mensagem 	VARCHAR2(100) NOT NULL,
	FOREIGN KEY(username) REFERENCES utilizador(username),
	FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao),
	CONSTRAINT pk_idLeilao PRIMARY KEY (id_leilao)
);

CREATE TABLE licitacoes(
	id_leilao 	NUMBER(6) NOT NULL, 
	username 	VARCHAR2(30) NOT NULL,
	data 		DATE NOT NULL,
	preco 		NUMBER(4) NOT NULL,
	FOREIGN KEY(username) REFERENCES utilizador(username),
	FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao),
	CONSTRAINT pk_idLeilao PRIMARY KEY (id_leilao)
);

CREATE TABLE notificacoes(
	id_leilao 	NUMBER(6) NOT NULL,
	username 	VARCHAR2(30) NOT NULL,
	data 		DATE NOT NULL,
	cod_artigo 	NUMBER(13) NOT NULL,
	FOREIGN KEY (cod_artigo) REFERENCES leilao(cod_artigo),
	FOREIGN KEY(username) REFERENCES utilizador(username),
	FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao),
	CONSTRAINT pk_idLeilao PRIMARY KEY (id_leilao)
);