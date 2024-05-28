package org.amelich.model.daos;


import org.amelich.model.exceptions.DAOException;

import java.util.List;

/**
 * Aquesta és una interfície genèrica DAO (Data Access Object).
 * Defineix les operacions estàndard que s'han de realitzar en un objecte(s) model.
 *
 * @param <T> el tipus de l'objecte model
 */
public interface DAO <T>{

    T get(Long id) throws DAOException;

    List<T> getAll() throws DAOException;

    void insert(T obj) throws DAOException;

    void update(T obj) throws DAOException;

    void delete(Long id) throws DAOException;

}
