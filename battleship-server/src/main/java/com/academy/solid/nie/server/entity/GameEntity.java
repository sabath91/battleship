package com.academy.solid.nie.server.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game")
public class GameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "game_id")
  private int id;

  @OneToMany(fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      mappedBy = "game")
  private List<PlayerEntity> players;

  public GameEntity() {
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public List<PlayerEntity> getPlayers() {
    return players;
  }

  public void setPlayers(final List<PlayerEntity> players) {
    this.players = players;
  }



  //TODO  LOGIC
  public void addPlayer(PlayerEntity player){
    if(players==null){
      players = new ArrayList<>(2);
    }
    players.add(player);
    player.setGameEntity(this);
  }

}
