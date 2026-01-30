package com.pendulum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Double Pendulum Simulation
 * A chaotic physics simulation demonstrating sensitive dependence on initial conditions.
 */
public class DoublePendulum extends JPanel implements ActionListener {

    // Physics constants
    private static final double G = 9.81;  // Gravity
    private static final double DT = 0.02; // Time step

    // Pendulum state
    private double l1 = 150;  // Length of pendulum 1
    private double l2 = 150;  // Length of pendulum 2
    private double m1 = 10;   // Mass of pendulum 1
    private double m2 = 10;   // Mass of pendulum 2
    private double a1 = Math.PI / 2;   // Angle of pendulum 1
    private double a2 = Math.PI / 2;   // Angle of pendulum 2
    private double a1_v = 0;  // Angular velocity of pendulum 1
    private double a2_v = 0;  // Angular velocity of pendulum 2

    // Initial values for reset
    private double initL1 = 150;
    private double initL2 = 150;

    // Display
    private int originX, originY;
    private final List<Point2D.Double> trail = new ArrayList<>();
    private static final int MAX_TRAIL = 500;

    // Animation
    private final Timer timer;
    private boolean running = false;

    // UI Components
    private JSlider length1Slider;
    private JSlider length2Slider;
    private JButton playButton;
    private JButton resetButton;
    private JLabel length1Label;
    private JLabel length2Label;

    public DoublePendulum() {
        setPreferredSize(new Dimension(800, 700));
        setBackground(Color.BLACK);
        timer = new Timer(16, this); // ~60 FPS
    }

    public void setLength1(double l) {
        this.initL1 = l;
        if (!running) {
            this.l1 = l;
        }
    }

    public void setLength2(double l) {
        this.initL2 = l;
        if (!running) {
            this.l2 = l;
        }
    }

    public void toggleRunning() {
        running = !running;
        if (running) {
            timer.start();
            playButton.setText("⏸ Pause");
        } else {
            timer.stop();
            playButton.setText("▶ Play");
        }
    }

    public void reset() {
        running = false;
        timer.stop();
        playButton.setText("▶ Play");

        l1 = initL1;
        l2 = initL2;
        a1 = Math.PI / 2;
        a2 = Math.PI / 2;
        a1_v = 0;
        a2_v = 0;
        trail.clear();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            // Perform multiple physics steps per frame for accuracy
            for (int i = 0; i < 4; i++) {
                updatePhysics();
            }
            repaint();
        }
    }

    private void updatePhysics() {
        // Double pendulum equations of motion using Lagrangian mechanics
        double num1 = -G * (2 * m1 + m2) * Math.sin(a1);
        double num2 = -m2 * G * Math.sin(a1 - 2 * a2);
        double num3 = -2 * Math.sin(a1 - a2) * m2;
        double num4 = a2_v * a2_v * l2 + a1_v * a1_v * l1 * Math.cos(a1 - a2);
        double den = l1 * (2 * m1 + m2 - m2 * Math.cos(2 * a1 - 2 * a2));
        double a1_a = (num1 + num2 + num3 * num4) / den;

        num1 = 2 * Math.sin(a1 - a2);
        num2 = a1_v * a1_v * l1 * (m1 + m2);
        num3 = G * (m1 + m2) * Math.cos(a1);
        num4 = a2_v * a2_v * l2 * m2 * Math.cos(a1 - a2);
        den = l2 * (2 * m1 + m2 - m2 * Math.cos(2 * a1 - 2 * a2));
        double a2_a = (num1 * (num2 + num3 + num4)) / den;

        // Update velocities and angles
        a1_v += a1_a * DT;
        a2_v += a2_a * DT;
        a1 += a1_v * DT;
        a2 += a2_v * DT;

        // Add damping to prevent energy buildup from numerical errors
        a1_v *= 0.9999;
        a2_v *= 0.9999;

        // Calculate second bob position for trail
        double x1 = originX + l1 * Math.sin(a1);
        double y1 = originY + l1 * Math.cos(a1);
        double x2 = x1 + l2 * Math.sin(a2);
        double y2 = y1 + l2 * Math.cos(a2);

        trail.add(new Point2D.Double(x2, y2));
        if (trail.size() > MAX_TRAIL) {
            trail.remove(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        originX = getWidth() / 2;
        originY = getHeight() / 3;

        // Calculate positions
        double x1 = originX + l1 * Math.sin(a1);
        double y1 = originY + l1 * Math.cos(a1);
        double x2 = x1 + l2 * Math.sin(a2);
        double y2 = y1 + l2 * Math.cos(a2);

        // Draw trail with gradient
        for (int i = 1; i < trail.size(); i++) {
            Point2D.Double p1 = trail.get(i - 1);
            Point2D.Double p2 = trail.get(i);
            float alpha = (float) i / trail.size();
            int hue = (int) (alpha * 180);  // Blue to cyan gradient
            g2d.setColor(new Color(Color.HSBtoRGB(hue / 360f, 0.8f, alpha)));
            g2d.setStroke(new BasicStroke(2f * alpha));
            g2d.draw(new Line2D.Double(p1, p2));
        }

        // Draw pivot point
        g2d.setColor(Color.GRAY);
        g2d.fillOval(originX - 5, originY - 5, 10, 10);

        // Draw rods
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3f));
        g2d.draw(new Line2D.Double(originX, originY, x1, y1));
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));

        // Draw bobs
        int bobSize1 = (int) (m1 * 1.5);
        int bobSize2 = (int) (m2 * 1.5);

        g2d.setColor(new Color(255, 100, 100));
        g2d.fillOval((int) x1 - bobSize1 / 2, (int) y1 - bobSize1 / 2, bobSize1, bobSize1);

        g2d.setColor(new Color(100, 200, 255));
        g2d.fillOval((int) x2 - bobSize2 / 2, (int) y2 - bobSize2 / 2, bobSize2, bobSize2);

        // Draw title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2d.drawString("Double Pendulum Simulation", 10, 25);

        // Draw info
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(String.format("L1: %.0f  L2: %.0f", l1, l2), 10, 45);
        g2d.drawString(String.format("θ1: %.2f  θ2: %.2f", Math.toDegrees(a1) % 360, Math.toDegrees(a2) % 360), 10, 60);
    }

    public void setUIComponents(JSlider l1Slider, JSlider l2Slider, JButton play, JButton reset,
                                JLabel l1Label, JLabel l2Label) {
        this.length1Slider = l1Slider;
        this.length2Slider = l2Slider;
        this.playButton = play;
        this.resetButton = reset;
        this.length1Label = l1Label;
        this.length2Label = l2Label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fall back to default look and feel
            }

            JFrame frame = new JFrame("Double Pendulum - GraalVM Native Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DoublePendulum pendulum = new DoublePendulum();

            // Create control panel
            JPanel controlPanel = new JPanel();
            controlPanel.setBackground(new Color(40, 40, 40));
            controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

            // Length 1 slider
            JLabel l1Label = new JLabel("Length 1: 150");
            l1Label.setForeground(Color.WHITE);
            JSlider l1Slider = new JSlider(50, 250, 150);
            l1Slider.setBackground(new Color(40, 40, 40));
            l1Slider.setForeground(Color.WHITE);
            l1Slider.addChangeListener(e -> {
                int val = l1Slider.getValue();
                l1Label.setText("Length 1: " + val);
                pendulum.setLength1(val);
            });

            // Length 2 slider
            JLabel l2Label = new JLabel("Length 2: 150");
            l2Label.setForeground(Color.WHITE);
            JSlider l2Slider = new JSlider(50, 250, 150);
            l2Slider.setBackground(new Color(40, 40, 40));
            l2Slider.setForeground(Color.WHITE);
            l2Slider.addChangeListener(e -> {
                int val = l2Slider.getValue();
                l2Label.setText("Length 2: " + val);
                pendulum.setLength2(val);
            });

            // Play button
            JButton playButton = new JButton("▶ Play");
            playButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            playButton.setBackground(new Color(70, 130, 70));
            playButton.setForeground(Color.WHITE);
            playButton.setFocusPainted(false);
            playButton.addActionListener(e -> pendulum.toggleRunning());

            // Reset button
            JButton resetButton = new JButton("↺ Reset");
            resetButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            resetButton.setBackground(new Color(130, 70, 70));
            resetButton.setForeground(Color.WHITE);
            resetButton.setFocusPainted(false);
            resetButton.addActionListener(e -> pendulum.reset());

            pendulum.setUIComponents(l1Slider, l2Slider, playButton, resetButton, l1Label, l2Label);

            // Add components to control panel
            controlPanel.add(l1Label);
            controlPanel.add(l1Slider);
            controlPanel.add(l2Label);
            controlPanel.add(l2Slider);
            controlPanel.add(playButton);
            controlPanel.add(resetButton);

            // Layout
            frame.setLayout(new BorderLayout());
            frame.add(pendulum, BorderLayout.CENTER);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
