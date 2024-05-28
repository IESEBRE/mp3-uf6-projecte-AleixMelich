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


    private final ModelComponentsVisuals modelComponentsVisuals =new ModelComponentsVisuals();
    private final AlumneDAOJDBCOracleImpl dadesAlumnes;
    private final ViewPestanya view;

    //Variable per a controlar si la columna ID està visible o no
    private boolean isIDColumnVisible = false;

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
        //Amago la columna que conté l'ID de l'alumne
        taula.getColumnModel().getColumn(0).setMinWidth(0);
        taula.getColumnModel().getColumn(0).setMaxWidth(0);
        taula.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    private void setModelTaulaAlumne(DefaultTableModel modelTaulaAlumne, List<Alumne> all) {

        // Fill the table model with data from the collection
        for (Alumne estudiant : all) {
            modelTaulaAlumne.addRow(new Object[]{estudiant.getId(), estudiant.getNomCognom(), estudiant.getNota(), estudiant.isFct(), estudiant});
        }
    }

    private void afegirListeners() {

        DefaultTableModel model = this.modelComponentsVisuals.getModel();

        // BUTTON'S
        JButton insertarButton = view.getInsertarButton();
        JButton modificarButton = view.getModificarButton();
        JButton borrarButton = view.getBorrarButton();
        JButton llimpiarButton = view.getLlimpiarButton();
        JButton IDButton = view.getIDButton();

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
             * Aquest mètode s'invoca quan es fa clic a insertarButton.
             * Primer valida les dades de l'estudiant introduïdes a la interfície d'usuari.
             * Si la validació té èxit, s'intenta inserir l'estudiant a la base de dades i la taula.
             * Si la inserció té èxit, mostra un missatge de diàleg informant a l'usuari que s'ha inscrit un nou estudiant,
             * i esborra els camps de dades de l'estudiant a la interfície d'usuari.
             * Si es produeix un error durant la inserció, llança la DAOException corresponent.
             *
             * @param e L'esdeveniment que activa aquest mètode.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de INSERTAR");
                    try {
                        double nota = validarDades(campNom.getText(), campNota.getText(), model, false);

                        Alumne al = new Alumne(campNom.getText(), nota, SI_CheckBox.isSelected());
                        dadesAlumnes.insert(al);
                        model.addRow(new Object[]{dadesAlumnes.alumneID(al),campNom.getText(), nota, SI_CheckBox.isSelected(), al});

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
             * Aquest mètode s'invoca quan s'ha fet clic (prem i deixat anar) un botó del ratolí a la taula.
             * Primer comprova si una fila està seleccionada a la taula, si és així, crida al mètode emplenarCamps per omplir els camps de dades de l'estudiant amb les dades de la fila seleccionada.
             * Si no se selecciona cap fila, crida al mètode llimpiarCampsAlumnes per esborrar els camps de dades de l'estudiant.
             *
             * @param e El MouseEvent que activa aquest mètode.
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
             * Omple els camps amb les dades de la fila seleccionada.
             */
            private void emplenarCamps(int fila) {
                campNom.setText(model.getValueAt(fila, 1).toString());
                campNota.setText(model.getValueAt(fila, 2).toString().replaceAll("\\.", ","));
                SI_CheckBox.setSelected((Boolean) model.getValueAt(fila, 3));
            }
        });


        // CODI DEL BOTO BORRAR
        borrarButton.addActionListener(new ActionListener() {
            /**
             * Aquest mètode s'invoca quan es fa clic al botó borrar.
             * Primer comprova si una fila està seleccionada a la taula, si no, llança una DAOException amb el codi d'error 100.
             * Si se selecciona una fila, s'intenta eliminar l'estudiant de la base de dades i de la taula.
             * Si l'eliminació és correcta, elimina la fila de la taula i esborra els camps de dades de l'estudiant a la interfície d'usuari.
             * Si es produeix un error durant la supressió, genera una DAOException amb el codi d'error 103.
             *
             * @param e L'esdeveniment que activa aquest mètode.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de BORRAR");

                int filaSel = taula.getSelectedRow();
                if (filaSel != -1) {
                    Long idEstudiant = (Long) model.getValueAt(filaSel, 0);
                    try {
                        dadesAlumnes.delete(idEstudiant);
                    } catch (DAOException daoException) {
                        setExcepcio(new DAOException(103));
                    }
                    model.removeRow(filaSel);
                    llimpiarCampsAlumnes();
                } else setExcepcio(new DAOException(100));
            }
        });


        // CODI DEL BOTO MODIFICAR
        modificarButton.addActionListener(new ActionListener() {
            /**
             * Aquest mètode s'invoca quan es fa clic al botó modificar.
             * Primer comprova si una fila està seleccionada a la taula, si no, llança una DAOException amb el codi d'error 100.
             * Si se selecciona una fila, valida les dades de l'estudiant introduïdes a la interfície d'usuari.
             * Si la validació té èxit, s'intenta actualitzar l'estudiant a la base de dades i la taula.
             * Si l'actualització té èxit, mostra un missatge de diàleg informant a l'usuari que l'estudiant ha estat modificat,
             * i esborra els camps de dades de l'estudiant a la interfície d'usuari.
             * Si es produeix un error durant l'actualització, llança la DAOException corresponent.
             *
             * @param e L'esdeveniment que activa aquest mètode.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha MODIFICAT un insert");
                int filaSel = taula.getSelectedRow();
                if(filaSel != -1){
                    try {
                        double nota = validarDades(campNom.getText(), campNota.getText(), model, true);
                        Alumne aID = (Alumne) model.getValueAt(filaSel, 4); // Obtenim l'objecte alumne de la fila seleccionada
                        Long idAlumne = dadesAlumnes.alumneID(aID); // Usem el metode alumneID per obtenir l'id de l'alumne proporcionat per l'objecte aID

                        Alumne al = new Alumne(idAlumne, campNom.getText(), nota, SI_CheckBox.isSelected());
                        dadesAlumnes.update(al);
                        model.addRow(new Object[]{idAlumne, campNom.getText(), nota, SI_CheckBox.isSelected(), al});
                        model.removeRow(filaSel);
                        taula.setRowSelectionInterval(model.getRowCount()-1, model.getRowCount()-1); // Selecciona la linia afegida a la taula

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


        // CODI DEL BOTO LLIMPIAR TAULA
        llimpiarButton.addActionListener(new ActionListener() {
            /**
             * Aquest mètode s'invoca quan es fa clic al botó llimpiar.
             * Primer comprova si la taula està buida, si és així, llança una DAOException amb el codi d'error 101.
             * Si la taula no està buida, demana a l'usuari la confirmació per eliminar totes les files.
             * Si l'usuari confirma, intenta eliminar tots els estudiants de la base de dades i de la taula.
             * Si l'eliminació té èxit, mostra un missatge de diàleg informant a l'usuari que s'han suprimit totes les files,
             * i esborra els camps de dades de l'estudiant a la interfície d'usuari.
             * Si es produeix un error durant l'eliminació, es llança la DAOException corresponent.
             *
             * @param e L'esdeveniment que activa aquest mètode.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de LLIMPIAR");
                if (model.getRowCount() == 0) {
                    setExcepcio(new DAOException(101));
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Estàs segur que vols eliminar totes les files?", "Confirmar eliminació", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        dadesAlumnes.deleteAll();
                        while (model.getRowCount() > 0) {
                            model.removeRow(0);
                        }
                        JOptionPane.showMessageDialog(null, "Totes les files han estat eliminades", "Eliminació completada", JOptionPane.INFORMATION_MESSAGE);
                        llimpiarCampsAlumnes();
                    } catch (DAOException ex) {
                        setExcepcio(ex);
                    }
                }

            }
        });


        // CODI DEL BOTO ID
        IDButton.addActionListener(new ActionListener() {
            /**
             * Aquest mètode s'invoca quan es fa clic a l'IDButton.
             * Alterna la visibilitat de la columna ID a la taula.
             * Si la columna ID és visible actualment, l'amaga.
             * Si la columna d'identificació està oculta actualment, ho mostra.
             *
             * @param e L'esdeveniment que activa aquest mètode.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Canvia l'estat de la variable isIDColumnVisible
                isIDColumnVisible = !isIDColumnVisible;

                // Mostra o amaga la columna d'ID en funció de l'estat de la variable isIDColumnVisible
                if (isIDColumnVisible) {
                    // Mostra la columna d'ID
                    taula.getColumnModel().getColumn(0).setMinWidth(50);
                    taula.getColumnModel().getColumn(0).setMaxWidth(50);
                    taula.getColumnModel().getColumn(0).setPreferredWidth(50);
                } else {
                    // Amaga la columna d'ID
                    taula.getColumnModel().getColumn(0).setMinWidth(0);
                    taula.getColumnModel().getColumn(0).setMaxWidth(0);
                    taula.getColumnModel().getColumn(0).setPreferredWidth(0);
                }
            }
        });

    }



    //METODES
    /**
     * Aquest mètode s'utilitza per configurar l'aparença i el comportament d'una JTable.
     * Estableix el tipus de lletra de la capçalera de la taula, l'alçada de la fila, el fons i el color de primer pla de la capçalera de la taula,
     * el mode de selecció i desactiva el reordenament i el canvi de mida de la capçalera de la taula.
     * També estableix l'editor per defecte de la taula a nul (desactivant l'edició de cel·les),
     * i canvia el cursor a un cursor manual quan es passa el cursor per sobre de la taula.
     *
     * @param table La JTable que s'ha de configurar.
     */
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

    /**
     * Aquest mètode s'utilitza per validar les dades de l'estudiant introduïdes a la interfície d'usuari.
     * Comprova si els camps de nom i qualificació no estan buits, si el nom no conté números,
     * si el nom no es repeteix a la taula (quan no es modifica), i si la nota és un nombre vàlid entre 0 i 10.
     * Si alguna d'aquestes validacions falla, genera una DAOException amb el codi d'error corresponent.
     *
     * @param nom El nom de l'alumne.
     * @param notaText La qualificació de l'alumne com a cadena.
     * @param model El model de taula de la taula de dades de l'estudiant.
     * @param isModifying Un booleà que indica si la validació és per modificar un estudiant existent.
     * @return La nota de l'alumne com a doble.
     * @throws DAOException Si alguna de les validacions falla.
     */
    private double validarDades(String nom, String notaText, DefaultTableModel model, boolean isModifying) throws DAOException {
        // Validar que el nom i la nota no estiguin buits
        if (nom.isBlank() || notaText.isBlank()) {
            throw new DAOException(5);
        }

        // Validar que el nom no contingui números
        if (nom.matches(".*\\d.*")) {
            throw new DAOException(22);
        }

        // Validar que el nom no estigui repetit
        if (!isModifying) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (nom.equals(model.getValueAt(i, 1))) {
                    throw new DAOException(23);
                }
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

    /**
     * Aquest mètode s'utilitza per esborrar els camps de dades de l'estudiant a la interfície d'usuari.
     * Estableix el text dels camps de nom i qualificació en una cadena buida,
     * desmarca la casella de selecció FCT i posa el focus al camp de nom.
     */
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