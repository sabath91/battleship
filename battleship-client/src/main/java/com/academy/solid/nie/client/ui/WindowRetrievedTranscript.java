package com.academy.solid.nie.client.ui;

import com.academy.solid.nie.client.communication.SocketServer;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * represent window for display transcript of game
 */
public class WindowRetrievedTranscript {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private TextArea textArea = new TextArea();
    private ComboBox<String> gamesIndices = new ComboBox<>();
    private SocketServer socketServer;

    public WindowRetrievedTranscript(final SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    /**
     * displays window for user
     */
    public void display() {
        StackPane secondaryLayout = new StackPane();
        gamesIndices.getItems().addAll(getGames());
        textArea.setDisable(Boolean.TRUE);
        secondaryLayout.getChildren().add(gamesIndices);
        secondaryLayout.getChildren().add(textArea);
        Scene secondScene = new Scene(secondaryLayout, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Stage secondStage = new Stage();
        secondStage.sizeToScene();
        secondStage.setScene(secondScene);
        secondStage.show();
    }

    /**
     * appends string to window
     *
     * @param msg message added to textArea in window
     */
    public void append(String msg) {
        textArea.appendText(msg);
    }

    public List<String> getGames(){
        socketServer.sendAskForGamesId("askGameId");
        socketServer.receiveGamesId();

        String s1 ="1";
        String s2 ="2";
        String s3 ="3";
        final ArrayList<String> test = new ArrayList<>();
        test.add(s1);
        test.add(s2);
        test.add(s3);
        return test;
    }
}
