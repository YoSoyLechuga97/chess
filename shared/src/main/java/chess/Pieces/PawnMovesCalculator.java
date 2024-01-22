package chess.Pieces;

import chess.ChessMove;
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

        //Create if statement calculations
        for(int i = 0; i < 8; i++) {
            //Starting location
            int row = piece.getPosition().getRow();
            int col = piece.getPosition().getColumn();
            int[] canMove = {0};

            switch (i) {
                //Up
                case 0:
                    row += 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Up and Right
                case 1:
                    row += 1;
                    col += 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Right
                case 2:
                    col += 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Down and to the Right
                case 3:
                    row -= 1;
                    col += 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Down
                case 4:
                    row -= 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Down and to the Left
                case 5:
                    row -= 1;
                    col -= 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Left
                case 6:
                    col -= 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Up and to the Left
                case 7:
                    row += 1;
                    col -= 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;
            }
        }

        return movePositions;
    }

}
