package com.academy.solid.nie.server;

import com.academy.solid.nie.client.communication.SocketServer;
import com.academy.solid.nie.server.config.HibernateConfig;
import com.academy.solid.nie.server.entity.GameEntity;
import com.academy.solid.nie.server.entity.PlayerEntity;
import com.academy.solid.nie.server.entity.Transcript;
import org.hibernate.Session;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
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
      new Thread(()-> dealWithTranscript(first), "Deal with transcript").start();
      new Thread(()-> dealWithTranscript(second), "Deal with transcript").start();

      LOGGER.info("Game over");
    } catch (IOException e) {
      LOGGER.warning(e.getMessage());
    }
  }

  private void dealWithTranscript(final Player player) {

      if(player.makeMove().equals("askGameId")) {
        final String gamesIds = getGamesIds();
        player.inform(gamesIds);
        final String identifier = player.makeMove();
        final String userTranscript = getTranscript(identifier);
        player.inform(userTranscript);
        LOGGER.info("TRANSCRIPT SEND TO USER");
      }
  }

  private String getTranscript(final String identifier) {
    final String[] divided = identifier.split(",");
    final int gameId = Integer.valueOf(divided[0]);
    final String playerIdentifier = divided[1];

    System.out.println(gameId  +"---> " +playerIdentifier);

    final Session session = HibernateConfig.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    TypedQuery<PlayerEntity> playersQuery = session.createQuery
        ("from PlayerEntity where game.id = :p1", PlayerEntity.class);
    playersQuery.setParameter("p1", gameId);

    final List<PlayerEntity> resultList = playersQuery.getResultList();
    int transcriptId = 0;
    int firstTranscriptAtList = resultList.get(0).getTranscript().getId();
    int secondTranscriptAtList = resultList.get(1).getTranscript().getId();

    session.getTransaction().commit();
    session.close();

    if(playerIdentifier.equals("Player1")){
      if(firstTranscriptAtList<secondTranscriptAtList){
        transcriptId = firstTranscriptAtList;
      }else {
        transcriptId = secondTranscriptAtList;
      }
    }
    if(playerIdentifier.equals("Player2")){
      if(firstTranscriptAtList<secondTranscriptAtList){
        transcriptId = secondTranscriptAtList;
      }else {
        transcriptId = firstTranscriptAtList;
      }
    }

    return getStatements(transcriptId);
  }

  private String getStatements(int transcriptId){


    final Session session = HibernateConfig.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    TypedQuery<String> query = session.createQuery("SELECT text FROM Statement where transcript.id = :transId", String.class);
    query.setParameter("transId", transcriptId);
    final List<String> resultList = query.getResultList();
    StringBuilder sb = new StringBuilder();
    for(String s : resultList){
      sb.append(s).append(",");
    }
    session.close();
    return sb.toString();
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
