package com.academy.solid.nie.client.ui;

import com.academy.solid.nie.client.communication.SocketServer;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

/**
 * represent window for display transcript of game
 */
public class WindowRetrievedTranscript {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private TextArea textArea = new TextArea();
    private ComboBox<String> gamesIndices = new ComboBox<>();
    private ComboBox<String> availableTranscripts = new ComboBox<>();
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
//        textArea.setDisable(Boolean.TRUE);
        secondaryLayout.getChildren().addAll(gamesIndices);

        final String[] selectedId = {""};
        gamesIndices.setOnAction((event -> {
            selectedId[0] = gamesIndices.getSelectionModel().getSelectedItem();
            gamesIndices.setVisible(false);
            availableTranscripts.getItems().addAll("Player1", "Player2");
            secondaryLayout.getChildren().addAll(availableTranscripts);
        }));

        availableTranscripts.setOnAction((event -> {
            final String player = availableTranscripts.getSelectionModel().getSelectedItem();
            socketServer.askDatabase(selectedId[0]+","+player);
            availableTranscripts.setVisible(false);
            secondaryLayout.getChildren().add(textArea);
            showTranscript(socketServer.receiveDatabaseResponse());
        }));


        Scene secondScene = new Scene(secondaryLayout, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Stage secondStage = new Stage();
        secondStage.sizeToScene();
        secondStage.setScene(secondScene);
        secondStage.show();
    }

    private void showTranscript(final List<String> strings) {
        strings.forEach(s -> textArea.appendText(s + System.lineSeparator()));
    }

    public List<String> getGames(){
        socketServer.askDatabase("askGameId");
        return socketServer.receiveDatabaseResponse();
    }
}
