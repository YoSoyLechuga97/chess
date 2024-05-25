package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteusername, String blackUsername, String gameName, ChessGame game) {}
