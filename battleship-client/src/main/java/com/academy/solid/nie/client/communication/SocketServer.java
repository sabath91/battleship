package com.academy.solid.nie.client.communication;

import com.academy.solid.nie.client.language.Communicate;
import com.academy.solid.nie.client.language.CommunicateProviderImpl;
import com.academy.solid.nie.client.ui.Point2D;
import com.academy.solid.nie.client.ui.WindowDisplayer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * SocketServer implementation to communicate with the server.
 *
 * @since 1.0.1
 */

public final class SocketServer implements Server {
    private static final Logger LOGGER = Logger.getLogger(SocketServer.class.getName());
    private ShipClient server;
    private String allMoves = "";
    private static final int DEFAULT_PORT_NUMBER = 8081;

    @Override
    public void connect(String ip) {
        try {
            Socket socket = createSocket(ip, DEFAULT_PORT_NUMBER);
            server = SocketClient.builder().
                    ip(ip).socket(socket).
                    out(createPrintWriter(socket)).
                    in(createBufferedReader(socket)).
                    build();
            server.run();
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    @Override
    public void send(final String allShips) {
        LOGGER.info(allShips);
        try {
            server.send(allShips);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    @Override
    public String receiveAllShips() {
        return server.getEnemyShips();
    }

    @Override
    public void sendGameOverToOpponent() {
        server.sendGameOverToOpponent();
    }

    private List<Point2D> receiveAllMovesWithoutSending() {
        allMoves = "";
        ArrayList<Point2D> points = new ArrayList<>();
        String moves = server.getEnemyShips();
        if (moves.equals("Q")) {
            new WindowDisplayer(CommunicateProviderImpl.getCommunicate(Communicate.LOSE))
                    .withButtonWhoExitSystem().display();
        }
        String[] split = moves.split(",;");
        for (String point : split) {
            String[] coordinates = point.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            points.add(Point2D.of(x, y));
        }
        return points;
    }

    @Override
    public void sendPlayerMove(final String move) {
        allMoves += move + ";";
    }

    @Override
    public List<Point2D> receiveEnemyMoves() {
        send(allMoves);
        return receiveAllMovesWithoutSending();
    }

    private Socket createSocket(String inputIp, int inputPortNumber) throws IOException {
        return new Socket(inputIp, inputPortNumber);
    }

    private BufferedReader createBufferedReader(Socket inputSocket) throws IOException {
        return new BufferedReader(createInputStreamReader(inputSocket));
    }

    private InputStreamReader createInputStreamReader(Socket inputSocket) throws IOException {
        return new InputStreamReader(inputSocket.getInputStream(), StandardCharsets.UTF_8);
    }

    private PrintWriter createPrintWriter(Socket inputSocket) throws IOException {
        return new PrintWriter(createOutputStream(inputSocket), true);
    }

    private OutputStreamWriter createOutputStream(Socket inputSocket) throws IOException {
        return new OutputStreamWriter(inputSocket.getOutputStream(), StandardCharsets.UTF_8);
    }

}
