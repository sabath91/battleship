package com.academy.solid.nie.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table (name = "statement")
public class Statement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "statement_id")
  private int id;
  @Column(name = "text")
  private String text;

  @ManyToOne
  @JoinColumn(name = "transcript_id")
  private Transcript transcript;

  public Statement() {
  }

  public Statement(final String text) {
    this.text = text;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public Transcript getTranscript() {
    return transcript;
  }

  public void setTranscript(final Transcript transcript) {
    this.transcript = transcript;
  }

  @Override
  public String toString() {
    return "Statement{" +
        "id=" + id +
        ", text='" + text + '\'' +
        ", transcript=" + transcript +
        '}';
  }
}
