package ControlPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;


// Viewer Class
public class ViewerPreview {
    // Stores billboard data
    //public static String[] billboardData = new String[8];

    // Declare billboard properties
    String message;
    String messageColour;
    String infoMessage;
    String infoColour;
    String pictureData;
    String backgroundColour;

    // Get and store screen dimensions
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double screenWidth = screenSize.getWidth();
    double screenHeight = screenSize.getHeight();

    // Test billboard data:
    //static String[] billboardData = {"Billboard Title", "#ff0000", "Info message Info message Info message", "#ffff00", "https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png", "#0000FF"};

    /**
     * Generates message:
     * Returns a JLabel which displays the billboard title message received from the billboard server
     *
     * @param message       = the contents of the message
     * @param messageColour = the colour of the message
     * @param messageWidth  = the width of the message determined by multiplying the screen width by this number
     * @param messageHeight = the height of the message determined by multiplying the screen height by this number
     * @return = return JLabel component
     */
    public JLabel generateMessage(String message, String messageColour, double messageWidth, double messageHeight) {
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        // Set text and colour
        messageLabel.setForeground(Color.decode(messageColour));

        // Generate font size //
        Font labelFont = messageLabel.getFont();
        String labelText = messageLabel.getText();

        // Get width of string in pixels
        int stringWidth = messageLabel.getFontMetrics(labelFont).stringWidth(labelText);
        // Determine maximum possible label size
        int componentWidth = (int) (screenWidth * messageWidth);
        int componentHeight = (int) (screenHeight * messageHeight);

        // Calculate how much larger the font can get
        double widthRatio = (double) componentWidth / (double) stringWidth;
        int newFontSize = (int) (labelFont.getSize() * widthRatio);

        // Calculate new font size smaller than the height of the label
        int fontSizeToUse = Math.min(newFontSize, componentHeight);

        // Set new font size
        messageLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));

        return messageLabel;
    }

    /**
     * Generates picture
     * Returns a JLabel containing an image received from the server
     *
     * @param picture  = the URL of the image or Base64 data
     * @param picScale = the size of the image determined by multiplying the screen width/height by this number Eg: (Image 50% width of the screen: picScale = 0.5)
     * @return = return JLabel component
     */
    public JLabel generatePicture(String picture, double picScale) {
        boolean data = false;
        // Check if provided data is URL or Base64
        if (!(picture.startsWith("https://"))) {
            data = true;
        }
        // Align image to center
        JLabel pictureLabel = new JLabel("", SwingConstants.CENTER);
        pictureLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        BufferedImage image;
        try {
            // Decode image if in base 64 format
            if (data) {
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] imageByte = decoder.decode(picture);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                image = ImageIO.read(bis);
            } else {
                // Else read the URL
                image = ImageIO.read(new URL(picture));
            }
            // Resize and convert back to image
            ImageIcon icon = new ImageIcon(image);
            Image convertedImg = icon.getImage();
            int imgWidth = convertedImg.getWidth(null);
            int imgHeight = convertedImg.getHeight(null);

            // Resize images based on their dimensions to fit billboard properly
            if (imgWidth <= imgHeight) {
                Image newImg = convertedImg.getScaledInstance(-1, (int) ((screenHeight) * picScale), Image.SCALE_DEFAULT);
                icon = new ImageIcon(newImg);
                pictureLabel.setIcon(icon);
            } else {
                Image newImg = convertedImg.getScaledInstance((int) ((screenWidth) * picScale), -1, Image.SCALE_DEFAULT);
                icon = new ImageIcon(newImg);
                pictureLabel.setIcon(icon);
            }
        } catch (Exception e) {
            pictureLabel.setText("Image not properly formatted.");
        }
        return pictureLabel;
    }

    /**
     * Generates info message:
     * Returns a Jlabel which displays the information message received from the billboard server
     *
     * @param infoMessage = the contents of the info message
     * @param infoColour  = the colour of the info message
     * @return = return JLabel component
     */
    public JLabel generateInfoMessage(String infoMessage, String infoColour, double fontSize) {
        JLabel infoLabel = new JLabel("<html>" + infoMessage + "</html>", SwingConstants.CENTER);
        infoLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Set text and colour
        infoLabel.setForeground(Color.decode(infoColour));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 75));
        Font labelFont = infoLabel.getFont();
        String labelText = infoLabel.getText();
        // Get width of string in pixels
        int stringWidth = infoLabel.getFontMetrics(labelFont).stringWidth(labelText);
        // Calibration variables
        int newFontSize;
        // If message is larger than fontSize: Decrease the current font size
        if (stringWidth > fontSize){
            double adjust = ((stringWidth - fontSize) / (fontSize)) * 75;
            newFontSize = (int)(75 - adjust);
        }
        // If message is the same as fontSize: Maintain the current font size
        if (stringWidth == fontSize){
            newFontSize = 75;
        }
        // If message is smaller than fontSize: Increase the current font size
        else {
            double adjust = ((fontSize - stringWidth) / fontSize) + 1.0;
            newFontSize = (int)(75 * adjust);
        }
        // Set new font size
        infoLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, newFontSize));

        return infoLabel;
    }

   

    // Billboard constructor
    public ViewerPreview(String backClr, String msg, String msgClr, String picData, String infoMsg, String infoClr) {
        backgroundColour = backClr;
        message = msg;
        messageColour = msgClr;
        pictureData = picData;
        infoMessage = infoMsg;
        infoColour = infoClr;
    }


    // Main
    public static void previewBillboard(String backCol, String messText, String  messCol, String pic, String infoText, String infoCol) {
        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();

        // Format frame
        JFrame frame = new JFrame("Billboard Viewer");
        // Format frame
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.BLACK);

        // If a successful connection is made and adequate data if retrieved: construct billboard
        // Clear frame
        frame.getContentPane().removeAll();
        //frame.repaint();
        // Create new billboard
        ViewerPreview billboard = new ViewerPreview(backCol, messText, messCol, pic, infoText, infoCol);
        frame.getContentPane().setBackground(Color.decode(billboard.backgroundColour));
        // Draw components based on availability
        if (billboard.message.length() > 0) {
            if (billboard.pictureData.length() > 0) {
                if (billboard.infoMessage.length() > 0) {
                    // Add all three components
                    GridLayout layout = new GridLayout(3, 1);
                    frame.setLayout(layout);
                    frame.add(billboard.generateMessage(billboard.message, billboard.messageColour, 0.75, 0.25));
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

        // Setup and draw frame
        frame.setVisible(true);
    }
}

