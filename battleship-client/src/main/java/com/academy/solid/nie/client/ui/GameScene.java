package com.academy.solid.nie.client.ui;


import com.academy.solid.nie.client.communication.SocketServer;
import com.academy.solid.nie.client.language.Message;
import com.academy.solid.nie.client.language.MessageProviderImpl;
import com.academy.solid.nie.client.output.Output;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Class represents Game UI.
 */
class GameScene extends Application implements Runnable {
    private static final int DEFAULT_ROOT_WIDTH = 500;
    private static final int DEFAULT_ROOT_HEIGHT = 1000;
    private static final int DEFAULT_SPACING = 50;
    private Output output;
    private Board enemyBoard;
    private Board playerBoard;
    private SocketServer socketServer;
    private ShipPlacer shipPlacer;
    private Semaphore waitForSending = new Semaphore(0);
    private Semaphore myTurn = new Semaphore(0);
    private String playerName;

    GameScene(final SocketServer socketServer, final Output output, String playerName) throws IOException {
        this.socketServer = socketServer;
        this.playerName = playerName;
    }


    private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(DEFAULT_ROOT_WIDTH, DEFAULT_ROOT_HEIGHT);
        Button randomPlacementButton = createRandomButton();
        root.setBottom(randomPlacementButton);
        enemyBoard = new Board(true);
        enemyBoard.initialize(getMove());
        playerBoard = new Board(false);
        shipPlacer = new ShipPlacer(enemyBoard, playerBoard, socketServer, myTurn, socketServer.isFirstPlayer(), waitForSending);
        playerBoard.initialize(shipPlacer.setUpPlayerShips());
        playerBoard.addLabel(MessageProviderImpl.getCommunicate(Message.YOUR_BOARD));
        enemyBoard.addLabel(MessageProviderImpl.getCommunicate(Message.ENEMY_BOARD));
        VBox vbox = new VBox(DEFAULT_SPACING, enemyBoard.getBoardFX(), playerBoard.getBoardFX());
        vbox.setAlignment(Pos.CENTER);
        root.setCenter(vbox);
        return root;
    }

    private Button createRandomButton() {
        Button button = new Button();
        button.setText(MessageProviderImpl.getCommunicate(Message.RANDOM));
        button.setOnMouseClicked(e -> shipPlacer.placeShipsRandomly());
        return button;
    }

    private EventHandler<MouseEvent> getMove() {
        return event -> {
            if (playerBoard.areAllShipsSunk()) {
                new WindowDisplayer(MessageProviderImpl
                        .getCommunicate(Message.LOSE))
                        .withButtonWhoExitSystem().display();
                socketServer.sendGameOverToOpponent();
            } else {
                Cell cell = (Cell) event.getSource();
                if (!myTurn.tryAcquire() || !shipPlacer.areAllShipsPlaced()) {
                    informAboutCurrentTurn();
                    return;
                }
                handlePlayersMove(cell);
            }
        };
    }

    private void informAboutCurrentTurn() {
        new WindowDisplayer(MessageProviderImpl.getCommunicate(Message.NOT_YOUR_TURN))
                .withButtonWhoExitThisWindow()
                .withTitle(String.format("%s - %s", MessageProviderImpl.getCommunicate(Message.TITLE), playerName))
                .display();
    }

    private void handlePlayersMove(final Cell cell) {
        boolean isMyTurn = cell.shoot();
        if (!isMyTurn) {
            waitForSending.release();
        } else {
            myTurn.release();
        }
        socketServer.sendPlayerMove(cell.toString());
        if (enemyBoard.areAllShipsSunk()) {
            new WindowDisplayer(MessageProviderImpl
                    .getCommunicate(Message.WIN))
                    .withButtonWhoExitSystem().display();
            socketServer.sendGameOverToOpponent();
        }
        if (enemyBoard.isShipSunken(cell.getShip())) {
            enemyBoard.markShipAsSunken(cell.getShip());
        }
    }

    void start() {
        start(new Stage());
    }

    @Override
    public void start(final Stage primaryStage) {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle(String.format("%s - %s", MessageProviderImpl.getCommunicate(Message.TITLE), playerName));
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void run() {
        while (true) {
            try {
                waitForSending.acquire();
            } catch (InterruptedException e) {
                output.send(e.getMessage());
            }
            try {
                playerBoard.makeMoves(socketServer.receiveMoves());
            } catch (IOException e) {
                output.send(e.getMessage());
            }
            if (playerBoard.isMyTurn()) {
                waitForSending.release();
            } else {
                myTurn.release();
            }
        }
    }
}
