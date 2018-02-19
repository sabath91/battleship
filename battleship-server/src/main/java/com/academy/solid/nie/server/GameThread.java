package com.academy.solid.nie.server;

import com.academy.solid.nie.server.config.HibernateConfig;
import com.academy.solid.nie.server.entity.GameEntity;
import com.academy.solid.nie.server.entity.PlayerEntity;
import com.academy.solid.nie.server.entity.Transcript;
import org.hibernate.Session;

import java.io.IOException;
import java.util.logging.Logger;

class GameThread implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(GameThread.class.getName());
  private int port;


  GameThread(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    try {
      GameInitializer gameInitializer;
      Player first = new NetPlayer();
      Player second = new NetPlayer();
      gameInitializer = new ServerGameInitializer(first, second, port);
      gameInitializer.initializeGame();

      saveClients(first, second);

      Game game = new Game(first, second);
      while (!game.isGameOver()) {
        game.play();
      }
      LOGGER.info("Game over");
    } catch (IOException e) {
      LOGGER.warning(e.getMessage());
    }
  }

  private void saveClients(final Player first, final Player second) {
    Transcript firstPlayerTranscript = new Transcript();
    Transcript secondPlayerTranscript = new Transcript();

    PlayerEntity playerOne = new PlayerEntity(first.getPlayerIP(), firstPlayerTranscript);
    PlayerEntity playerTwo = new PlayerEntity(second.getPlayerIP(), secondPlayerTranscript);

    first.setTranscript(firstPlayerTranscript);
    second.setTranscript(firstPlayerTranscript);


    final Session session = HibernateConfig.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    GameEntity gameEntity = new GameEntity();
    gameEntity.addPlayer(playerOne);
    gameEntity.addPlayer(playerTwo);
    session.save(gameEntity);
    session.getTransaction().commit();
    session.close();

  }


}
