package Viewer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ViewerTest {
    Viewer v;

    @BeforeEach
    void buildViewer(){
        v = new Viewer("", "", "", "" ,"", "");
    }

    @Test
    void generateMessageTest() {
        assertDoesNotThrow(() -> {v.generateMessage("message", "#000000", 0.5, 0.5);});
    }

    @Test
    void generatePictureTest() {
        assertDoesNotThrow(() -> {v.generatePicture("", 0.5);});
    }

    @Test
    void generateInfoMessageTest() {
        assertDoesNotThrow(() -> {v.generateInfoMessage("info", "#000000", 8000);});
    }
}