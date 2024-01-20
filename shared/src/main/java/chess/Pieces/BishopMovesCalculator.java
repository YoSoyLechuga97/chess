package chess.Pieces;

import chess.ChessMove;
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
        return new ArrayList<>();
    }

}
