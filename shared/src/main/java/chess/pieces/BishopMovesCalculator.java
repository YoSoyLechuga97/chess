package chess.pieces;

import chess.ChessMove;
import chess.PiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PiecesMovesCalculator {
    //Needs to be able to move in any diagonal until they are blocked
    //Returns a collection of possible moves for the piece
    //Check for mobility in CLOCKWISE FASHION
    private PiecesMovesCalculator piece = null;

    public BishopMovesCalculator(PiecesMovesCalculator chessPiece) {
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
                //Up and to the Right
                case 0:
                    while (canMove[0] == 0) {
                        row += 1;
                        col += 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
                    break;

                //Down and to the Right
                case 1:
                    while (canMove[0] == 0) {
                        row -= 1;
                        col += 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
                    break;

                //Down and to the Left
                case 2:
                    while (canMove[0] == 0) {
                        row -= 1;
                        col -= 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
                    break;

                //Up and to the Left
                case 3:
                    while (canMove[0] == 0) {
                        row += 1;
                        col -= 1;
                        newMove = checkMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }
                    break;
            }
        }

        return movePositions;
    }
}