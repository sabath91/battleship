package com.academy.solid.nie.server;

import com.academy.solid.nie.client.communication.SocketServer;
import com.academy.solid.nie.server.config.HibernateConfig;
import com.academy.solid.nie.server.entity.GameEntity;
import com.academy.solid.nie.server.entity.PlayerEntity;
import com.academy.solid.nie.server.entity.Transcript;
import org.hibernate.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
      new Thread(() -> {
        final String askAboutId = first.makeMove();
        System.out.println(askAboutId);
        getGamesIds();
        //Receive ask for transcript
        //    --> send list of games
        //Choose game
        //    --> send list of players for game
        //Choose player
        //    --> send transcript
      }, "Deal With Transcipt").start();

      new Thread(() -> {
        final String askAboutId = second.makeMove();
        System.out.println(askAboutId);
        getGamesIds();
        //Receive ask for transcript
        //    --> send list of games
        //Choose game
        //    --> send list of players for game
        //Choose player
        //    --> send transcript
      }, "Deal With Transcipt").start();


      LOGGER.info("Game over");
    } catch (IOException e) {
      LOGGER.warning(e.getMessage());
    }
  }

  private String getGamesIds() {

    final Session session = HibernateConfig.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    final List<Integer> gamesId = session.createQuery("SELECT id from GameEntity", Integer.class).getResultList();

    StringBuilder result = new StringBuilder();
    for(Integer i : gamesId){
      result.append(i).append(",");
    }

    session.getTransaction().commit();
    session.close();
    return result.toString();
  }

  private void saveClients(final Player first, final Player second) {
    Transcript firstPlayerTranscript = new Transcript();
    Transcript secondPlayerTranscript = new Transcript();

    PlayerEntity playerOne = new PlayerEntity(first.getPlayerIP(), firstPlayerTranscript);
    PlayerEntity playerTwo = new PlayerEntity(second.getPlayerIP(), secondPlayerTranscript);



    final Session session = HibernateConfig.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    GameEntity gameEntity = new GameEntity();
    gameEntity.addPlayer(playerOne);
    gameEntity.addPlayer(playerTwo);
    session.save(gameEntity);
    session.getTransaction().commit();
    first.setPlayerDatabaseId(playerOne.getId());
    second.setPlayerDatabaseId(playerTwo.getId());
    System.out.println(playerOne.getId() + "    " + playerTwo.getId());
    session.close();

  }


}
