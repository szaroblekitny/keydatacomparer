  CREATE TABLE CONFIGURACJA 
   ("CONF_ID" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"DATA_POCZ" DATE NOT NULL ENABLE, 
	"DATA_KONC" DATE, 
	"PAR_CHAR" VARCHAR2(20 BYTE), 
	"PAR_INT" INTEGER, 
	"KWOTA" NUMBER(25,2) DEFAULT 0, 
	"KURS_ULAMKOWY" NUMBER(8,7), 
	"KTO" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"KIEDY" TIMESTAMP (0) NOT NULL ENABLE, 
	 CONSTRAINT "CONFIGURACJA_PK" PRIMARY KEY ("CONF_ID")
  USING INDEX)
  TABLESPACE "USERS";
 
   COMMENT ON COLUMN "CONFIGURACJA"."CONF_ID" IS 'Identyfikator';
 
   COMMENT ON COLUMN "CONFIGURACJA"."DATA_POCZ" IS 'Data pocz�tkowa';
 
   COMMENT ON COLUMN "CONFIGURACJA"."DATA_KONC" IS 'Data ko�cowa - pusta bez ogranicze�';
 
   COMMENT ON COLUMN "CONFIGURACJA"."PAR_CHAR" IS 'Parametr znakowy';
 
   COMMENT ON COLUMN "CONFIGURACJA"."PAR_INT" IS 'Parametr liczby naturalnej';
 
   COMMENT ON COLUMN "CONFIGURACJA"."KWOTA" IS 'Kwota';
 
   COMMENT ON COLUMN "CONFIGURACJA"."KURS_ULAMKOWY" IS 'Liczba u�amkowa - np. kurs wal.';
 
   COMMENT ON COLUMN "CONFIGURACJA"."KTO" IS 'Kto wprowadzi�';
 
   COMMENT ON COLUMN "CONFIGURACJA"."KIEDY" IS 'Kiedy wprowadzono rekord';
 
   COMMENT ON TABLE "CONFIGURACJA"  IS 'Przyk�adowa tabelka konfiguracji';
 
create sequence CONFIGURACJA_SEQ;

CREATE OR REPLACE TRIGGER "CONFIGURACJA_TRG" BEFORE INSERT ON CONFIGURACJA 
FOR EACH ROW 
BEGIN
  <<COLUMN_SEQUENCES>>
  BEGIN
    IF :NEW.CONF_ID IS NULL THEN
      SELECT CONFIGURACJA_SEQ.NEXTVAL INTO :NEW.CONF_ID FROM DUAL;
    END IF;
  END COLUMN_SEQUENCES;
END;
/
ALTER TRIGGER "CONFIGURACJA_TRG" ENABLE
/
 