
package com.metromanagement.main;

import javax.swing.SwingUtilities;
import com.metromanagement.gui.LoginFrame;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
