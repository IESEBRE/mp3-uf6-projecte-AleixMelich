package org.amelich.view;

import javax.swing.*;

/**
 * Aquesta classe és la vista de la nostra aplicació.
 */
public class ViewPestanya extends JFrame{
    private JPanel panel;
    private JButton modificarButton;
    private JButton insertarButton;
    private JButton borrarButton;
    private JButton llimpiarButton;

    private JButton IDButton;

    //PESTANYA ALUMNES 2023/2024

    private JTable taula;
    private JTextField campNom;
    private JTextField campNota;
    private JCheckBox SI_CheckBox;
    //GETTERS

    public JButton getModificarButton() {
        return modificarButton;
    }
    public JButton getInsertarButton() {
        return insertarButton;
    }
    public JButton getBorrarButton() {
        return borrarButton;
    }
    public JButton getLlimpiarButton() {
        return llimpiarButton;
    }
    public JButton getIDButton() {
        return IDButton;
    }

    //PESTANYA ALUMNES 2023/2024
    public JTable getTaula() {
        return taula;
    }
    public JTextField getCampNom() {
        return campNom;
    }
    public JTextField getCampNota() {
        return campNota;
    }
    public JCheckBox getSI_CheckBox() {
        return SI_CheckBox;
    }



    /**
     * Constructor de la classe.
     */
    public ViewPestanya() {
        //PER PODER VEURE LA FINESTRA
        this.setContentPane(panel); // DEFINIM EL PANEL QUE CONTÉ TOTS ELS ELEMENTS
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // PERMETRE TANCAR LA FINESTRA
        this.pack(); // AJUSTAR LA FINESTRA AL CONTINGUT
        this.setVisible(false); // FER VISIBLE LA FINESTRA
        this.setSize(800, 600); // DEFINIM LA MIDA DE LA FINESTRA
        this.setLocationRelativeTo(null); // CENTRAR LA FINESTRA
        this.setTitle("PROJECTE UF6 - AMELICH"); // DEFINIM EL TITOL DE LA FINESTRA
        this.setIconImage(new ImageIcon("./src/main/resources/imagen.jpg").getImage()); // DEFINIM LA ICONA DE LA FINESTRA
    }
}