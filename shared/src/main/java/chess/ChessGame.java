package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    //Variables
    private TeamColor turnColor;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        //Return the color of the team currently playing (turnColor)
        return turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        //set the color of the team currently playing (turnColor)
        turnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //Figure out possible moves for piece (piecesMoves but make sure that it doesn't go into check)
        Collection<ChessMove> allMoves = new ArrayList<>();
        //If no piece return null
        ChessPiece currPiece = board.getPiece(startPosition);
        if (currPiece == null) {
            return null;
        }

        //Determine all possible moves (legal or not)
        allMoves = currPiece.pieceMoves(board, startPosition);

        //Determine if specific allMoves leaves king open for attack (check)
        //Temporarily remove piece from current position
        board.addPiece(startPosition, null);
        ChessMove currMove;
        ChessPiece prevPiece;
        ChessPosition pos;
        Iterator<ChessMove> iterator = allMoves.iterator();

        while(iterator.hasNext()) {
            currMove = iterator.next();
            pos = currMove.getEndPosition(); //Position of next allMoves in list
            prevPiece = board.getPiece(pos); //Save piece that is currently in that position
            board.addPiece(pos, currPiece); //Temporarily allMoves piece to that position
            if (isInCheck(currPiece.getTeamColor())) { //Moving piece here puts king in check -> not valid allMoves
                iterator.remove();
            }
            board.addPiece(pos, prevPiece); //Restore piece that was in board
        }

        //Restore board
        board.addPiece(startPosition, currPiece);

        //Determine if there are valid moves
        if (allMoves == null) {
            return null;
        }
        return allMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //update board to make sure that it shows the piece is in a new spot (learn how invalidMoveException works)
        //Execute new move (provided it's legal)
        //Illegal if: piece cannot move to specified position, leaves king in danger, or it is the other team's turn
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Return true or false
        boolean isCheck = false;
        //Find team king
        ChessPosition kingPosition = new ChessPosition(1, 1);
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition checkPosition = new ChessPosition(i , j);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(checkPosition).getTeamColor() == teamColor) {
                        kingPosition = checkPosition;
                    }
                }
            }
        }

        //Check to see if any of other team's valid moves can attack king
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition checkPosition = new ChessPosition(i , j);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != teamColor) { //enemy piece
                        Collection<ChessMove> pieceMoves = new ArrayList<>();
                        pieceMoves = board.getPiece(checkPosition).pieceMoves(board, checkPosition);

                        for (ChessMove pieceMove : pieceMoves) {
                            ChessPosition endPosition = pieceMove.getEndPosition();
                            if (endPosition.equals(kingPosition)) {
                                isCheck = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return isCheck;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //Return true or false
        boolean isCheckmate = true;
        //Is in check
        boolean isCheck = isInCheck(teamColor);
        if (!isCheck) {
            isCheckmate = false;
            return isCheckmate;
        }
        //Check to see if any piece can move
        if (movesAvailable(teamColor)) {
            isCheckmate = false;
            return isCheckmate;
        }

        return isCheckmate;
    }

    public boolean movesAvailable(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition checkPosition = new ChessPosition(i, j);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() == teamColor) { //friendly piece
                        Collection<ChessMove> pieceMoves = new ArrayList<>();
                        pieceMoves = validMoves(checkPosition);
                        if (pieceMoves != null) { //Valid move that takes king out of check is available
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //Return true or false
        boolean isStalemate = true;

        //Determine king is not in check
        if (isInCheck(teamColor)) {
            isStalemate = false;
            return isStalemate;
        }

        //Determine team cannot move
        if (movesAvailable(teamColor)) {
            isStalemate = false;
            return isStalemate;
        }

        return isStalemate;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //Look up what this is supposed to do (setup game ChessBoard?)
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        //Return current ChessBoard
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turnColor == chessGame.turnColor && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnColor, getBoard());
    }
}
