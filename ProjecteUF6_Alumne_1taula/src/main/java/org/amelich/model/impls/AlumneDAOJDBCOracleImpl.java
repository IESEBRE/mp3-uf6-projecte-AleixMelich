package org.amelich.model.impls;

import org.amelich.controller.DBConnect;
import org.amelich.model.daos.DAO;
import org.amelich.model.entities.Alumne;
import org.amelich.model.exceptions.DAOException;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumneDAOJDBCOracleImpl implements DAO<Alumne> {


    @Override
    public Alumne get(Long id) throws DAOException {

        //Declaració de variables del mètode
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        Alumne estudiant = null;

        //Accés a la BD usant l'API JDBC
        try {

            con = DBConnect.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM ALUMNES WHERE id=?");
            if (rs.next()) {
                estudiant = new Alumne(rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getDouble("nota"),
                        rs.getBoolean("fct"));
            }
        } catch (SQLException throwables) {
            throw new DAOException(1);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                throw new DAOException(1);
            }

        }
        return estudiant;
    }

    @Override
    public List<Alumne> getAll() throws DAOException {
        //Declaració de variables del mètode
        List<Alumne> estudiants = new ArrayList<>();

        //Accés a la BD usant l'API JDBC
        try (Connection con = DBConnect.getConnection();
             PreparedStatement st = con.prepareStatement("SELECT * FROM ALUMNES");
             ResultSet rs = st.executeQuery();
        ) {

            while (rs.next()) {
                estudiants.add(new Alumne(rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getDouble("nota"),
                        rs.getBoolean("fct")));
            }
        } catch (SQLException throwables) {
            int tipoError = throwables.getErrorCode();
            switch(throwables.getErrorCode()){
                case 17002: //l'he obtingut posant un sout en el throwables.getErrorCode()
                    tipoError = 0;
                    break;
                default:
                    tipoError = 1;  //error desconegut
            }
            throw new DAOException(tipoError);
        }
        return estudiants;
    }

    //METODE D'AFEGIR UN NOU ALUMNE A LA BASE DE DADES
    @Override
    public void insert(Alumne obj) throws DAOException {
        String insertSQL = "INSERT INTO ALUMNES (nom, nota, fct) VALUES (?,?,?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement st = con.prepareStatement(insertSQL);
        )
        {
            st.setString(1, obj.getNomCognom());
            st.setDouble(2, obj.getNota());
            st.setInt(3, obj.isFct() ? 1 : 0);
            st.executeUpdate();
        } catch (SQLException throwables) {
            int errorCode = throwables.getErrorCode();
            throw new DAOException(errorCode);
        }
    }

    //METODE PER MODIFICAR LES DADES D'UN ALUMNE A LA BASE DE DADES
    @Override
    public void update(Alumne obj) throws DAOException {
        String updateSQL = "UPDATE ALUMNES SET nom=?, nota=?, fct=? WHERE id=?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement st = con.prepareStatement(updateSQL);
        ) {
            st.setString(1, obj.getNomCognom());
            st.setDouble(2, obj.getNota());
            st.setInt(3, obj.isFct() ? 1 : 0);
            st.setLong(4, obj.getId());
            st.executeUpdate();
        } catch (SQLException throwables) {
            int errorCode = throwables.getErrorCode();
            throw new DAOException(errorCode);
        }
    }

    //METODE PER ELIMINAR UN ALUMNE DE LA BASE DE DADES
    @Override
    public void delete(Long id) throws DAOException {
        String deleteSQL = "DELETE FROM ALUMNES WHERE id=?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement st = con.prepareStatement(deleteSQL);
        ) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException throwables) {
            int errorCode = throwables.getErrorCode();
            throw new DAOException(errorCode);
        }
    }

    //METODE PER TREURE LA ID DE L'ALUMNE SELECCIONAT
    public Long alumneID(Alumne obj) throws DAOException {
        Long id = null;
        String selectSQL = "SELECT id FROM ALUMNES WHERE nom=? AND nota=? AND fct=?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement st = con.prepareStatement(selectSQL);
        ) {
            st.setString(1, obj.getNomCognom());
            st.setDouble(2, obj.getNota());
            st.setInt(3, obj.isFct() ? 1 : 0);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id");
            }
        } catch (SQLException throwables) {
            int errorCode = throwables.getErrorCode();
            throw new DAOException(errorCode);
        }
        return id;
    }

    //METODE PER ELIMINAR TOTES FILES DE LA TAULA ALUMNES
    public void deleteAll() throws DAOException {
        String deleteSQL = "DELETE FROM ALUMNES";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement st = con.prepareStatement(deleteSQL);
        ) {
            st.executeUpdate();
        } catch (SQLException throwables) {
            int errorCode = throwables.getErrorCode();
            throw new DAOException(errorCode);
        }
    }
}
