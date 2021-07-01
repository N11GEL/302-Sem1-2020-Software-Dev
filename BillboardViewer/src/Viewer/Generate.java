package Viewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.TimerTask;

/**
 * The purpose of this class is to generate a billboard based on the information retrieved from
 * the serverConnect() method in the Connect class.
 */
public class Generate extends TimerTask {
    // Get screen size
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double screenWidth = screenSize.getWidth();
    double screenHeight = screenSize.getHeight();
    // Create frame
    public static JFrame frame = new JFrame("Billboard Viewer");

    // Create timer task to run request and generate billboard based on timer interval
    @Override
    public void run() {
        try {
            // If a successful connection is made and adequate data if retrieved: construct billboard
            if (Connect.serverConnect()) {
                // Clear frame
                frame.getContentPane().removeAll();
                //frame.repaint();
                // Create new billboard
                Viewer billboard = new Viewer(Viewer.billboardData[2], Viewer.billboardData[3], Viewer.billboardData[4], Viewer.billboardData[5], Viewer.billboardData[6], Viewer.billboardData[7]);
                frame.getContentPane().setBackground(Color.decode(billboard.backgroundColour));
                // Draw components based on availability
                if (billboard.message.length() > 0) {
                    if (billboard.pictureData.length() > 0) {
                        if (billboard.infoMessage.length() > 0) {
                            // Add all three components
                            GridLayout layout = new GridLayout(3, 1);
                            frame.setLayout(layout);
                            frame.add(billboard.generateMessage(billboard.message, billboard.messageColour, 0.75, 0.28));
                            frame.add(billboard.generatePicture(billboard.pictureData, 0.333));
                            JPanel listPane = new JPanel();
                            BoxLayout boxlayout = new BoxLayout(listPane, BoxLayout.X_AXIS);
                            listPane.setLayout(boxlayout);
                            listPane.add(Box.createRigidArea(new Dimension((int) ((screenWidth) * 0.125), 0)));
                            listPane.add(Box.createHorizontalGlue());
                            listPane.add(billboard.generateInfoMessage(billboard.infoMessage, billboard.infoColour, 6000));
                            listPane.add(Box.createHorizontalGlue());
                            listPane.add(Box.createRigidArea(new Dimension((int) ((screenWidth) * 0.125), 0)));
                            listPane.setBackground(Color.decode(billboard.backgroundColour));
                            frame.add(listPane);

                        } else {
                            // Just message and picture
                            JPanel listPane = new JPanel();
                            BoxLayout boxlayout = new BoxLayout(listPane, BoxLayout.Y_AXIS);
                            listPane.setLayout(boxlayout);
                            listPane.add(Box.createRigidArea(new Dimension(0, (int) ((screenHeight) / 9))));
                            listPane.add(billboard.generateMessage(billboard.message, billboard.messageColour, 0.5, 0.3));
                            listPane.add(Box.createVerticalGlue());
                            listPane.add(billboard.generatePicture(billboard.pictureData, 0.5));
                            listPane.add(Box.createRigidArea(new Dimension(0, (int) ((screenHeight) / 9))));
                            listPane.setBackground(Color.decode(billboard.backgroundColour));
                            frame.add(listPane);
                        }
                    } else {
                        if (billboard.infoMessage.length() > 0) {
                            // Just message and info
                            GridLayout layout = new GridLayout(2, 1);
                            frame.setLayout(layout);
                            frame.add(billboard.generateMessage(billboard.message, billboard.messageColour, 0.75, 0.5));
                            JPanel listPane = new JPanel();
                            BoxLayout boxlayout = new BoxLayout(listPane, BoxLayout.X_AXIS);
                            listPane.setLayout(boxlayout);
                            listPane.add(Box.createRigidArea(new Dimension((int) ((screenWidth) * 0.125), 0)));
                            listPane.add(Box.createHorizontalGlue());
                            listPane.add(billboard.generateInfoMessage(billboard.infoMessage, billboard.infoColour, 8000));
                            listPane.add(Box.createHorizontalGlue());
                            listPane.add(Box.createRigidArea(new Dimension((int) ((screenWidth) * 0.125), 0)));
                            listPane.setBackground(Color.decode(billboard.backgroundColour));
                            frame.add(listPane);
                        } else {
                            // Just message
                            GridLayout layout = new GridLayout(1, 1);
                            frame.setLayout(layout);
                            frame.add(billboard.generateMessage(billboard.message, billboard.messageColour, 0.9, 0.5));
                        }
                    }
                } else {
                    if (billboard.pictureData.length() > 0) {
                        if (billboard.infoMessage.length() > 0) {
                            // Just picture and info
                            JPanel listPane = new JPanel();
                            BoxLayout boxlayout = new BoxLayout(listPane, BoxLayout.Y_AXIS);
                            listPane.setLayout(boxlayout);
                            listPane.add(Box.createRigidArea(new Dimension(0, (int) ((screenHeight) / 7))));
                            listPane.add(billboard.generatePicture(billboard.pictureData, 0.5));
                            listPane.add(Box.createVerticalGlue());
                            JPanel pane = new JPanel();
                            pane.setLayout(new BorderLayout());
                            pane.setMaximumSize(new Dimension((int) (screenWidth * 0.75), (int) (screenHeight * 0.33)));
                            pane.add(billboard.generateInfoMessage(billboard.infoMessage, billboard.infoColour, 5000), BorderLayout.CENTER);
                            pane.setBackground(Color.decode(billboard.backgroundColour));
                            listPane.add(pane);
                            listPane.setBackground(Color.decode(billboard.backgroundColour));
                            listPane.add(Box.createRigidArea(new Dimension(0, (int) ((screenHeight) / 7))));
                            listPane.setBackground(Color.decode(billboard.backgroundColour));
                            frame.add(listPane);

                        } else {
                            // Just picture
                            GridLayout layout = new GridLayout(1, 1);
                            frame.setLayout(layout);
                            frame.add(billboard.generatePicture(billboard.pictureData, 0.5));
                        }
                    } else {
                        // Just info
                        JPanel listPane = new JPanel();
                        BoxLayout boxlayout = new BoxLayout(listPane, BoxLayout.X_AXIS);
                        listPane.setLayout(boxlayout);
                        listPane.add(Box.createVerticalGlue());
                        listPane.add(Box.createHorizontalGlue());
                        listPane.add(Box.createRigidArea(new Dimension((int) ((screenWidth) * 0.15), 0)));
                        listPane.add(billboard.generateInfoMessage(billboard.infoMessage, billboard.infoColour, 8655));
                        listPane.add(Box.createRigidArea(new Dimension((int) ((screenWidth) * 0.15), 0)));
                        listPane.add(Box.createHorizontalGlue());
                        listPane.add(Box.createVerticalGlue());
                        listPane.setBackground(Color.decode(billboard.backgroundColour));
                        frame.add(listPane);
                    }
                }
                frame.setVisible(true);
            } else {
                // Clear frame
                frame.getContentPane().removeAll();
                frame.repaint();
                // Create empty billboard with error message
                Viewer billboard = new Viewer(null, null, null, null, null, null);
                GridLayout layout = new GridLayout(1, 1);
                frame.add(billboard.generateMessage("No billboard available at this time", "#FFFFFF", 0.20, 0.20));
                frame.setLayout(layout);
                frame.getContentPane().setBackground(Color.BLACK);
                frame.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

