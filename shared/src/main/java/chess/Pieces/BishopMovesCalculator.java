package chess.Pieces;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.PiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {
    //Needs to be able to move in any diagonal until they are blocked
    //Returns a collection of possible moves for the piece
    //Check for mobility in CLOCKWISE FASHION
    private PiecesMovesCalculator piece = null;

    public BishopMovesCalculator(PiecesMovesCalculator chessPiece) {
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
            boolean canMove = true;

            switch (i) {
                //Up and to the Left
                case 0:
                    while (canMove) {
                        row += 1;
                        col += 1;
                        nextPosition = new ChessPosition(row, col);
                        canMove = ValidateMove(piece, nextPosition, newMove);
                        if (canMove) {
                            newMove = new ChessMove(piece.getPosition(), nextPosition, piece.getPieceType());
                            System.out.println("(" + newMove.getEndPosition().getRow() + ", " + newMove.getEndPosition().getColumn() + ")");
                            movePositions.add(newMove);
                        }
                    }

                //Down and to the Left
                case 1:
                    while (canMove) {
                        row -= 1;
                        col += 1;
                        nextPosition = new ChessPosition(row, col);
                        canMove = ValidateMove(piece, nextPosition, newMove);
                        if (canMove) {
                            newMove = new ChessMove(piece.getPosition(), nextPosition, piece.getPieceType());
                            System.out.println("(" + newMove.getEndPosition().getRow() + ", " + newMove.getEndPosition().getColumn() + ")");
                            movePositions.add(newMove);
                        }
                    }

                //Down and to the Right
                case 2:
                    while (canMove) {
                        row -= 1;
                        col -= 1;
                        nextPosition = new ChessPosition(row, col);
                        canMove = ValidateMove(piece, nextPosition, newMove);
                        if (canMove) {
                            newMove = new ChessMove(piece.getPosition(), nextPosition, piece.getPieceType());
                            System.out.println("(" + newMove.getEndPosition().getRow() + ", " + newMove.getEndPosition().getColumn() + ")");
                            movePositions.add(newMove);
                        }
                    }

                //Up and to the Right
                case 3:
                    while (canMove) {
                        row += 1;
                        col -= 1;
                        nextPosition = new ChessPosition(row, col);
                        canMove = ValidateMove(piece, nextPosition, newMove);
                        if (canMove) {
                            newMove = new ChessMove(piece.getPosition(), nextPosition, piece.getPieceType());
                            System.out.println("(" + newMove.getEndPosition().getRow() + ", " + newMove.getEndPosition().getColumn() + ")");
                            movePositions.add(newMove);
                        }
                    }
            }
        }

        return movePositions;
    }

    public boolean ValidateMove(PiecesMovesCalculator piece, ChessPosition nextPosition, ChessMove newMove){
        if(nextPosition.getColumn() > 8 || nextPosition.getRow() > 8 || nextPosition.getRow() < 1 || nextPosition.getColumn() < 1) {
            return false;
        }
        if(piece.getBoard().getPiece(nextPosition) != null) {
            //We will check for capture later
            System.out.println("Not Null");
            return false;
        }
        else {
            return true;
        }
    }

}


