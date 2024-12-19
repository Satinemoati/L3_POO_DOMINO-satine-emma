package fr.pantheonsorbonne.miage;

import java.io.*;
import java.net.Socket;

public abstract class DominoNetworkEngine {

    protected BufferedReader in;
    protected PrintWriter out;

    public DominoNetworkEngine(Socket socket) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    protected void sendMessage(String message) {
        out.println(message);
    }

    protected String receiveMessage() throws IOException {
        return in.readLine();
    }
}