package org.amelich.controller;

import org.amelich.model.entities.Alumne;
import org.amelich.model.exceptions.DAOException;
import org.amelich.view.ModelComponentsVisuals;
import org.amelich.model.impls.AlumneDAOJDBCOracleImpl;
import org.amelich.view.ViewPestanya;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class Controller implements PropertyChangeListener { //1. Implementació de interfície PropertyChangeListener


    private ModelComponentsVisuals modelComponentsVisuals =new ModelComponentsVisuals();
    private AlumneDAOJDBCOracleImpl dadesAlumnes;
    private ViewPestanya view;

    public Controller(AlumneDAOJDBCOracleImpl dadesAlumnes, ViewPestanya view) {
        this.dadesAlumnes = dadesAlumnes;
        this.view = view;

        //5. Necessari per a que Controller reaccione davant de canvis a les propietats lligades
        canvis.addPropertyChangeListener(this);

        lligaVistaModel();

        afegirListeners();

        //Si no hem tingut cap poroblema amb la BD, mostrem la finestra
        view.setVisible(true);


    }

    private void lligaVistaModel() {

        //Carreguem la taula d'alumnes en les dades de la BD
        try {
            setModelTaulaAlumne(modelComponentsVisuals.getModel(),dadesAlumnes.getAll());
        } catch (DAOException e) {
            this.setExcepcio(e);
        }

        //Fixem el model de la taula dels alumnes
        JTable taula = view.getTaula();
        taula.setModel(this.modelComponentsVisuals.getModel());
        //Amago la columna que conté l'objecte alumne
        taula.getColumnModel().getColumn(4).setMinWidth(0);
        taula.getColumnModel().getColumn(4).setMaxWidth(0);
        taula.getColumnModel().getColumn(4).setPreferredWidth(0);

    }
    private void setModelTaulaAlumne(DefaultTableModel modelTaulaAlumne, List<Alumne> all) {

        // Fill the table model with data from the collection
        for (Alumne estudiant : all) {
            modelTaulaAlumne.addRow(new Object[]{estudiant.getId(), estudiant.getNomCognom(), estudiant.getNota(), estudiant.isFct(), estudiant});
        }
    }

    private void afegirListeners() {

        ModelComponentsVisuals modelo = this.modelComponentsVisuals;

        DefaultTableModel model = modelo.getModel();

        // BOTONS
        JButton insertarButton = view.getInsertarButton();
        JButton modificarButton = view.getModificarButton();
        JButton borrarButton = view.getBorrarButton();
        JButton exportarButton = view.getExportarButton();

        // ALUMNES 2023/2024
        JTable taula = view.getTaula();
        JTextField campNom = view.getCampNom();
        JTextField campNota = view.getCampNota();
        JCheckBox SI_CheckBox = view.getSI_CheckBox();

        // CONFIGURACIONS DE LA TAULA
        configureTable(taula);


        // CODI DEL CLIC AL BOTO INSERTAR
        insertarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de INSERTAR");
                    try {
                        double nota = validarDades(campNom.getText(), campNota.getText(), model);
                        Alumne al = new Alumne(campNom.getText(), nota, SI_CheckBox.isSelected());
                        model.addRow(new Object[]{campNom.getText(), nota, SI_CheckBox.isSelected(), al});
                        dadesAlumnes.insert(al);

                        JOptionPane.showMessageDialog(null, "Has inscrit un nou alumne", "Inscripció correcta", JOptionPane.INFORMATION_MESSAGE);
                        llimpiarCampsAlumnes();

                    } catch (DAOException ex) {
                        setExcepcio(ex);
                    }
                }
        });


        // SELECCIÓ DELS ELEMENTS DE LA TAULA
        taula.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e the event to be processed
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int filaSel = taula.getSelectedRow();

                if (filaSel != -1) {
                    emplenarCamps(filaSel);
                } else {
                    llimpiarCampsAlumnes();
                }
            }

            /**
             * @return Omple els camps amb les dades de la fila seleccionada.
             */
            private void emplenarCamps(int fila) {
                campNom.setText(model.getValueAt(fila, 1).toString());
                campNota.setText(model.getValueAt(fila, 2).toString().replaceAll("\\.", ","));
                SI_CheckBox.setSelected((Boolean) model.getValueAt(fila, 3));
            }
        });


        // CODI DEL BOTO BORRAR
        borrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de BORRAR");

                int filaSel = taula.getSelectedRow();
                if (filaSel != -1) {
                    Long idEstudiant = (Long) model.getValueAt(filaSel, 0);
                    try {
                        dadesAlumnes.delete(idEstudiant);
                    } catch (DAOException daoException) {
                        JOptionPane.showMessageDialog(null, "Error al esborrar l'estudiant de la base de dades", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    model.removeRow(filaSel);
                    llimpiarCampsAlumnes();
                } else setExcepcio(new DAOException(100));
            }
        });


        // CODI DEL BOTO MODIFICAR
        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha MODIFICAT un insert");
                int filaSel = taula.getSelectedRow();
                if(filaSel != -1){
                    try {
                        double nota = validarDades(campNom.getText(), campNota.getText(), model);
                        Alumne al = new Alumne(campNom.getText(), nota, SI_CheckBox.isSelected());
                        model.addRow(new Object[]{campNom.getText(), nota, SI_CheckBox.isSelected(), al});
                        dadesAlumnes.update(al);
                        model.removeRow(filaSel);
                        JOptionPane.showMessageDialog(null, "Has modificat l'alumne", "Modificació correcta", JOptionPane.INFORMATION_MESSAGE);
                        llimpiarCampsAlumnes();
                    } catch (DAOException ex) {
                        setExcepcio(ex);
                    }
                } else setExcepcio(new DAOException(100));
            }
        });

// TRACTAR EXCEPCIONS, TRY/CATCH/FINALLY
        // EN AQUEST CAS DETERMINEM QUE S'HA D'INTRODUIR UNA NOTA VALIDA (DEL 0 AL 10)
        campNota.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             * @return Controla que la nota sigui un número vàlid.
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);

                if (!campNota.getText().matches("[0-9]+([.,][0-9]+)?")) {
                    setExcepcio(new DAOException(10));
                }

                try {
                    NumberFormat num=NumberFormat.getNumberInstance(Locale.getDefault());
                    double nota= num.parse(campNota.getText().trim()).doubleValue();
                    if (nota<0 || nota>10) {
                        setExcepcio(new DAOException(10));
                    }
                } catch (ParseException ignored) {}
            }
        });

        // EN AQUEST CAS DETERMINEM QUE S'HA D'INTRODUIR UNA NOM VALID ON NOMES PERMETRÀ CARACTERS
        campNom.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             * @return Controla que el nom sigui un text vàlid.
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);

                String nom=(campNom.getText());
                if (nom.matches(".*\\d.*")){
                    setExcepcio(new DAOException(22));
                }
            }
        });

    }




    //METODE DE CONFIGURACIÓ DE LA TAULA
    private void configureTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD,13));
        table.setRowHeight(23);
        table.getTableHeader().setBackground(new Color(32, 136, 203));
        table.getTableHeader().setForeground(new Color(255, 255, 255));
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setDefaultEditor(Object.class, null);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    private double validarDades(String nom, String notaText, DefaultTableModel model) throws DAOException {
        // Validar que el nom i la nota no estiguin buits
        if (nom.isBlank() || notaText.isBlank()) {
            throw new DAOException(5);
        }

        // Validar que el nom no contingui números
        if (nom.matches(".*\\d.*")) {
            throw new DAOException(22);
        }

        // Validar que el nom no estigui repetit
        for (int i = 0; i < model.getRowCount(); i++) {
            if (nom.equals(model.getValueAt(i, 1))) {
                throw new DAOException(23);
            }
        }

        // Validar que la nota sigui un número vàlid
        double nota;
        try {
            NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());
            nota = num.parse(notaText.trim()).doubleValue();
            if (nota < 0 || nota > 10) {
                throw new DAOException(10);
            }
        } catch (ParseException ignored) {
            throw new DAOException(10);
        }
        return nota;
    }


    private void llimpiarCampsAlumnes() {
        view.getCampNom().setText("");
        view.getCampNota().setText("");
        view.getSI_CheckBox().setSelected(false);
        view.getCampNom().requestFocus();
    }


    //TRACTAMENT D'EXCEPCIONS

    //2. Propietat lligada per controlar quan genero una excepció
    public static final String PROP_EXCEPCIO="excepcio";
    private DAOException excepcio;

    public DAOException getExcepcio() {
        return excepcio;
    }

    public void setExcepcio(DAOException excepcio) {
        DAOException valorVell=this.excepcio;
        this.excepcio = excepcio;
        canvis.firePropertyChange(PROP_EXCEPCIO, valorVell,excepcio);
    }


    //3. Propietat PropertyChangesupport necessària per poder controlar les propietats lligades
    PropertyChangeSupport canvis=new PropertyChangeSupport(this);


    //4. Mètode on posarem el codi de tractament de les excepcions --> generat per la interfície PropertyChangeListener
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DAOException rebuda=(DAOException)evt.getNewValue();

        try {
            throw rebuda;
        } catch (DAOException e) {
            //Aquí farem ele tractament de les excepcions de l'aplicació
            switch(evt.getPropertyName()){
                case PROP_EXCEPCIO:

                switch(rebuda.getTipo()){
                    case 0:
                        JOptionPane.showMessageDialog(null, rebuda.getMessage());
                        System.exit(1);
                        break;
                    case 5:
                        JOptionPane.showMessageDialog(null, rebuda.getMessage());
                        if (this.view.getCampNom().getText().isBlank()) {
                            this.view.getCampNom().requestFocus();
                        } else {
                            this.view.getCampNota().requestFocus();
                        }
                        break;
                    case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19:
                        JOptionPane.showMessageDialog(null, rebuda.getMessage());
                        this.view.getCampNota().setSelectionStart(0);
                        this.view.getCampNota().setSelectionEnd(this.view.getCampNota().getText().length());
                        this.view.getCampNota().requestFocus();
                        break;
                    case 20, 21, 22, 23, 24, 25, 26, 27, 28, 29:
                        JOptionPane.showMessageDialog(null, rebuda.getMessage());
                        //this.view.getCampNom().setText(rebuda.getMissatge());
                        this.view.getCampNom().setSelectionStart(0);
                        this.view.getCampNom().setSelectionEnd(this.view.getCampNom().getText().length());
                        this.view.getCampNom().requestFocus();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, rebuda.getMessage());
                        break;
                }
            }
        }
    }
}