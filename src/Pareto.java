/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Borenste_848114
 */
public class Pareto extends JPanel {

    private long startTime = 0;

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int INITIAL_DELAY = 100;
    private final int PERIOD_INTERVAL = 300;

    private Timer timer;
    private Scanner in = new Scanner(System.in);
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private long runsToDo = 0;
    private long currentCycle;
    public Color[][] palette = new Color[256][150];
    private String typedInput = "";
    private boolean typing = false;
    private int typedInputCommand = 0;
    private double chanceToBeg = 0;
    private boolean wealthOver160 = false;

    private ArrayList<Agent> agents = new ArrayList<>();

    public Pareto(ParetoPrincipleSim parent, long agentSampleSize, int inititalWealth, double chanceToBeg) {
        initBoard(parent, agentSampleSize, inititalWealth, chanceToBeg);
    }

    private void initBoard(ParetoPrincipleSim parent, long agentSampleSize, int initialWealth, double chanceToBeg) {
        setFocusable(true);
        color();
        wealthOver160 = initialWealth > 160;
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(),
                INITIAL_DELAY, PERIOD_INTERVAL);
        addKeyListener(new TAdapter());
        for (long i = 1; i <= agentSampleSize; i++) {
            agents.add(new Agent(initialWealth, i));
        }
        this.chanceToBeg = chanceToBeg;
        color();
        repaint();
    }

    public void start() {
        startTime = System.currentTimeMillis();
        print("New Sim \nCycle 0", true, false);
        isStarted = true;
        isPaused = false;
    }

    public void print(String i, boolean topLineBreak, boolean bottomLineBreak) {
        String form = "0.00";
        DecimalFormat format = new DecimalFormat(form);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        Double te = (double) timeElapsed / 1000;
        String second = format.format(te % 60);
        timeElapsed /= 1000;
        long minute = (timeElapsed - (timeElapsed % 60)) / 60;
        i = minute + ":" + second + " :: " + i;

        if (topLineBreak) {
            for (int j = 0; j < i.length() + (i.length() % 5); j++) {
                System.out.print("*");
            }
            System.out.println("");
        }
        System.out.println(i);
        if (bottomLineBreak) {
            for (int j = 0; j < i.length() + (i.length() % 5); j++) {
                System.out.print("*");
            }
            System.out.println("");
        }
    }

    public void printAgents() {
        for (int i = 0; i <= 160; i++) {
            for (Agent a : agents) {
                if (a.getValue() == i) {
                    System.out.println(a);
                }
            }
        }
        for (Agent a : agents) {
            if (a.getValue() > 80) {
                System.out.println(a);
            }
        }
    }

    public void printMoneyAgents(long wealth) {
        System.out.println("Agents with $" + wealth + " :: " + totalAgents(wealth));
    }

    public void printMoneyAgents() {
        for (long i = 0; i <= 160; i++) {
            if (totalAgents(i) > 0) {
                printMoneyAgents(i);
            }
        }
    }

    public void runCycle() {
        if (runsToDo != 0 && !isPaused && isStarted) {
            runsToDo--;
            currentCycle++;
            runTrades();
            repaint();
            print("Success Cycle: " + currentCycle, true, false);
            // printMoneyAgents();
            // printMoneyAgents(0);
            if (agentsWithWealth() == 1) {
                isStarted = false;
                print("Complete!", true, true);
            }
        }
    }

    public int sizeFinder() {
        long output = Long.MIN_VALUE;
        for (int i = 0; i <= 160; i++) {
            if (totalAgents(i) > output) {
                output = (int) totalAgents(i);
            }
        }
        output = (int) Math.round((output + (5 - (output % 5))) * 1.1);
        return (int) output;
    }

    public int agentsWithWealth() {
        int output = 0;
        for (Agent a : agents) {
            if (a.isPlayable()) {
                output++;
            }
        }
        return output;
    }

    public void doCycle() {
        if (!isPaused && isStarted) {
            runsToDo--;
            currentCycle++;
            runTrades();
            repaint();
            print("Success Cycle: " + currentCycle, true, false);
            // printMoneyAgents();
            if (agentsWithWealth() == 1) {
                isStarted = false;
                print("Complete!", true, true);
            }
        }
    }

    public double totalAgents(long i) {
        double output = 0;
        for (Agent a : agents) {
            if (a.getValue() == i) {
                output++;
            }
        }
        return output;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateGraphics(g);
    }

    public void updateGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int maxHeight = sizeFinder();
        g2.setBackground(Color.BLACK);
        clearScreen(g2);

        Color orig = g2.getColor();
        g2.setColor(Color.WHITE);
        g2.drawString("Height: " + maxHeight, 15, 15);
        g2.setColor(orig);

        if (bigEnough()) {
            g2.setColor(palette[0][0]);
            for (int i = 0; i < 700; i += 10) {
                g2.setColor(palette[(96 + (i / 10)) % 256][0]);
                g2.drawString("->", 750, i);
            }
        }

        for (long i = 0; i < 999; i++) {
            double height = totalAgents(i);
            height *= (600.0 / maxHeight);
            g2.setColor(palette[(int) i % 256][0]);
            Rectangle2D rect = new Rectangle2D.Double((double) i * 5, 553 - height, 5.0, height);
            g2.draw(rect);
        }
        double height = 0;
        for (Agent a : agents) {
            if (a.getValue() >= 999) {
                height++;
            }
        }
        height *= (600.0 / maxHeight);
        g2.setColor(palette[(int) 160][0]);
        Rectangle2D rect = new Rectangle2D.Double((double) 160 * 5, 553 - height, 5.0, height);
        g2.draw(rect);
        for (int i = 0; i < 1; i++) {
            shiftColors();
        }
    }

    public void shiftColors() {
        Color[][] output = new Color[palette.length][palette[0].length];
        Color temp = palette[palette.length - 1][0];

        for (int i = (output.length - 2); i >= 0; i--) {
            for (int j = 0; j < output[i].length; j++) {
                output[i + 1][j] = palette[i][j];
            }
        }

        for (int i = 0; i < output[0].length; i++) {
            output[0][i] = temp;
        }
        palette = output;
    }

    private void pause() {
        if (!isStarted) {
            return;
        }
        isPaused = !isPaused;
        if (isPaused) {
            print("Paused", false, true);
        } else {
            print("Unpaused", true, false);
        }
    }

    private void color() {
        for (int i = 0; i <= 255; i++) {
            for (int t = 0; t < 150; t++) {
                palette[i][t] = HSVtoRGB(i, 255, 255);
            }
        }
    }

    public Color HSVtoRGB(double hh, double ss, double vv) {
        float r = 0, g = 0, b = 0, h, s, v = 0; //this function works with floats between 0 and 1
        h = (float) (hh / 256.0);
        s = (float) (ss / 256.0);
        v = (float) (vv / 256.0);
        if (s == 0) {
            r = g = b = v;
        }
        float f, p, q, t;
        int i;
        h *= 6; //to bring hue to a number between 0 and 6, better for the calculations
        i = (int) Math.round(Math.floor(h));  //e.g. 2.7 becomes 2 and 3.01 becomes 3 or 4.9999 becomes 4
        f = h - i;  //the fractional part of h
        p = v * (1 - s);
        q = v * (1 - (s * f));
        t = v * (1 - (s * (1 - f)));
        switch (i) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
        }
        int rr = (int) Math.round(r * 255.0);
        int gg = (int) Math.round(g * 255.0);
        int bb = (int) Math.round(b * 255.0);
        return new Color(rr, gg, bb);
    }

    private void runTrades() {
        for (Agent a : agents) {
            int pos = randomViableAgent(a);
            if (!a.isPlayable() && chanceToBeg > 0) {
                // Begin Charity
                double shouldIBeg = Math.random();
                if (shouldIBeg < chanceToBeg) {
                    int keepTrying = (int) Math.ceil(agents.size()*.05);
                    while (keepTrying > 0) {
                        if (agents.get(pos).isPlayable() && Math.random() < .5) {
                            a.win();
                            agents.get(pos).lose();
                            keepTrying = 0;
                        } else {
                            keepTrying--;
                            pos = randomViableAgent(a);
                        }
                    }
                }
                // End Charity
            } else {
                // Begin Harsh Trade
                if (a.isPlayable()) {
                    while (!agents.get(pos).isPlayable()) {
                        pos = randomViableAgent(a);
                    }
                    a.trade(agents.get(pos));
                }
                // End Harsh Trade
            }

        }
    }

    private int randomViableAgent(Agent a) {
        Random rando = new Random();
        int output = 0;
        /**
         * do { output = rando.nextInt(agents.size()); } while
         * (!agents.get(output).isPlayable() && agents.get(output).getId() !=
         * a.getId());
         */
        output = rando.nextInt(agents.size());
        return output;
    }

    private void clearScreen(Graphics g2) {
        Color original = g2.getColor();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 10000, 10000);
        g2.setColor(original);
    }

    private boolean bigEnough() {
        for (long i = 0; i < 170; i++) {
            if (wealthOver160) {
                return true;
            } else {
                if ((totalAgents(i) > 0) && i > 160) {
                    wealthOver160 = true;
                    return true;
                }
            }
        }
        return false;
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == KeyEvent.VK_P) {
                pause();
                return;
            }

            if (isPaused) {
                // return;
            }

            switch (keycode) {

                case KeyEvent.VK_1:
                    if (typing) {
                        typedInput += "1";
                        System.out.print("1");
                    } else {
                        runsToDo++;
                    }
                    break;
                case KeyEvent.VK_2:
                    if (typing) {
                        typedInput += "2";
                        System.out.print("2");
                    } else {
                        runsToDo += 2;
                    }
                    break;
                case KeyEvent.VK_3:
                    if (typing) {
                        typedInput += "3";
                        System.out.print("3");
                    } else {
                        runsToDo += 3;
                    }
                    break;

                case KeyEvent.VK_4:
                    if (typing) {
                        typedInput += "4";
                        System.out.print("4");
                    } else {
                        runsToDo += 4;
                    }
                    break;

                case KeyEvent.VK_5:
                    if (typing) {
                        typedInput += "5";
                        System.out.print("5");
                    } else {
                        runsToDo += 5;
                    }
                    break;

                case KeyEvent.VK_6:
                    if (typing) {
                        typedInput += "6";
                        System.out.print("6");
                    } else {
                        runsToDo += 6;
                    }
                    break;

                case KeyEvent.VK_7:
                    if (typing) {
                        typedInput += "7";
                        System.out.print("7");
                    } else {
                        runsToDo += 7;
                    }
                    break;

                case KeyEvent.VK_8:
                    if (typing) {
                        typedInput += "8";
                        System.out.print("8");
                    } else {
                        runsToDo += 8;
                    }
                    break;

                case KeyEvent.VK_9:
                    if (typing) {
                        typedInput += "9";
                        System.out.print("9");
                    } else {
                        runsToDo += 9;
                    }
                    break;

                case KeyEvent.VK_0:
                    if (typing) {
                        typedInput += "0";
                        System.out.print("0");
                    } else {
                        runsToDo += 0;
                    }
                    break;

                case KeyEvent.VK_BACK_SPACE:
                    if (typing) {
                        if (typedInput.length() != 0) {
                            typedInput = typedInput.substring(0, typedInput.length() - 1);
                            System.out.print("\b");
                        }
                    } else {
                        runsToDo += 0;
                    }
                    break;

                case KeyEvent.VK_SPACE:
                    if (runsToDo < 0) {
                        runsToDo = 0;
                    } else {
                        runsToDo = -1;
                    }
                    break;

                case KeyEvent.VK_Q:
                    printMoneyAgents();
                    break;

                case KeyEvent.VK_ENTER:
                    if (typing) {
                        if (typedInputCommand == 2) {
                            equals();
                        } else if (typedInputCommand == 1) {
                            subtract();
                        } else {
                            add();
                        }
                        System.out.println("");
                        pause();
                    } else {
                        doCycle();
                    }
                    break;

                case KeyEvent.VK_EQUALS:
                    pause();
                    equals();
                    break;

                case KeyEvent.VK_SUBTRACT:
                    pause();
                    subtract();
                    break;

                case KeyEvent.VK_ADD:
                    pause();
                    add();
                    break;
            }
        }

        private void equals() {
            if (typing) {
                runsToDo = outputTyping();
                typedInputCommand = 0;
            } else {
                inputTyping();
                typedInputCommand = 2;
            }
        }

        private void add() {
            if (typing) {
                runsToDo += outputTyping();
                typedInputCommand = 0;
            } else {
                inputTyping();
                typedInputCommand = 0;
            }
        }

        private void subtract() {
            if (typing) {
                runsToDo -= outputTyping();
                typedInputCommand = 0;
            } else {
                inputTyping();
                typedInputCommand = 1;
            }
        }

        private void inputTyping() {
            System.out.print("InputTyping :: " + typedInput);
            typedInput = "0";
            typing = true;
        }

        private long outputTyping() {
            if (typedInput.contains("[a-zA-Z]+") == true) {
                typedInput = "0";
                typing = true;
                return 0;
            } else {
                long output = Long.parseLong(typedInput);
                typedInput = "";
                typing = false;
                return output;
            }
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {
            runCycle();
        }
    }
}
