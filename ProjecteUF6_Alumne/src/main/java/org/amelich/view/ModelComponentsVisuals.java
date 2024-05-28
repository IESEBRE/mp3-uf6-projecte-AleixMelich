package org.amelich.view;

import javax.swing.table.DefaultTableModel;

/**
 * Aquesta classe s'encarrega de gestionar els components visuals del model.
 * Conté un objecte DefaultTableModel que s'utilitza per representar les dades en un format de taula.
 */
public class ModelComponentsVisuals {
    //Propietats de la classe
    private DefaultTableModel model;

    //Getters dels objectes del model
    public DefaultTableModel getModel() {
        return model;
    }

    /**
     * Constructor de la classe Model
     */
    public ModelComponentsVisuals() {

        model=new DefaultTableModel(new Object[]{"ID","NOM","NOTA","FCT","Object"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                // LES CELES NO SON EDITABLES
                return false;
            }

            //Permet definir el tipo de cada columna
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Double.class;
                    case 3:
                        return Boolean.class;
                    default:
                        return Object.class;
                }
            }
        };
    }
}