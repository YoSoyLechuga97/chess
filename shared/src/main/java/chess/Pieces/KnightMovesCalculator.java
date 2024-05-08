package chess.Pieces;

import chess.ChessMove;
import chess.PiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PiecesMovesCalculator {
    //Can only move in 'L' patterns (change of 2, change of 1)
    //Check position to make sure not same piece where you want to go
    //Clockwise starting with up 2 over 1, then up 1 over 2, down 1 over 2, down 2 over 1, etc.
    private PiecesMovesCalculator piece = null;

    public KnightMovesCalculator(PiecesMovesCalculator chessPiece){
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
                //Up 2 Right 1
                case 0:
                    row += 2;
                    col += 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Up 1 and Right 2
                case 1:
                    row += 1;
                    col += 2;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Down 1 Right 2
                case 2:
                    row -= 1;
                    col += 2;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Down 2 Right 1
                case 3:
                    row -= 2;
                    col += 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Down 2 Left 1
                case 4:
                    row -= 2;
                    col -= 1;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Down 1 Left 2
                case 5:
                    row -= 1;
                    col -= 2;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Up 1 Left 2
                case 6:
                    row += 1;
                    col -= 2;
                    newMove = CheckMove(piece, row, col, canMove);
                    if (newMove != null) {
                        movePositions.add(newMove);
                    }
                    break;

                //Up 2 Left 1
                case 7:
                    row += 2;
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