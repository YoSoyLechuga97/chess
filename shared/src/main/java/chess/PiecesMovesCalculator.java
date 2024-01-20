package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PiecesMovesCalculator {

    private ChessPiece.PieceType pieceType;
    private ChessBoard board;
    private ChessPosition position;

    public ChessPiece.PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(ChessPiece.PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessPosition getPosition() {
        return position;
    }

    public void setPosition(ChessPosition position) {
        this.position = position;
    }

    public PiecesMovesCalculator(ChessBoard board, ChessPosition position, ChessPiece.PieceType piece){
        this.pieceType = piece;
        this.board = board;
        this.position = position;
    }
    public Collection<ChessMove> pieceMoves(){
        switch (pieceType) {
            case KING:
                throw new RuntimeException("Not implemented");
                //break;
            case QUEEN:
                throw new RuntimeException("Not implemented");
                //break;
            case BISHOP:
                System.out.println("This is a bishop piece.");
                //Find out how to import BishopMovesCalculator class
                break;
            case KNIGHT:
                throw new RuntimeException("Not implemented");
                //break;
            case ROOK:
                throw new RuntimeException("Not implemented");
                //break;
            case PAWN:
                throw new RuntimeException("Not implemented");
                //break;


        }
        return new ArrayList<>();
    }
}
