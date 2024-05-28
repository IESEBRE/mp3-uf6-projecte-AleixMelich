package org.amelich.model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class DAOException extends Exception{

    private static final Map<Integer, String> missatges = new HashMap<>();
    //num i retorna string, el map
    static {
        missatges.put(0, "Connexó no establerta amb la BD, revisa-ho.");
        missatges.put(1, "Error amb la taula, revisa-ho.");

        missatges.put(5, "Falta omplir alguna dada, revisa-ho.");

        // 10-19 -- ERRORS CAMP NOTA
        missatges.put(10, "Has d'introduir una nota correcta (0-10) i si te decimals separar-la per comes.");

        // 20-29 -- ERRORS CAMP NOM
        missatges.put(22, "No pots introduir cap numero en aquest camp, nomes caracters.");
        missatges.put(23, "Aquest nom ja esta inscrit a la taula, canvial.");

        // NOMES JOPTIONPANE
        missatges.put(100, "Per modificar o borrar una fila l'has de seleccionar a la taula");
        missatges.put(101, "Per llimpiar la taula han d'haber files a la taula");
        missatges.put(102, "Les dades de la BD no s'han carregat correctament");

        missatges.put(904, "Nom de columna no vàlid");
        missatges.put(936, "Falta expressió en l'ordre SQL");
        missatges.put(942, "La taula o la vista no existeix");
        missatges.put(1000, "S'ha superat el nombre màxim de cursors oberts");
        missatges.put(1400, "Inserció de valor nul en una columna que no permet nuls");
        missatges.put(1403, "No s'ha trobat cap dada");
        missatges.put(1722, "Ha fallat la conversió d'una cadena de caràcters a un número");
        missatges.put(1747, "El nombre de columnes de la vista no coincideix amb el nombre de columnes de les taules subjacents");
        missatges.put(4091, "Modificació d'un procediment o funció en execució actualment");
        missatges.put(6502, "Error numèric o de valor durant l'execució del programa");
        missatges.put(12154, "No s'ha pogut resoldre el nom del servei de la base de dades Oracle o l'identificador de connexió");
        missatges.put(2292, "S'ha violat la restricció d'integritat -  s'ha trobat un registre fill");
    }

    //atribut
    private int tipo;

    //constructor al q pasem tipo
    public DAOException(int tipo){
        this.tipo=tipo;
    }

    //sobreescrivim el get message
    @Override
    public String getMessage(){
        return missatges.get(this.tipo); //el missatge del tipo
    }

    public int getTipo() {
        return tipo;
    }
}
