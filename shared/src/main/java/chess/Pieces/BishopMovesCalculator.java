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
        ChessMove newMove;
        ChessPosition currPosition = piece.getPosition();
        ChessPosition nextPosition;
        ChessPiece.PieceType pieceType = piece.getPieceType();
        System.out.println("Starting position: " + piece.getPosition().getRow() + ", " + piece.getPosition().getColumn());

        //Create if statement calculations
        //Set Wall Bound and Starting location
        int row = piece.getPosition().getRow();
        int col = piece.getPosition().getColumn();
        int bound;
        if (row > col){
            bound = row;
        } else {
            bound = col;
        }

        for(int i = bound; i < 8; i++) {
            row += 1;
            col += 1;
            nextPosition = new ChessPosition(row, col);

            if(piece.getBoard().getPiece(nextPosition) != null) {
                //We will check for capture later
                System.out.println("Not Null");
            }
            else {
                newMove = new ChessMove(currPosition, nextPosition, pieceType);
                System.out.println("(" + newMove.getEndPosition().getRow() + ", " + newMove.getEndPosition().getColumn()+ ")");
                movePositions.add(newMove);
            }
        }
        return movePositions;
    }

    private static void addPosition(ArrayList<ArrayList<Integer>> possiblePositions, int x, int y) {
        ArrayList<Integer> newPosition = new ArrayList<>();
        newPosition.add(x);
        newPosition.add(y);
        possiblePositions.add(newPosition);
    }

}


