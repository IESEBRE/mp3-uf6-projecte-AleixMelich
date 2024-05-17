-- Poseu el codi dels procediments/funcions emmagatzemats, triggers, ..., usats al projecte
CREATE OR REPLACE TRIGGER check_boolean
BEFORE INSERT OR UPDATE ON ALUMNES
FOR EACH ROW
BEGIN
  IF :NEW.fct NOT IN (0, 1) THEN
    RAISE_APPLICATION_ERROR(-20001, 'El valor de fct ha de ser 0 o 1');
  END IF;
END;

--
--