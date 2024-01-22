package chess;

import chess.Pieces.BishopMovesCalculator;
import chess.Pieces.KingMovesCalculator;
import chess.Pieces.KnightMovesCalculator;

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
                KingMovesCalculator kingPiece = new KingMovesCalculator(this);
                return kingPiece.pieceMoves();
            case QUEEN:
                throw new RuntimeException("Not implemented");
                //break;
            case BISHOP:
                //Call Bishop Child Class
                BishopMovesCalculator bishopPiece = new BishopMovesCalculator(this);
                return bishopPiece.pieceMoves();
            case KNIGHT:
                KnightMovesCalculator knightPiece = new KnightMovesCalculator(this);
                return knightPiece.pieceMoves();
            case ROOK:
                throw new RuntimeException("Not implemented");
                //break;
            case PAWN:
                throw new RuntimeException("Not implemented");
                //break;


        }
        return new ArrayList<>();
    }

    //Do Move
    public ChessMove CheckMove(PiecesMovesCalculator piece, int row, int col, int[] canMove) {
        ChessPosition nextPosition = new ChessPosition(row, col);
        ChessMove newMove;
        canMove[0] = ValidateMove(piece, nextPosition);
        if (canMove[0] == 0 || canMove[0] == 1) {
            newMove = new ChessMove(piece.getPosition(), nextPosition, piece.getPieceType());
            System.out.println("(" + newMove.getEndPosition().getRow() + ", " + newMove.getEndPosition().getColumn() + ")");
            return newMove;
        }
        return null;
    }

    //Validate that the place a piece wants to move is allowed
    public int ValidateMove(PiecesMovesCalculator piece, ChessPosition nextPosition){
        if(nextPosition.getColumn() > 8 || nextPosition.getRow() > 8 || nextPosition.getRow() < 1 || nextPosition.getColumn() < 1) {
            return 2;
        }
        if(piece.getBoard().getPiece(nextPosition) != null) {
            //Check to see if occupied space is capturable
            if (CheckToCapture(piece, nextPosition)) {
                return 1;
            } else {
                return 2;
            }
        }
        else {
            return 0;
        }
    }

    //Check if occupied space can be captured
    public boolean CheckToCapture(PiecesMovesCalculator piece, ChessPosition nextPosition){
        //Check team colors
        if (piece.getBoard().getPiece(nextPosition).getTeamColor() != piece.getBoard().getPiece(piece.position).getTeamColor()){
            return true;
        } else {
            return false;
        }
    }

}
