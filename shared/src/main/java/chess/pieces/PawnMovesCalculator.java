package chess.pieces;

import chess.*;

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
        ArrayList<ChessMove> newMove;
        boolean isPromotion = false;

        //Starting location
        int row = piece.getPosition().getRow();
        int col = piece.getPosition().getColumn();
        int[] canMove = {0};

        //Determine Color
        switch (piece.getBoard().getPiece(piece.getPosition()).getTeamColor()) {
            case WHITE:
                //Check moving up
                row += 1;
                newMove = checkPawnMove(piece, row, col, canMove);
                if (newMove != null) {
                    movePositions.addAll(newMove);
                    //Check to see if it is the first move for that pawn
                    if (piece.getPosition().getRow() == 2){
                        row += 1;
                        newMove = checkPawnMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.addAll(newMove);
                        }
                    }
                }
                //Check for capture
                for (int i = 0; i < 2; i++){
                    row = piece.getPosition().getRow() + 1;
                    col = piece.getPosition().getColumn();
                    if (i == 0){
                        col -= 1;
                    } else{
                        col += 1;
                    }
                    newMove = checkPawnMove(piece, row, col, canMove);
                    if (newMove != null){
                        movePositions.addAll(newMove);
                    }
                }
                break;

            case BLACK:
                //Check moving down
                row -= 1;
                newMove = checkPawnMove(piece, row, col, canMove);
                if (newMove != null) {
                    movePositions.addAll(newMove);
                    //Check to see if it is the first move for that pawn
                    if (piece.getPosition().getRow() == 7){
                        row -= 1;
                        newMove = checkPawnMove(piece, row, col, canMove);
                        if (newMove != null) {
                            movePositions.addAll(newMove);
                        }
                    }
                }
                //Check for capture
                for (int i = 0; i < 2; i++){
                    row = piece.getPosition().getRow() - 1;
                    col = piece.getPosition().getColumn();
                    if (i == 0){
                        col -= 1;
                    } else{
                        col += 1;
                    }
                    newMove = checkPawnMove(piece, row, col, canMove);
                    if (newMove != null){
                        movePositions.addAll(newMove);
                    }
                }
                break;
        }
        return movePositions;
    }

    public ArrayList<ChessMove> checkPawnMove(PiecesMovesCalculator piece, int row, int col, int[] canMove) {
        ChessPosition nextPosition = new ChessPosition(row, col);
        ArrayList<ChessMove> newMove = new ArrayList<>();
        ChessMove addMove;
        boolean isPromotion;
        boolean isAttack;
        //Check to see if piece is promoting or not
        if((piece.getBoard().getPiece(piece.getPosition()).getTeamColor() == ChessGame.TeamColor.WHITE && row == 8) || (piece.getBoard().getPiece(piece.getPosition()).getTeamColor() == ChessGame.TeamColor.BLACK && row == 1)) {
            isPromotion = true;
        } else {
            isPromotion = false;
        }
        //Check to see if piece is attacking or not
        if(piece.getPosition().getColumn() != col) {
            isAttack = true;
        } else {
            isAttack = false;
        }

        //Check to see if space is available to move
        canMove[0] = validatePawnMove(piece, nextPosition, isAttack);
        if ((canMove[0] == 1 || canMove[0] == 0) && !isPromotion) {
            addMove = new ChessMove(piece.getPosition(), nextPosition, null);
            newMove.add(addMove);
            return newMove;
        }
        if ((canMove[0] == 1 || canMove[0] == 0) && isPromotion) {
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        addMove = new ChessMove(piece.getPosition(), nextPosition, ChessPiece.PieceType.BISHOP);
                        newMove.add(addMove);
                        break;
                    case 1:
                        addMove = new ChessMove(piece.getPosition(), nextPosition, ChessPiece.PieceType.KNIGHT);
                        newMove.add(addMove);
                        break;
                    case 2:
                        addMove = new ChessMove(piece.getPosition(), nextPosition, ChessPiece.PieceType.QUEEN);
                        newMove.add(addMove);
                        break;
                    case 3:
                        addMove = new ChessMove(piece.getPosition(), nextPosition, ChessPiece.PieceType.ROOK);
                        newMove.add(addMove);
                        break;
                }
            }
            return newMove;
        }
        return null;
    }

    public  int validatePawnMove(PiecesMovesCalculator piece, ChessPosition nextPosition, boolean isAttack){
        if(nextPosition.getColumn() > 8 || nextPosition.getRow() > 8 || nextPosition.getRow() < 1 || nextPosition.getColumn() < 1) {
            return 2;
        }
        if(piece.getBoard().getPiece(nextPosition) != null) {
            //Check to see if occupied space is capturable
            if (checkToCapture(piece, nextPosition) && isAttack) {
                return 1;
            } else {
                return 2;
            }
        }
        else if (isAttack) {
            return 2;
        } else {
            return 0;
        }
    }

}
