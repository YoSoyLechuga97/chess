package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    //Private variables
    private ChessPiece[][] squares =  new ChessPiece[8][8];

    //Constructor
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //Set square at position equal to piece given
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        //Return piece given position
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessBoard resetBoard;
        //Clear board
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares.length; j++) {
                squares[i][j] = null;
            }
        }
        //Fill bottom and top rows
        for (int x = 0; x < 2; x++) {
            int i;
            int p;
            ChessGame.TeamColor color;
            if (x == 0) { //White Team
                i = 0;
                color = ChessGame.TeamColor.WHITE;
            } else { //Black Team
                i = 7;
                color = ChessGame.TeamColor.BLACK;
            }
            for (int j = 0; j < 8; j++) {
                ChessPiece newPiece = null;
                //Add Rooks
                if (j == 0 || j == 7) {
                    newPiece = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                }
                //Add Knights
                if (j == 1 || j == 6) {
                    newPiece = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                }
                //Add Bishops
                if (j == 2 || j == 5) {
                    newPiece = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                }
                //Add Queen
                if (j == 3) {
                    newPiece = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                }
                //Add King
                if (j == 4) {
                    newPiece = new ChessPiece(color, ChessPiece.PieceType.KING);
                }
                squares[i][j] = newPiece;
                //Add Pawn in same row
                if(x == 0) {
                    p = i + 1;
                } else {
                    p = i - 1;
                }
                ChessPiece newPawn = new ChessPiece(color, ChessPiece.PieceType.PAWN);
                squares[p][j] = newPawn;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
