package com.academy.solid.nie.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

/**
 * Provides methods for the player.
 */
public interface Player {
    /**
     * Marks the connection of the player to the game.
     *
     * @param serverSocket represent single socket on the server
     * @throws IOException If something goes wrong on the server.
     */
    void register(ServerSocket serverSocket) throws IOException;

    /**
     * Informs player based on provided message.
     *
     * @param s represents message
     */
    void inform(String s);

    /**
     * Provides ships to the player.
     *
     * @return String representation of the ships
     * @throws IOException when cannot provide the ships.
     */
    String provideShips() throws IOException;

    /**
     * Makes a move.
     *
     * @return String representation of the single move.
     * @throws IOException when cannot perform a move.
     */
    String makeMove();

    /**
     * @param move last move of current player
     * @return true when current player should shoot again
     */
    boolean shallPlayersBeChanged(String move);

    /**
     * @return true when last move sunk ship
     */
    boolean wasSunk();

    /**
     * @return true when last move has won the game
     */
    boolean isGameOver();

    String getPlayerIP();

    List<String> getMessages() throws IOException;

    void setPlayerDatabaseId(int id);

    int getPlayerDatabaseId();
}
