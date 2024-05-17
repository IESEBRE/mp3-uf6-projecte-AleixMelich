package org.amelich.model.impls;

import org.amelich.model.daos.DAO;
import org.amelich.model.entities.Alumne;
import org.amelich.model.exceptions.DAOException;

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

            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//localhost:1521/xe",
                    "C##HR",
                    "HR"
            );
//            st = con.prepareStatement("SELECT * FROM estudiant WHERE id=?;");
            st = con.createStatement();
//            st = con.prepareStatement("SELECT * FROM estudiant WHERE id=?;");
//            st.setLong(1, id);
            rs = st.executeQuery("SELECT * FROM ALUMNES2");
//            estudiant = new Alumne(rs.getLong(1), rs.getString(2));
//            st.close();
            if (rs.next()) {
                estudiant = new Alumne(rs.getLong("id"), rs.getString("nom"),rs.getDouble("nota"), rs.getBoolean("fct"));
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
        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st = con.prepareStatement("SELECT * FROM ALUMNES2");
             ResultSet rs = st.executeQuery();
        ) {

            while (rs.next()) {
                estudiants.add(new Alumne(rs.getLong("id"), rs.getString("nom"),rs.getDouble("nota"), rs.getBoolean("fct")));
            }
        } catch (SQLException throwables) {
            int tipoError = throwables.getErrorCode();
            //System.out.println(tipoError+" "+throwables.getMessage());
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

    @Override
    public void save(Alumne obj) throws DAOException {
        String insertSQL = "INSERT INTO ALUMNES2 (id, nom, nota, fct) VALUES (?,?,?,?)";
        try (Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/xe",
                "C##HR",
                "HR"
        );
             PreparedStatement st = con.prepareStatement(insertSQL);
        ) {
            st.setLong(1, obj.getId());
            st.setString(2, obj.getNomCognom());
            st.setDouble(3, obj.getNota());
            st.setInt(4, obj.isFct() ? 1 : 0);
            st.executeUpdate();
        } catch (SQLException throwables) {
            //throw new DAOException(1);
            System.out.println(throwables.getMessage());
        }
    }
}
