package com.smartcity;

import javax.swing.*;

public class MainFrame extends JFrame {
    private final HomePage homePage;

    public MainFrame() {
        setTitle("Smart City Guide");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        homePage = new HomePage(this);
    }

    public void showMainUI() {
        setContentPane(homePage); // Switch to HomePage
        revalidate();
        repaint();
        setVisible(true); // Show main UI after login
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        new Welcome(mainFrame); // Start with Welcome Page
    }
}
