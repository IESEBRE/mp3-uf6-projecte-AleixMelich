package org.amelich.model.daos;


import org.amelich.model.exceptions.DAOException;

import java.util.List;

public interface DAO <T>{

    T get(Long id) throws DAOException;

    List<T> getAll() throws DAOException;

    void insert(T obj) throws DAOException;

    //CODI PER MODIFICAR LES DADES D'UN ALUMNE A LA BASE DE DADES
    void update(T obj) throws DAOException;

    //CODI PER ELIMINAR UN ALUMNE DE LA BASE DE DADES
    void delete(Long id) throws DAOException;

    //Tots els mètodes necessaris per interactuar en la BD

}
