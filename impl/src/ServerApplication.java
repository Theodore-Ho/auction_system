import socket.Server;

import java.io.IOException;

public class ServerApplication {
    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.run();
    }
}
