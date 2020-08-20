package gaskell;

import javax.swing.*;
import java.awt.*;

/*
 * @author Connor Gaskell
 * @description Creates the JFrame
 */

public class Frame extends JFrame {

    /*
     * Default frame dimensions
     */
    private int frameWidth = 650;
    private int frameHeight = 500;

    /*
     * Constructor, sets up the JFrame
     */
    public Frame() {
        // Call the additionalSetup() method.
        additionalSetup();

        // Set the JFrame layout to BorderLayout
        setLayout(new BorderLayout());

        // Set the title of the application
        setTitle(Constants.GAME_NAME);

        // Set the minimum size of the window
        setMinimumSize(new Dimension(frameWidth, frameHeight));

        // Set the initial size of the window
        setSize(1150, 750);

        // Centre the window relative to the screen
        setLocationRelativeTo(null);

        // Close the window when the user presses the close button
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create an object of Game (Java3D Canvas) and add it to the frame
        getContentPane().add(new Game(), BorderLayout.CENTER);

        // Set the window visible
        setVisible(true);
    }

    /*
     * Java3D requires a system property be set, along with disabling a few features.
     * Reference: https://justjava3d.wordpress.com/2017/01/30/java3d-jframe-setup/
     */
    public void additionalSetup() {
        System.setProperty("sun.awt.noerasebackground", "true");
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
    }

}
