package com.academy.solid.nie.client.communication;

import lombok.Builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of ShipClient interface.
 * Allows you to send and receive information from the server.
 *
 * @since 1.0.1
 */
@Builder
public final class SocketClient implements ShipClient {
    private static final Logger LOGGER = Logger.getLogger(SocketClient.class.getName());
    private static final int PORT_NUMBER = 8081;
    private String ip;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String enemyShips;

    @Override
    public boolean receiveServerInitialMessage() throws IOException {
        String fromServer = in.readLine();
        boolean firstPlayer = false;
        while (!("Provide ships").equals(fromServer)) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Server: " + fromServer);
            }
            fromServer = in.readLine();
            if ("Game has started. 1".equals(fromServer)) {
                firstPlayer = true;
            }
        }
        return firstPlayer;
    }

    @Override
    public void send(final String allShips) throws IOException {
        out.println(allShips);
    }

    @Override
    public String getEnemyShips() {
        try {
            return in.readLine();
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return null;
    }

    @Override
    public void sendGameOverToOpponent() {
        out.println("Q");
    }

    @Override
    public String receive(){
        try {
            return in.readLine();
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return null;
    }

}
