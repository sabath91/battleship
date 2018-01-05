package com.epam.solid.nie.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public final class NetPlayer implements Player {
    private static final Logger LOGGER = Logger.getLogger(NetPlayer.class.getName());
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void register(final ServerSocket serverSocket) throws IOException {
        Socket clientSocket = serverSocket.accept();
        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

        out.println("hi. wait.");
        LOGGER.info("registered");
    }

    @Override
    public void inform(final String s) throws IOException {
        out.println(s);
    }

    @Override
    public String provideShips() throws IOException {
        out.println("Provide ships");
        return in.readLine();
    }

    @Override
    public String makeMove() throws IOException {
        return in.readLine();
    }
}
