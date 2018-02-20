package com.academy.solid.nie.server;

import java.util.stream.IntStream;

/**
 * Main class for the game.
 */
public final class Main {
    private static final int DEFAULT_PORT = 9970;
    private static final int NUMBER_OF_GAMES = 10;

    private Main() {
    }

    /**
     * Creates server here.
     *
     * @param args represents input
     */
    public static void main(final String[] args) {
        int firstPort = DEFAULT_PORT;
        if (args.length > 0) {
            firstPort = Integer.parseInt(args[0]);
        }
        int lastPort = firstPort + NUMBER_OF_GAMES;
        IntStream.rangeClosed(firstPort, lastPort).forEach(i -> new Thread(new GameThread(i)).start());
    }
}
