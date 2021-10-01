/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;

/**
 *
 * @author Borenste_848114
 */
public class ParetoPrincipleSim extends JFrame {

    private JLabel xLabel;
    private JLabel yLabel;
    private String[] args;
    public boolean restartSim = false;

    /**
     * This creates the Java Window and Initializes the initUI() method
     */
    public ParetoPrincipleSim() {
        initUI();
    }

    /**
     * This method creates Board and puts the Game into action
     */
    private void initUI() {
        xLabel = new JLabel("Amount of Money");
        add(xLabel, BorderLayout.SOUTH);
        yLabel = new JLabel(transformStringToHtml("Number of Agents"));
        add(yLabel, BorderLayout.WEST);

        Pareto perot = new Pareto(this,Long.parseLong(args[0]),Integer.parseInt(args[1]), Double.parseDouble(args[2]));
        add(perot);
        perot.start();

        setTitle("Pareto Principle");
        setSize(810, 610);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
    }

    public static String transformStringToHtml(String strToTransform) {
        String ans = "<html>";
        String br = "<br>";
        String[] lettersArr = strToTransform.split("");
        for (String letter : lettersArr) {
            ans += letter + br;
        }
        ans += "</html>";
        return ans;
    }

    /**
     * This method returns the text in the Status Bar
     *
     * @return Status Bar
     */
    public JLabel getStatusBarX() {
        return xLabel;
    }

    /**
     * This method returns the text in the Status Bar
     *
     * @return Status Bar
     */
    public JLabel getStatusBarY() {
        return yLabel;
    }

    /**
     * This is the main method that invokes the rest of the code
     *
     * @param args
     */
    public static void main(String[] args) {
        // ParetoPrincipleSim.args = args;
        EventQueue.invokeLater(() -> {
            ParetoPrincipleSim game = new ParetoPrincipleSim();
            game.setVisible(true);
        });
    }
}
