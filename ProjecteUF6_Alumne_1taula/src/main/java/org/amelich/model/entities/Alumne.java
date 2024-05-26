package org.amelich.model.entities;

import java.io.Serializable;


/**
 * @author Aleix Melich
 * <p>
 * Aquesta classe és un POJO que representa un alumne.
 */
public class Alumne {


    private Long id;
    private String nomCognom;
    private double nota;
    private boolean fct;

    public Alumne() {
    }

    /**
     * @param nomCognom
     * @param nota
     * @param fct
     */
    public Alumne(long id, String nomCognom, double nota, boolean fct) {
        this.id = id;
        this.nomCognom = nomCognom;
        this.nota = nota;
        this.fct = fct;
    }

    public Alumne(long id, String nomCognom) {
        this.id = id;
        this.nomCognom = nomCognom;
    }

    public Alumne(long id, String nomCognom, double nota) {
        this.id = id;
        this.nomCognom = nomCognom;
        this.nota = nota;
    }

    public Alumne(String nomCognom, Double nota, boolean fct) {
        this.nomCognom = nomCognom;
        this.nota = nota;
        this.fct = fct;
    }

    public Alumne(Long id, String nomCognom, String nota, boolean fct) {
        this.id = id;
        this.nomCognom = nomCognom;
        this.nota = Double.parseDouble(nota);
        this.fct = fct;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNomCognom() {
        return nomCognom;
    }
    public void setNomCognom(String nomCognom) {
        this.nomCognom = nomCognom;
    }

    public double getNota() {
        return nota;
    }
    public void setNota(double nota) {
        this.nota = nota;
    }

    public boolean isFct() {
        return fct;
    }
    public void setFct(boolean fct) {
        this.fct = fct;
    }

}