package com.academy.solid.nie.server.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "transcript")
public class Transcript {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column (name = "transcript_id")
  private int id;

  @OneToOne(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      mappedBy = "transcript")
  private PlayerEntity player;

  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
  mappedBy = "transcript")
//  @JoinColumn (name = "transcript" , referencedColumnName = "transcript_id")
  private List<Statement> statements;

  public Transcript() {
  }

  public Transcript(final PlayerEntity player) {
    this.player = player;
  }

  public void addStatement(Statement statement){
    if(statements == null){
      statements = new ArrayList<>();
    }
    statements.add(statement);
    statement.setTranscript(this);
  }


}
