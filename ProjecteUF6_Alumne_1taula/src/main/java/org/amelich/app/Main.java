package org.amelich.app;

import org.amelich.controller.Controller;
import org.amelich.model.impls.AlumneDAOJDBCOracleImpl;
import org.amelich.view.ModelComponentsVisuals;
import org.amelich.view.ViewPestanya;

import javax.swing.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                //Definim la cultura de la nostra aplicació
                Locale.setDefault(new Locale("ca","ES"));
                new Controller(new AlumneDAOJDBCOracleImpl(), new ViewPestanya());
            }
        });
    }

}