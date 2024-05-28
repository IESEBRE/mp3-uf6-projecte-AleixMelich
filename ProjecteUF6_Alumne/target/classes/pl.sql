-- CREACIÓ DE LA TAULA ALUMNES
CREATE TABLE ALUMNES(
    ID NUMBER PRIMARY KEY,
    NOM VARCHAR2(50) NOT NULL,
    NOTA NUMBER NOT NULL,
    FCT NUMBER(1,0) DEFAULT 0
);

-- CREACIÓ DEL TRIGGER ID_AUTO PER A LA TAULA ALUMNES
CREATE TRIGGER ID_AUTO
    before insert
    on ALUMNES
    for each row
DECLARE
    ultima_id NUMBER;
BEGIN
    SELECT NVL(MAX(ID), 0) INTO ultima_id
    FROM ALUMNES;
    :NEW.ID:=ultima_id+1;
END;