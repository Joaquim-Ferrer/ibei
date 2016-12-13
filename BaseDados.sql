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
	titulo 		VARCHAR(30) NOT NULL,
	descricao 	VARCHAR(100) NOT NULL
		CONSTRAINT descMinLen CHECK(LENGTH(descricao) > 10),
	preco_maximo NUMBER(6,2) NOT NULL,
	deadline 	DATE NOT NULL,
	FOREIGN KEY(username) REFERENCES utilizador(username)
)

CREATE SEQUENCE LEILAO_ID
START WITH 1
INCREMENT BY 1
MINVALUE 1;


CREATE TABLE licitacao(
	id_leilao NUMBER(10,2) NOT NULL,
	username VARCHAR(20) NOT NULL,
	montante NUMBER(6,2) NOT NULL,
	data DATE NOT NULL,
	CONSTRAINT pk_licitacao PRIMARY KEY (id_leilao, username, montante),
	FOREIGN KEY (username) REFERENCES utilizador(username),
	FOREIGN KEY (id_leilao) REFERENCES leilao(id_leilao)
);

CREATE OR REPLACE TRIGGER custo_licitacao_constraint
	BEFORE INSERT ON licitacao
	FOR EACH ROW
DECLARE
	highest_bid NUMBER;
BEGIN
	SELECT MIN(montante)
	INTO highest_bid
	FROM licitacao
	WHERE username = :NEW.username
		AND id_leilao = :NEW.id_leilao;
	IF highest_bid < :NEW.montante
	THEN
		RAISE_APPLICATION_ERROR(-20000, 'CANT BID HIGHER THAN LOWEST BID');
	END IF
END custo_licitacao_constraint;
/

CREATE TABLE mensagens(
	username 	VARCHAR2(30) NOT NULL,
	id_leilao 	NUMBER(6) NOT NULL,
	mensagem 	VARCHAR2(100) NOT NULL,
	FOREIGN KEY(username) REFERENCES utilizador(username),
	FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao),
	CONSTRAINT pk_idLeilao PRIMARY KEY (id_leilao)
);

create sequence notif_id start with 1 increment by 1 minvalue 1 maxvalue 100000;

CREATE TABLE notificacao(
	id_notif	NUMBER(6) NOT NULL,
	id_leilao 	NUMBER(6) NOT NULL,
	username 	VARCHAR2(30) NOT NULL,
	data 		DATE NOT NULL,
	estado		VARCHAR2(30) NOT NULL,
	FOREIGN KEY(username) REFERENCES utilizador(username),
	FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao),
	CONSTRAINT pk_idNotif PRIMARY KEY (id_notif)
);

CREATE TABLE notif_leilao(
	id_notif	NUMBER(6) NOT NULL,
	valor		NUMBER(6,2) NOT NULL,
	FOREIGN KEY(id_notif) REFERENCES notificacao(id_notif)
);

CREATE TABLE notif_msg(
	remetente	VARCHAR2(30) NOT NULL,
	id_notif	NUMBER(6) NOT NULL,
	texto	VARCHAR2(30) NOT NULL,
	FOREIGN KEY(id_notif) REFERENCES notificacao(id_notif),
	FOREIGN KEY(remetente) REFERENCES utilizador(username)
);

