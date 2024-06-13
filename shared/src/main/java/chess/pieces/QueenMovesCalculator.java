package chess.pieces;

import chess.ChessMove;
import chess.PiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends PiecesMovesCalculator {
    //Clockwise starting with forward
    private PiecesMovesCalculator piece = null;

    public QueenMovesCalculator(PiecesMovesCalculator chessPiece){
        super(chessPiece.getBoard(), chessPiece.getPosition(), chessPiece.getPieceType());
        piece = chessPiece;
    }

    public Collection<ChessMove> pieceMoves(){
        //Initialize Array and all the inputs per array slot
        ArrayList<ChessMove> movePositions = new ArrayList<>();
        ChessMove newMove;
        //Create if statement calculations
        for(int i = 0; i < 8; i++) {
            //Set Wall Bound and Starting location
            int row = piece.getPosition().getRow();
            int col = piece.getPosition().getColumn();
            int[] canMove = {0};

            switch (i) {
                //Up
                case 0:
                    while (canMove[0] == 0) {
                        row += 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Up and to the Right
                case 1:
                    while (canMove[0] == 0) {
                        row += 1;
                        col += 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Right
                case 2:
                    while (canMove[0] == 0) {
                        col += 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Down and to the Right
                case 3:
                    while (canMove[0] == 0) {
                        row -= 1;
                        col += 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Down
                case 4:
                    while (canMove[0] == 0) {
                        row -= 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Down and to the Left
                case 5:
                    while (canMove[0] == 0) {
                        row -= 1;
                        col -= 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Left
                case 6:
                    while (canMove[0] == 0) {
                        col -= 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                    //Up and to the Left
                case 7:
                    while (canMove[0] == 0) {
                        row += 1;
                        col -= 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
            }
        }
        return movePositions;
    }
}
