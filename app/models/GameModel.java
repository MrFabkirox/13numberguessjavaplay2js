package models;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pure business logic of number guess.
 */
public final class GameModel implements Serializable {

 /**
  * Serial.
  */
 private static final long serialVersionUID = 1L;

 /**
  * Current number to guess.
  */
 private int numberToGuess = new Random().nextInt((100 - 1) + 1) + 1;

 /**
  * Current total number of games;
  */
 private int numberOfGames = 0;

 /**
  * Current wins so far.
  */
 private int wins = 0;

 /**
  * Current losses so far.
  */
 private int losses = 0;

 /**
  * Receives a guess and tells if player won.
  * 
  * @param guess
  *            The guess
  * @return Result
  */
 public boolean won(final int guess) {
  return guess == numberToGuess;
 }

 public void newGame() {
  this.numberToGuess = new Random().nextInt((100 - 1) + 1) + 1;
 }

 /**
  * Ser a {@link GameModel} instance into JSON.
  * 
  * @return JSON
  * @throws JsonProcessingException
  */
 public String serialize() throws JsonProcessingException {
  return new ObjectMapper().writeValueAsString(this);
 }

 /**
  * Deser a {@link GameModel} instance from JSON.
  * 
  * @param json
  *            JSON
  * @return The instance
  * @throws JsonParseException
  * @throws JsonMappingException
  * @throws IOException
  */
 public static GameModel deser(final String json) throws JsonParseException,
   JsonMappingException, IOException {
  return new ObjectMapper().readValue(json, GameModel.class);
 }

 public int getNumberToGuess() {
  return numberToGuess;
 }

 public void setNumberToGuess(int numberToGuess) {
  this.numberToGuess = numberToGuess;
 }

 public int getNumberOfGames() {
  return numberOfGames;
 }

 public void setNumberOfGames(int numberOfGames) {
  this.numberOfGames = numberOfGames;
 }

 public int getWins() {
  return wins;
 }

 public void setWins(int wins) {
  this.wins = wins;
 }

 public int getLosses() {
  return losses;
 }

 public void setLosses(int losses) {
  this.losses = losses;
 }
}
