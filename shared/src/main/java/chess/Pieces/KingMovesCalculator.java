package chess.Pieces;

import chess.ChessMove;
import chess.PiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PiecesMovesCalculator {
    //Moves 1 in any direction (any touching square)
    //Don't need to worry about check for this part
    //CLOCKWISE starting with up
    private PiecesMovesCalculator piece = null;

    public KingMovesCalculator(PiecesMovesCalculator chessPiece){
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