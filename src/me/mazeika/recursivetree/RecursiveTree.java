package me.mazeika.recursivetree;

import javax.swing.*;
import java.awt.*;

/**
 * Represents the main class of a recursive tree program.
 */
public class RecursiveTree
{
    private static final String TITLE = "Recursive Tree";
    private static final int FRAME_WIDTH = 900;
    private static final int FRAME_HEIGHT = 900;

    /**
     * The main method. Eventually sets up the GUI.
     *
     * @param args the program arguments
     */
    public static void main(String[] args)
    {
        new RecursiveTree().setUpGUI();
    }

    /**
     * Sets up the GUI.
     */
    public void setUpGUI()
    {
        final JFrame frame = new JFrame();
        final JPanel panel = new JPanel();
        final JPanel canvas = new Canvas();

        panel.setLayout(new BorderLayout());
        panel.add(canvas, BorderLayout.CENTER);

        frame.setContentPane(panel);

        frame.setTitle(TITLE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
