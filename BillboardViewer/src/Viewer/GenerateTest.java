package Viewer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GenerateTest {
    Generate g;

    @BeforeEach
    void buildGen(){
        g = new Generate();
    }

    @Test
    void genTest() {
        assertDoesNotThrow(() -> {
        JFrame frame = new JFrame("Billboard Viewer");
        Viewer billboard = new Viewer(Viewer.billboardData[2], Viewer.billboardData[3], Viewer.billboardData[4], Viewer.billboardData[5], Viewer.billboardData[6], Viewer.billboardData[7]);
        frame.getContentPane().setBackground(Color.BLACK);
        GridLayout layout = new GridLayout(3, 1);
        frame.setLayout(layout);
        frame.add(billboard.generateMessage("Test", "#000000", 0.75, 0.28));});
    }
}