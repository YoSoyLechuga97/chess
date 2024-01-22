package chess.Pieces;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.PiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PiecesMovesCalculator {
    //Can only move up (unless this is the first time it has moved)
    //Check row to see if this is the first time the piece has moved
    //Move up 1 if spot isn't blocked
    private PiecesMovesCalculator piece = null;

    public PawnMovesCalculator(PiecesMovesCalculator chessPiece){
        super(chessPiece.getBoard(), chessPiece.getPosition(), chessPiece.getPieceType());
        piece = chessPiece;
    }

    public Collection<ChessMove> pieceMoves(){
        //Initialize Array and all the inputs per array slot
        ArrayList<ChessMove> movePositions = new ArrayList<>();
        ChessMove newMove;

        //Starting location
        int row = piece.getPosition().getRow();
        int col = piece.getPosition().getColumn();
        int[] canMove = {0};

        //Determine Color
        switch (piece.getBoard().getPiece(piece.getPosition()).getTeamColor()) {
            case WHITE:
                //Check moving up
                row += 1;
                newMove = CheckMove(piece, row, col, canMove);
                if (newMove != null) {
                    movePositions.add(newMove);
                    //Check to see if it is the first move for that pawn
                    if (piece.getPosition().getRow() == 2){
                        row += 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
                }
                //Check for capture
                for (int i = 0; i < 2; i++){
                    row = piece.getPosition().getRow() + 1;
                    col = piece.getPosition().getColumn();
                    if (i == 0){
                        col -= 1;
                    } else{
                        col += 1;
                    }
                    newMove = CheckPawnCapture(piece, row, col, canMove);
                    if (newMove != null){
                        movePositions.add(newMove);
                    }
                }
                break;

            case BLACK:
                //Check moving down
                row -= 1;
                newMove = CheckMove(piece, row, col, canMove);
                if (newMove != null) {
                    movePositions.add(newMove);
                    //Check to see if it is the first move for that pawn
                    if (piece.getPosition().getRow() == 7){
                        row -= 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
                }
                //Check for capture
                for (int i = 0; i < 2; i++){
                    row = piece.getPosition().getRow() - 1;
                    col = piece.getPosition().getColumn();
                    if (i == 0){
                        col -= 1;
                    } else{
                        col += 1;
                    }
                    newMove = CheckPawnCapture(piece, row, col, canMove);
                    if (newMove != null){
                        movePositions.add(newMove);
                    }
                }
                break;
        }



        return movePositions;
    }

    public ChessMove CheckPawnCapture(PiecesMovesCalculator piece, int row, int col, int[] canMove) {
        ChessPosition nextPosition = new ChessPosition(row, col);
        ChessMove newMove;
        canMove[0] = ValidatePawnCapture(piece, nextPosition);
        if (canMove[0] == 1) {
            newMove = new ChessMove(piece.getPosition(), nextPosition, piece.getPieceType());
            System.out.println("(" + newMove.getEndPosition().getRow() + ", " + newMove.getEndPosition().getColumn() + ")");
            return newMove;
        }
        return null;
    }

    public  int ValidatePawnCapture(PiecesMovesCalculator piece, ChessPosition nextPosition){
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

}
