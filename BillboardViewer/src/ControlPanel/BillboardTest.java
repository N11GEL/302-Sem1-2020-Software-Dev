package ControlPanel;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class BillboardTest {

    Billboard billboard;

    @BeforeEach
    void ConstructBillboard(){
        billboard = new Billboard();
    }

    @Test
    void fromStrings(){
        String name, backgroundColour, message, messageColour, pictureURL, infoMessage, infoColour, creator;
        name = "Test";
        backgroundColour = "#000000";
        message = "This is a message test";
        messageColour = "#FFFFFF";
        pictureURL = "https://www.google.com/image.png";
        infoMessage = "This is the informative message";
        infoColour = "#FFFFFF";
        creator = "TestCreator";
        assertTrue(billboard.fromStrings(name, backgroundColour, message, messageColour, "", "", infoMessage, infoColour, creator));
    }

    @Test
    void fromXMLTest() throws Exception{
        billboard.fromXML("C:\\Users\\timbr\\OneDrive\\Uni\\2-1\\CAB302\\test.xml");
        assertEquals("#0000FF", billboard.backgroundColour);
        assertEquals("#FFFF00", billboard.messageColour);
        assertEquals("Welcome to the ____ Corporation's Annual Fundraiser!", billboard.message);
        assertEquals("https://example.com/fundraiser_image.jpg", billboard.pictureURL);
        assertEquals("#00FFFF", billboard.infoColour);
        assertEquals("Be sure to check out https://example.com/ for more information.", billboard.infoMessage);
    }

    @Test
    void toXMLTest() throws Exception{
        fromXMLTest();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
                             "<billboard background=\"#0000FF\">" + "\n" +
                              "<message colour=\"#FFFF00\">Welcome to the ____ Corporation's Annual Fundraiser!</message>" + "\n" +
                              "<picture url=\"https://example.com/fundraiser_image.jpg\" />" + "\n" +
                              "<information colour=\"#00FFFF\">Be sure to check out https://example.com/ for more information.</information>" + "\n" +
                             "</billboard>\n";
        assertEquals("C:\\Users\\timbr\\OneDrive\\Uni\\2-1\\CAB302\\test2.xml", billboard.toXML("C:\\Users\\timbr\\OneDrive\\Uni\\2-1\\CAB302\\test2.xml"));
    }
}