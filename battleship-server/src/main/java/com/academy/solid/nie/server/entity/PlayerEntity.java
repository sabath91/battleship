package com.academy.solid.nie.server.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "player")
public class PlayerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "player_id")
  private int id;

  @Column(name = "identity")
  private String identity;

  @ManyToOne(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER)
  @JoinColumn(name = "game_id")
  private GameEntity game;

  @OneToOne(fetch = FetchType.EAGER,
      cascade = CascadeType.ALL)
  @JoinColumn(name = "transcript_id")
  private Transcript transcript;

  @Column(name = "is_winner")
  private boolean IsWinner;

  public PlayerEntity(final String identity, final Transcript transcript) {
    this.identity = identity;
    this.transcript = transcript;
  }

  public PlayerEntity(final String identity) {
    this.identity = identity;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(final String identity) {
    this.identity = identity;
  }

  public GameEntity getGameEntity() {
    return game;
  }

  public void setGameEntity(final GameEntity game) {
    this.game = game;
  }

  public Transcript getTranscript() {
    return transcript;
  }

  public void setTranscript(final Transcript transcript) {
    this.transcript = transcript;
  }

  public boolean isWinner() {
    return IsWinner;
  }

  public void setWinner(final boolean winner) {
    IsWinner = winner;
  }


}
