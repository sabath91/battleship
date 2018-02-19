package com.academy.solid.nie.server;

import com.academy.solid.nie.server.config.HibernateConfig;
import com.academy.solid.nie.server.entity.Statement;
import com.academy.solid.nie.server.entity.Transcript;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class Game {
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
    private final Player first;
    private Player otherPlayer;
    private Player currentPlayer;
    private String move;
    Game(Player first, Player second) {
        this.first = first;
        this.currentPlayer = first;
        this.otherPlayer = second;
    }

    void play() throws IOException {
        log("It is now turn of " + getNameOfCurrentPlayer());
        String yourTurnTranscriptMsg = "Your turn.";
        move = currentPlayer.makeMove();
        log((getNameOfCurrentPlayer()) + " shoot at " + move);
        String youShutTranscriptMsg = "You shot at: " + move;
        saveTranscriptMsg(currentPlayer, yourTurnTranscriptMsg, youShutTranscriptMsg);
        otherPlayer.inform(move);
        String opponentTurnTranscriptMsg = "It is your opponent turn.";
        String opponentShotTranscriptMsg = "Your opponent shot at: " + move;
        saveTranscriptMsg(otherPlayer, opponentTurnTranscriptMsg, opponentShotTranscriptMsg);
        changeCurrentPlayer();
    }

    private void saveTranscriptMsg(Player player, String... msg) {
        final Session session = HibernateConfig.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        final Transcript transcript = player.getTranscript();
        for (final String s : msg) {
            transcript.addStatement(new Statement(s));
        }
        session.update(transcript);
        session.getTransaction().commit();
        session.close();
    }

    private String getNameOfCurrentPlayer() {
        return currentPlayer.equals(first) ? "First Player" : "Second Player";
    }

    private void changeCurrentPlayer() {
        if (otherPlayer.shallPlayersBeChanged(move)) {
            log((getNameOfCurrentPlayer()) + " missed");
            saveTranscriptMsg(currentPlayer, "You missed.");
            Player temp = currentPlayer;
            saveTranscriptMsg(otherPlayer, "Your opponent missed.", "It is your turn");
            currentPlayer = otherPlayer;
            otherPlayer = temp;
        } else {
            log((getNameOfCurrentPlayer()) + " hit ship");
            saveTranscriptMsg(currentPlayer, "You hit a ship");
            saveTranscriptMsg(otherPlayer, "Opponent of yours hit a ship");
            if (otherPlayer.wasSunk()) {
                log("Ship has been sunk");
                saveTranscriptMsg(currentPlayer, "The last shot has sank the ship");
                saveTranscriptMsg(otherPlayer, "The last shot has sank the ship");
            }
            if (otherPlayer.isGameOver()) {
                log(getNameOfCurrentPlayer() + "has won the game");
                saveTranscriptMsg(currentPlayer,"You won");
                saveTranscriptMsg(otherPlayer,"you lost");
            }
        }
    }

    boolean isGameOver() {
        return otherPlayer.isGameOver();
    }

    private void log(String msg) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(msg);
        }
    }
}
