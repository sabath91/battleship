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
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

/**
 * Class represents Game UI.
 */
class GameScene extends Application implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(GameScene.class.getName());
    private static final int DEFAULT_ROOT_WIDTH = 650;
    private static final int DEFAULT_ROOT_HEIGHT = 950;
    private static final int DEFAULT_SPACING = 0;
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
        this.output = output;
    }


    private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(DEFAULT_ROOT_WIDTH, DEFAULT_ROOT_HEIGHT);
        Button randomPlacementButton = createRandomButton();
        root.setBottom(randomPlacementButton);
        enemyBoard = new Board(true, output);
        enemyBoard.initialize(getMove());
        playerBoard = new Board(false, output);
        shipPlacer = new ShipPlacer(enemyBoard, playerBoard, socketServer, myTurn, socketServer.isFirstPlayer(),
                waitForSending, output);
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
            if (playerBoard.areAllShipsSunk() && shipPlacer.areAllShipsPlaced()) {
                new WindowDisplayer(MessageProviderImpl
                        .getCommunicate(Message.LOSE))
                        .withButtonWhoExitSystem().display();
//                socketServer.sendGameOverToOpponent();
                new WindowRetrievedTranscript(socketServer).display();
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
        output.send("You shot at: " + cell.toString());
        if (!isMyTurn) {
            waitForSending.release();
            output.send("You missed\nIt is your opponent turn.");
        } else {
            myTurn.release();
            output.send("You hit a ship.");
        }
        socketServer.sendPlayerMove(cell.toString());
        if (enemyBoard.areAllShipsSunk()) {
            new WindowDisplayer(MessageProviderImpl
                    .getCommunicate(Message.WIN))
                    .withButtonWhoExitSystem().display();
            //New window with ability to choose transcript
            //      --> drop down menu
            //      -->
            new WindowRetrievedTranscript(socketServer).display();
        }
        if (enemyBoard.isShipSunken(cell.getShip())) {
            enemyBoard.markShipAsSunken(cell.getShip());
        }
        if (enemyBoard.areAllShipsSunk()) {
            output.send("You won.");
        } else {
            if (isMyTurn) {
                output.send("Your turn.");
            }
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
                LOGGER.warning(e.getMessage());
            }
            try {
                List<Point2D> points = socketServer.receiveMoves();
                StringBuilder stringBuilder = new StringBuilder("Your opponent shot at: ");
                points.forEach(stringBuilder::append);
                output.send(stringBuilder.toString());
                playerBoard.makeMoves(points);
                if (playerBoard.isMyTurn()) {
                    output.send("Opponent of yours hit a ship");
                }
                playerBoard.markSunkenShipOnPlayerBoard();
            } catch (IOException e) {
                LOGGER.warning(e.getMessage());
            }
            if (playerBoard.isMyTurn()) {
                if (playerBoard.areAllShipsSunk()) {
                    output.send("You lost.");
                } else {
                    output.send("It is your turn.");
                    waitForSending.release();
                }
            } else {
                output.send("Your opponent missed.\nIt is your turn.");
                myTurn.release();
            }
        }
    }
}
