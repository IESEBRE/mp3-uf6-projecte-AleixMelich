package org.amelich.model.impls;

import org.amelich.controller.DBConnect;
import org.amelich.model.daos.DAO;
import org.amelich.model.entities.Alumne;
import org.amelich.model.exceptions.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumneDAOJDBCOracleImpl implements DAO<Alumne> {

    /**
     * Aquest mètode s'utilitza per recuperar un estudiant de la base de dades mitjançant el seu DNI.
     * Estableix una connexió a la base de dades, crea una instrucció i executa una consulta per seleccionar l'estudiant amb l'ID donat.
     * Si es troba un estudiant amb l'ID donat, es crea un nou objecte Alumne amb les dades de l'alumne i el retorna.
     * Si no es troba cap estudiant amb l'identificador donat, retorna null.
     * Si es produeix un error durant l'accés a la base de dades, genera una DAOException amb el codi d'error 1.
     * També garanteix que el conjunt de resultats, la declaració i la connexió es tanquin després de l'accés a la base de dades.
     *
     * @param id L'identificador de l'estudiant que s'ha de recuperar.
     * @return L'objecte Alumne de l'estudiant amb l'identificador donat, o nul si no es troba cap estudiant.
     * @throws DAOException Si es produeix un error durant l'accés a la base de dades.
     */
    @Override
    public Alumne get(Long id) throws DAOException {

        //Declaració de variables del mètode
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        Alumne estudiant = null;

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


    /**
     * Aquest mètode s'utilitza per recuperar tots els estudiants de la base de dades.
     * Estableix una connexió a la base de dades, crea un PreparedStatement i executa una consulta per seleccionar tots els estudiants.
     * A continuació, itera sobre el ResultSet i per a cada fila, crea un nou objecte Alumne amb les dades de l'estudiant i l'afegeix a la llista d'estudiants.
     * Si es produeix un error durant l'accés a la base de dades, genera una DAOException amb un codi d'error.
     * El codi d'error és 0 si l'error és un error de xarxa (codi d'error SQL 17002) i 1 en cas contrari.
     * També garanteix que el ResultSet, PreparedStatement i Connection es tanquin després de l'accés a la base de dades.
     *
     * @return Una llista d'objectes Alumne que representen tots els estudiants de la base de dades.
     * @throws DAOException Si es produeix un error durant l'accés a la base de dades.
     */
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


    /**
     * Aquest mètode s'utilitza per inserir un nou estudiant a la base de dades.
     * Estableix una connexió a la base de dades, crea un PreparedStatement i executa una instrucció insert.
     * La instrucció insert afegeix una nova fila a la taula ALUMNES amb el nom, la qualificació i l'estat de FCT de l'estudiant.
     * Els valors del nom, la qualificació i l'estat de FCT s'estableixen a partir de l'objecte Alumne passat com a paràmetre.
     * Si l'estat de FCT de l'estudiant és cert, estableix el valor de la columna FCT a 1, en cas contrari, el defineix a 0.
     * Si es produeix un error durant l'accés a la base de dades, genera una DAOException amb el codi d'error SQL.
     * També garanteix que PreparedStatement i Connection es tanquin després de l'accés a la base de dades.
     *
     * @param obj L'objecte Alumne que representa l'alumne a inserir.
     * @throws DAOException Si es produeix un error durant l'accés a la base de dades.
     */
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


    /**
     * Aquest mètode s'utilitza per actualitzar el registre d'un estudiant a la base de dades.
     * Estableix una connexió a la base de dades, crea un PreparedStatement i executa una instrucció d'actualització.
     * La instrucció d'actualització modifica la fila de la taula ALUMNES amb l'ID donat, establint el nom, la qualificació i l'estat de l'FCT als valors de l'objecte Alumne.
     * Si l'estat de FCT de l'estudiant és cert, estableix el valor de la columna FCT a 1, en cas contrari, el defineix a 0.
     * Si es produeix un error durant l'accés a la base de dades, genera una DAOException amb el codi d'error SQL.
     * També garanteix que PreparedStatement i Connection es tanquin després de l'accés a la base de dades.
     *
     * @param obj L'objecte Alumne que representa l'alumne el registre del qual s'ha d'actualitzar. L'ID de l'objecte Alumne s'utilitza per identificar la fila que s'ha d'actualitzar.
     * @throws DAOException Si es produeix un error durant l'accés a la base de dades.
     */
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


    /**
     * Aquest mètode s'utilitza per eliminar un estudiant de la base de dades.
     * Estableix una connexió a la base de dades, crea un PreparedStatement i executa una instrucció de supressió.
     * La instrucció de supressió elimina la fila de la taula ALUMNES amb l'ID donat.
     * Si es produeix un error durant l'accés a la base de dades, genera una DAOException amb el codi d'error SQL.
     * També garanteix que PreparedStatement i Connection es tanquin després de l'accés a la base de dades.
     *
     * @param id L'identificador de l'estudiant que s'ha d'eliminar.
     * @throws DAOException Si es produeix un error durant l'accés a la base de dades.
     */
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


    /**
     * Aquest mètode s'utilitza per obtenir l'identificador d'un estudiant a la base de dades.
     * Estableix una connexió a la base de dades, crea un PreparedStatement i executa una consulta per seleccionar l'ID de l'estudiant amb el nom, la qualificació i l'estat de FCT donats.
     * Si es troba un estudiant amb el nom, la qualificació i l'estat de FCT donats, es retorna l'ID de l'estudiant.
     * Si no es troba cap estudiant amb el nom, la qualificació i l'estat de FCT donats, retorna null.
     * Si es produeix un error durant l'accés a la base de dades, genera una DAOException amb el codi d'error SQL.
     * També garanteix que el ResultSet, PreparedStatement i Connection es tanquin després de l'accés a la base de dades.
     *
     * @param obj L'objecte Alumne que representa l'estudiant del qual s'ha d'obtenir l'identificador.
     * @return L'identificador de l'estudiant amb el nom, la qualificació i l'estat de FCT donats, o nul si no es troba cap estudiant.
     * @throws DAOException Si es produeix un error durant l'accés a la base de dades.
     */
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


    /**
     * Aquest mètode s'utilitza per eliminar tots els estudiants de la base de dades.
     * Estableix una connexió a la base de dades, crea una instrucció i executa una instrucció de supressió per eliminar totes les files de la taula ALUMNES.
     * Si es produeix un error durant l'accés a la base de dades, genera una DAOException amb el codi d'error SQL.
     * També garanteix que la connexió es tanqui després de l'accés a la base de dades.
     *
     * @throws DAOException Si es produeix un error durant l'accés a la base de dades.
     */
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
