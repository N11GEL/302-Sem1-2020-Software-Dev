package Viewer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectTest {
    Connect c;

    @BeforeEach
    void buildConnect(){
        c = new Connect();
    }

    @Test
    void serverConnectTest() throws IOException {
        assertDoesNotThrow(() -> {
        c.serverConnect();});
    }
}
