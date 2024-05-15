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
import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

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
        taula.getColumnModel().getColumn(3).setMinWidth(0);
        taula.getColumnModel().getColumn(3).setMaxWidth(0);
        taula.getColumnModel().getColumn(3).setPreferredWidth(0);


        //5. Necessari per a que Controller reaccione davant de canvis a les propietats lligades
        canvis.addPropertyChangeListener(this);
    }
    private void setModelTaulaAlumne(DefaultTableModel modelTaulaAlumne, List<Alumne> all) {

        // Fill the table model with data from the collection
        for (Alumne estudiant : all) {
            modelTaulaAlumne.addRow(new Object[]{estudiant.getNomCognom(), estudiant.getNota(), true, estudiant});
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

                if (insertCorrecte()) {
                    try {
                        if (!nomRepetit()) {
                            insertAlumne();
                        }
                    } catch (ParseException ex) {
                        campNota.setSelectionStart(0);
                        campNota.setSelectionEnd(campNota.getText().length());
                        campNota.requestFocus();
                    }
                }
            }

            /**
             * @return si algun dels camps està buit, mostra un missatge d'error.
             */
            private boolean insertCorrecte() {
                if (campNom.getText().isBlank() || campNota.getText().isBlank()){
                    JOptionPane.showMessageDialog(null,"No olvidis emplenar tots els camps.", "Avís", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                return true;
            }

            /**
             * @return si el nom està repetit, mostra un missatge d'error.
             */
            private boolean nomRepetit() {
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (campNom.getText().equals(model.getValueAt(i, 0))) {
                        JOptionPane.showMessageDialog(null, "Aquest nom ja esta inscrit a la taula, canvial.", "Error", JOptionPane.ERROR_MESSAGE);
                        campNom.setSelectionStart(0);
                        campNom.setSelectionEnd(campNom.getText().length());
                        campNom.requestFocus();
                        return true;
                    }
                }
                return false;
            }

            /**
             * @return si tot està correcte, insereix un nou alumne a la taula.
             */
            private void insertAlumne() throws ParseException {
                Double nota = parsearIVerificarNota(campNota.getText());
                model.addRow(new Object[]{campNom.getText(), nota, SI_CheckBox.isSelected()});
                JOptionPane.showMessageDialog(null,"Has inscrit un nou alumne","Inscripció correcta",JOptionPane.INFORMATION_MESSAGE);
                campNom.setText("");
                campNota.setText("");
                SI_CheckBox.setSelected(false);
                campNom.requestFocus();
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
                    campNom.setText("");
                    campNota.setText("");
                    SI_CheckBox.setSelected(false);
                    campNom.requestFocus();
                }
            }

            /**
             * @return Omple els camps amb les dades de la fila seleccionada.
             */
            private void emplenarCamps(int fila) {
                campNom.setText(model.getValueAt(fila, 0).toString());
                campNota.setText(model.getValueAt(fila, 1).toString().replaceAll("\\.", ","));
                SI_CheckBox.setSelected((Boolean) model.getValueAt(fila, 2));
            }
        });


        // CODI DEL BOTO BORRAR
        borrarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de BORRAR");
                int filaSel = taula.getSelectedRow();
                if(filaSel!=-1){
                    model.removeRow(filaSel);
                    campNom.setText("");
                    campNota.setText("");
                    SI_CheckBox.setSelected(false);
                    campNom.requestFocus();
                }
                else JOptionPane.showMessageDialog(null, "Per borrar una fila l'has de seleccionar a la taula", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        // CODI DEL BOTO MODIFICAR
        modificarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha MODIFICAT un insert");
                int filaSel = taula.getSelectedRow();
                if(filaSel != -1){
                    if (campNom.getText().isBlank() || campNota.getText().isBlank()) {
                        JOptionPane.showMessageDialog(null,"No olvidis emplenar tots els camps.", "Avís", JOptionPane.WARNING_MESSAGE);
                    }
                    else {
                        actualitzaFila();
                    }
                }
                else JOptionPane.showMessageDialog(null, "Per modificar una fila l'has de seleccionar a la taula", "Error", JOptionPane.ERROR_MESSAGE);
            }

            /**
             * @return Actualitza la fila seleccionada amb les dades dels camps.
             */
            private void actualitzaFila() {
                int filaSel = taula.getSelectedRow();
                model.removeRow(filaSel);
                model.insertRow(filaSel, new Object[]{campNom.getText(), Double.valueOf(campNota.getText()), SI_CheckBox.isSelected()});
                campNom.setText("");
                campNota.setText("");
                SI_CheckBox.setSelected(false);
                campNom.requestFocus();
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
                    JOptionPane.showMessageDialog(null,"Has d'introduir una nota correcta i si te decimals separar-la per comes.", "Avís", JOptionPane.WARNING_MESSAGE);
                }

                try {
                    NumberFormat num=NumberFormat.getNumberInstance(Locale.getDefault());
                    double nota= num.parse(campNota.getText().trim()).doubleValue();
                    if (nota<0 || nota>10) {
                        JOptionPane.showMessageDialog(null,"Has d'introduir una nota correcta i si te decimals separar-la per comes.", "Avís", JOptionPane.WARNING_MESSAGE);
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
                    JOptionPane.showMessageDialog(null,"No pots introduir cap numero en aquest camp.", "Avís", JOptionPane.WARNING_MESSAGE);
                }
            }
        });


// CODI DEL BOTO EXPORTAR
        exportarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             * @return Exporta les dades a un fitxer de text.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de EXPORTAR");

                if (model.getRowCount() > 0) {
                    File arxiu = seleccionarArxiu();
                    if (arxiu != null) {
                        PrintWriter writer = crearPrintWriter(arxiu);
                        if (writer != null) {
                            escriureFiles(writer);
                            JOptionPane.showMessageDialog(null, "Fitxer guardat amb exit amb el nom: " + arxiu.getName() +  "\n" + "A la ruta: " + arxiu.getAbsolutePath());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi han dades per exportar.","Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            /**
             * @return Retorna el fitxer seleccionat.
             */
            private File seleccionarArxiu() {
                JFileChooser fitxerElegit = new JFileChooser();
                fitxerElegit.setCurrentDirectory(new File("./src/main/resources/"));
                fitxerElegit.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fitxerElegit.setDialogTitle("Guardar arxiu de sortida");

                int userSelection = fitxerElegit.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File arxiu = fitxerElegit.getSelectedFile();
                    if (!arxiu.getAbsolutePath().endsWith(".txt")) {
                        arxiu = new File(arxiu.getAbsolutePath() + ".txt");
                    }
                    return arxiu;
                }
                return null;
            }

            /**
             * @param arxiu Fitxer on es guardarà la informació.
             * @return Retorna un PrintWriter per a escriure les dades al fitxer.
             */
            private PrintWriter crearPrintWriter(File arxiu) {
                try {
                    return new PrintWriter(new BufferedWriter(new FileWriter(arxiu)));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al crear el fitxer de sortida.");
                    return null;
                }
            }

            /**
             * @param writer PrintWriter per a escriure les dades al fitxer.
             * @return Escriu les dades al fitxer.
             */
            private void escriureFiles(PrintWriter writer) {
                try {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        writer.println("ENTRADA " + (i + 1));
                        writer.println("Nom i cognom: " + model.getValueAt(i, 0));
                        writer.println("Nota: " + model.getValueAt(i, 1));
                        writer.println("FCT: " + model.getValueAt(i, 2));
                        writer.println("--------------------");
                    }
                } finally {
                    writer.close();
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

    /**
     * Aquest mètode s'encarrega de parsejar una cadena a un doble i verificar que estigui dins del rang de 0 a 10.
     * @param entrada La cadena que s'analitzarà.
     * @return El valor doble analitzat.
     * @throws ParseException Si el doble analitzat no està dins del rang de 0 a 10.
     */
    public double parsearIVerificarNota(String entrada) throws ParseException {
        NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());
        double nota = num.parse(entrada.trim()).doubleValue();
        if (nota < 0 || nota > 10) {
            throw new ParseException("El doble analitzat no es un numero entre 0 i 10.", 0);
        }
        return nota;
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
                    case 1:
                        JOptionPane.showMessageDialog(null, rebuda.getMessage());
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null, rebuda.getMessage());
                        //this.view.getCampNom().setText(rebuda.getMissatge());
                        this.view.getCampNom().setSelectionStart(0);
                        this.view.getCampNom().setSelectionEnd(this.view.getCampNom().getText().length());
                        this.view.getCampNom().requestFocus();

                        break;
                }
            }
        }
    }
}