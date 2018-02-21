package com.academy.solid.nie.client.communication;

import java.io.IOException;

/**
 * List of methods to the client site.
 * The server client is one player in the game.
 */
interface ShipClient {
    /**
     * Starts the game.
     * Connects to a specific ip and a specific port.
     * The server must be previously started.
     *
     * @throws IOException
     */
    boolean receiveServerInitialMessage() throws IOException;

    /**
     * Sends the positions of all ships on the board.
     * After sending the move, the method waits for the opponent's answer.
     *
     * @param allShips as String
     * @throws IOException
     */
    void send(String allShips) throws IOException;

    /**
     * Gets the positions of the opponent's ships.
     *
     * @return
     */
    String getEnemyShips();

    /**
     * Sends the opponent a message about the end of the game.
     * If the opponent got such a message it means that he has lost the game.
     */
    void sendGameOverToOpponent();

    String receive();

}
