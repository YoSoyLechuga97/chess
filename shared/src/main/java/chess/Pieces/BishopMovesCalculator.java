package chess.Pieces;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
        System.out.println("Made it to BishopMovesCalculator.");
        //Initialize Array and all the inputs per array slot
        ArrayList<ChessMove> movePositions = new ArrayList<>();
        ChessMove newMove = new ChessMove(piece.getPosition(), piece.getPosition(), piece.getPieceType());
        ChessPosition currPosition = piece.getPosition();
        ChessPosition nextPosition;
        ChessPiece.PieceType pieceType = piece.getPieceType();
        System.out.println("Starting position: " + piece.getPosition().getRow() + ", " + piece.getPosition().getColumn());

        //Create if statement calculations
        for(int i = 0; i < 4; i++) {
            //Set Wall Bound and Starting location
            int row = piece.getPosition().getRow();
            int col = piece.getPosition().getColumn();
            int[] canMove = {0};

            switch (i) {
                //Up and to the Left
                case 0:
                    while (canMove[0] == 0) {
                        row += 1;
                        col += 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                //Down and to the Left
                case 1:
                    while (canMove[0] == 0) {
                        row -= 1;
                        col += 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                //Down and to the Right
                case 2:
                    while (canMove[0] == 0) {
                        row -= 1;
                        col -= 1;
                        newMove = CheckMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.add(newMove);
                        }
                    }

                //Up and to the Right
                case 3:
                    while (canMove[0] == 0) {
                        row += 1;
                        col -= 1;
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


