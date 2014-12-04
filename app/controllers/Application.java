package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import java.io.IOException;

import models.GameModel;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller {

	/**
	  * Game status cookie identifier.
	  */
	 private static final String GAME_COOKIE_ID = "actGame";

	 /**
	  * Home page. Serves the template at the first place. Other action methods
	  * should return pure JSON responses to help Ajax calls from views.
	  */
	 public static Result index() {

	  // Create a new game and store it in session cookie as JSON
	  try {
	   response().setCookie(GAME_COOKIE_ID, new GameModel().serialize());
	  } catch (JsonProcessingException e) {
	   throw new RuntimeException(e);
	  }

	  return ok(views.html.index.render("The game has started"));
	 }

	 /**
	  * A guess.
	  */
	 public static Result guess(final String actGuess) {
	  final int guess;
	  final ObjectNode result = Json.newObject();
	  try {
	   // Parse (validate? :) the guess
	   guess = Integer.valueOf(actGuess).intValue();
	  } catch (final NumberFormatException e) {
	   result.put("message", "Give me an integer");
	   return badRequest(result);
	  }

	  // Deserialize current game, in whatever state it is
	  final GameModel actGame;
	  try {
	   actGame = GameModel.deser(request().cookies().get(GAME_COOKIE_ID)
	     .value());
	  } catch (JsonParseException e) {
	   throw new RuntimeException(e);
	  } catch (JsonMappingException e) {
	   throw new RuntimeException(e);
	  } catch (IOException e) {
	   throw new RuntimeException(e);
	  }

	  actGame.setNumberOfGames(actGame.getNumberOfGames() + 1);
	  final boolean won = actGame.won(guess);
	  result.put("youWon", won);
	  result.put("totalGames", actGame.getNumberOfGames());
	  result.put("wins", actGame.getWins());
	  result.put("losses", actGame.getLosses());

	  if (won) {
	   actGame.setWins(actGame.getWins() + 1);
	   result.put("wins", actGame.getWins());
	   result.put(
	     "message",
	     String.format(
	       "The number was %d indeed, now guessing new number, good luck",
	       guess));
	   actGame.newGame();

	   // Update game status in cookie
	   try {
	    response().setCookie(GAME_COOKIE_ID, actGame.serialize());
	   } catch (JsonProcessingException e) {
	    throw new RuntimeException(e);
	   }
	   return ok(result);
	  } else {
	   actGame.setLosses(actGame.getLosses() + 1);
	   result.put("losses", actGame.getLosses());
	   result.put("message", String.format("Try again with %s",
	     guess > actGame.getNumberToGuess() ? "smaller" : "larger"));

	   // Update game status in cookie
	   try {
	    response().setCookie(GAME_COOKIE_ID, actGame.serialize());
	   } catch (JsonProcessingException e) {
	    throw new RuntimeException(e);
	   }
	   return ok(result);
	  }
	 }

	 /**
	  * Fully reset the game.
	  */
	 public static Result resetGame() {
	  GameModel actGame = new GameModel();
	  final ObjectNode result = Json.newObject();
	  result.put("youWon", false);
	  result.put("totalGames", actGame.getNumberOfGames());
	  result.put("wins", actGame.getWins());
	  result.put("losses", actGame.getLosses());
	  result.put("message", "Game has been reset");

	  // Update game status in cookie
	  try {
	   response().setCookie(GAME_COOKIE_ID, actGame.serialize());
	  } catch (JsonProcessingException e) {
	   throw new RuntimeException(e);
	  }

	  return ok(result);
	 }

	 /**
	  * JavaScript routing.
	  */
	 public static Result javascriptRoutes() {
	  response().setContentType("text/javascript");
	  return ok(Routes.javascriptRouter("jsRoutes",
	    routes.javascript.Application.guess(),
	    routes.javascript.Application.resetGame()));
	 }
	

}
