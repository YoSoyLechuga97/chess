package chess.Pieces;

import chess.ChessMove;
import chess.PiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PiecesMovesCalculator{
    //Moves straight up, down, left or right for as long as not interrupted
    //Check in this order: RIGHT, LEFT, DOWN, UP
    private PiecesMovesCalculator piece = null;

    public RookMovesCalculator(PiecesMovesCalculator chessPiece){
        super(chessPiece.getBoard(), chessPiece.getPosition(), chessPiece.getPieceType());
        piece = chessPiece;
    }

    public Collection<ChessMove> pieceMoves(){
        //Initialize Array and all the inputs per array slot
        ArrayList<ChessMove> movePositions = new ArrayList<>();
        ChessMove newMove;

        //Create if statement calculations
        for(int i = 0; i < 4; i++) {
            //Set Wall Bound and Starting location
            int row = piece.getPosition().getRow();
            int col = piece.getPosition().getColumn();
            int[] canMove = {0};

            switch (i) {
                //Right
                case 0:
                    while (canMove[0] == 0) {
                        col += 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Left
                case 1:
                    while (canMove[0] == 0) {
                        col -= 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Down
                case 2:
                    while (canMove[0] == 0) {
                        row -= 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Up
                case 3:
                    while (canMove[0] == 0) {
                        row += 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
            }
        }

        return movePositions;
    }
}