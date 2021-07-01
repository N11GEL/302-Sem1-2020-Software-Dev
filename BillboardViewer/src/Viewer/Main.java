package Viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.util.Timer;

/**
 * Viewer Class:
 * The Viewer class contains the functions which generate
 * the Swing layout components used in the Generate class.
 *
 */
class Viewer {
    // Stores billboard data
    public static String[] billboardData = new String[8];

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
     * @param fontSize    = the size of the font based on string length (a value of 10,000 covers about half of the screen)
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
    public Viewer(String backClr, String msg, String msgClr, String picData, String infoMsg, String infoClr) {
        backgroundColour = backClr;
        message = msg;
        messageColour = msgClr;
        pictureData = picData;
        infoMessage = infoMsg;
        infoColour = infoClr;
    }

    // Main
    public static void main(String[] args) {
        // Format frame
        Generate.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Generate.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Generate.frame.getContentPane().setBackground(Color.BLACK);

        // Setup timer with an interval of 15 seconds
        Timer timer = new Timer();
        timer.schedule(new Generate(), 0, 5000);

        // Listener events to close program on key/mouse press:
        // Close app if ESC is pressed
        Generate.frame.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Generate.frame.dispose();
                    System.exit(0);
                }
            }
            public void keyTyped(KeyEvent e) {
            }
        });

        // Close app if left/right mouse is clicked
        Generate.frame.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                Generate.frame.dispose();
                System.exit(0);
            }
        });

        // Setup and draw frame
        Generate.frame.setUndecorated(true);
        Generate.frame.setVisible(true);
    }
}

