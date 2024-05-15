package org.amelich.view;

import javax.swing.table.DefaultTableModel;


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

        model=new DefaultTableModel(new Object[]{"NOM","NOTA","FCT","Object"},0){
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
                        return String.class;
                    case 1:
                        return Double.class;
                    case 2:
                        return Boolean.class;
                    default:
                        return Object.class;
                }
            }
        };



        //Omplim la taula en dades del fitxer
        //Fitxers.llegirDades(model);
    }

    /**
     * Mètode per afegir un alumne a la taula
     */
//    public void escriureDadesFitxer(){
//        Fitxers.escriureDades(model);
//    }
}