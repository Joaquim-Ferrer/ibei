CREATE TABLE utilizador(
  username    VARCHAR2(30) NOT NULL
    CONSTRAINT userMinLen CHECK(LENGTH(username)>5)
    CONSTRAINT userUnique PRIMARY KEY,
  password    VARCHAR(30) NOT NULL
    CONSTRAINT passMinLen CHECK(LENGTH(password)>5),
  lastOnline  DATE DEFAULT SYSDATE NOT NULL
);

CREATE TABLE leilao(
  username  VARCHAR2(30) NOT NULL,
  id_leilao NUMBER(6) NOT NULL
    CONSTRAINT pk_idLeilao PRIMARY KEY,
  cod_artigo  NUMBER(13) NOT NULL
    CONSTRAINT notValidCod CHECK(LENGTH(TO_CHAR(cod_artigo)) = 13),
  titulo  VARCHAR(20) NOT NULL,
  descricao VARCHAR(100) NOT NULL
    CONSTRAINT descMinLen CHECK(LENGTH(descricao) > 10),
  preco_maximo FLOAT NOT NULL,
  deadline  DATE NOT NULL,
  estado_resolvido  NUMBER(1,0) DEFAULT 0 NOT NULL,
  FOREIGN KEY(username) REFERENCES utilizador(username)
);

CREATE TABLE historial_leilao(
  id_leilao NUMBER(6) NOT NULL,
  data  DATE NOT NULL,
  titulo  VARCHAR(20) NOT NULL,
  descricao VARCHAR(100) NOT NULL,
  FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao)
);

CREATE SEQUENCE LEILAO_ID
START WITH 1
INCREMENT BY 1;

CREATE TABLE notificacao(
  id_notif  NUMBER(7) NOT NULL,
  id_leilao NUMBER(6) NOT NULL,
  username VARCHAR2(30) NOT NULL,
  data_criacao  DATE DEFAULT SYSDATE NOT NULL,
  estado_visto  NUMBER(1,0) NOT NULL,
  FOREIGN KEY(username) REFERENCES utilizador(username),
  FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao),
  CONSTRAINT pk_idNotif PRIMARY KEY(id_notif)
);

CREATE TABLE notif_leilao(
  id_notif NUMBER(7) NOT NULL,
  valor FLOAT NOT NULL,
  FOREIGN KEY(id_notif) REFERENCES notificacao(id_notif),
  CONSTRAINT pk_notif_leilao PRIMARY KEY(id_notif)
);

CREATE TABLE notif_msg(
  remetente VARCHAR2(30) NOT NULL,
  id_notif NUMBER(7) NOT NULL,
  texto VARCHAR2(100) NOT NULL,
  FOREIGN KEY(id_notif) REFERENCES notificacao(id_notif),
  FOREIGN KEY(remetente) REFERENCES utilizador(username),
  CONSTRAINT pk_notif_msg PRIMARY KEY(id_notif)
);

CREATE SEQUENCE NOTIF_ID
START WITH 1
INCREMENT BY 1;

CREATE TABLE licitacao(
  id_leilao NUMBER(6) NOT NULL,
  username  VARCHAR2(30) NOT NULL,
  montante   FLOAT NOT NULL
  CONSTRAINT montante_positivo CHECK(montante>0),
  data  DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT pk_licitacao PRIMARY KEY (id_leilao, montante),
  FOREIGN KEY (username) REFERENCES utilizador(username),
  FOREIGN KEY (id_leilao) REFERENCES leilao(id_leilao)
);  

CREATE TABLE mensagem(
  username  VARCHAR2(30) NOT NULL,
  id_leilao NUMBER(6),
  mensagem  VARCHAR2(100) NOT NULL,
  FOREIGN KEY(username) REFERENCES utilizador(username),
  FOREIGN KEY(id_leilao) REFERENCES leilao(id_leilao)
);

CREATE OR REPLACE TRIGGER custo_licitacao_constraint
  BEFORE INSERT ON licitacao
  FOR EACH ROW
DECLARE
  min_bid FLOAT;
BEGIN
  SELECT NVL(min(montante), leilao.preco_maximo)
  INTO min_bid
  FROM licitacao, leilao
  WHERE  leilao.id_leilao = :NEW.id_leilao AND
                  leilao.id_leilao = licitacao.id_leilao(+)
  GROUP BY preco_maximo;
  IF  :NEW.montante > min_bid
  THEN
      RAISE_APPLICATION_ERROR(-20000, 'CANT BID HIGHER THAN LAST BID');
  END IF;
END custo_licitacao_constraint;
/

CREATE OR REPLACE TRIGGER old_auction_constraint
  BEFORE INSERT ON licitacao
  FOR EACH ROW
DECLARE
  data_limite DATE;
BEGIN
  SELECT deadline
  INTO data_limite
  FROM leilao
  WHERE :NEW.id_leilao = leilao.id_leilao;
  IF data_limite < :NEW.data THEN
    RAISE_APPLICATION_ERROR(-20000, 'CANT BID ON OLD AUCTION');
END IF;
END old_auction_constraint;
/
