package facade;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import static ui.EscapeSequences.*;
public class ChessDisplay {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;


    public void run(ChessGame game, boolean whiteBoard, ArrayList<ChessPosition> highlight) throws InvalidMoveException {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out, whiteBoard);
        drawChessBoard(out, game, whiteBoard, highlight);
        drawHeaders(out, whiteBoard);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, boolean whiteBoard) {

        setBlack(out);

        String[] headers = { "   ", "\u2003a ", "\u2003b ", "\u2003c ", "\u2003d ", "\u2003e ", "\u2003f ", "\u2003g ", "\u2003h ", "   " };
        if (whiteBoard) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES + 2; ++boardCol) {
                drawHeader(out, headers[boardCol]);
            }
        } else {
            for (int boardCol = 9; boardCol >= 0; --boardCol) {
                drawHeader(out, headers[boardCol]);
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out, ChessGame game, boolean whiteBoard, ArrayList<ChessPosition> highlight) {
        int borderNumber = 0;
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {//Draw squares from the top down
            drawRowOfSquares(out, borderNumber, game, whiteBoard, highlight);
            borderNumber++;
        }
    }

    private static void drawRowOfSquares(PrintStream out, int borderNumber, ChessGame game, boolean whiteBoard, ArrayList<ChessPosition> highlight) {

        String[] sides = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_CHARS; ++squareRow) {
            for (int boardCol = -1; boardCol < BOARD_SIZE_IN_SQUARES + 1; ++boardCol) {
                setWhite(out);
                boolean isHighlighted = false;

                //Check to see if you need to highlight
                if (highlight != null) {
                    for (ChessPosition position : highlight) {
                        if (whiteBoard) {
                            if (8 - borderNumber == position.getRow() && boardCol + 1 == position.getColumn()) {
                                isHighlighted = true;
                                break;
                            }
                        } else {
                            if ((9 - (8 - borderNumber)) == position.getRow() && (9 - (boardCol + 1)) == position.getColumn()) {
                                isHighlighted = true;
                                break;
                            }
                        }
                    }
                }

                //Alternate colors
                if ((boardCol + borderNumber) % 2 == 0) {
                    if (!isHighlighted) {
                        setWhite(out);
                    } else {
                        setGreen(out);
                    }
                } else {
                    if (!isHighlighted) {
                        setBlue(out);
                    } else {
                        setDarkGreen(out);
                    }
                }
                if (boardCol >= 0 && boardCol < BOARD_SIZE_IN_SQUARES) {
                    String piece = EMPTY;
                    if (whiteBoard) {
                        piece = determinePiece(game, boardCol, borderNumber);
                    } else {
                        piece = determinePiece(game, 7 - boardCol, 7 - borderNumber);
                    }
                    printPlayer(out, piece);
                }

                if (boardCol == -1 || boardCol == BOARD_SIZE_IN_SQUARES) {
                    // Draw vertical border
                    setBorder(out);
                    if (whiteBoard) {
                        out.print(sides[borderNumber]);
                    } else {
                        out.print(sides[7 - borderNumber]);
                    }
                }
                setBlack(out);
            }
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        //out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
    }

    private static void setBorder(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static String determinePiece(ChessGame game, int col, int row) {
        ChessPosition position = new ChessPosition(8 - row, col + 1);
        ChessPiece piece = game.getBoard().getPiece(position);
        String pieceString = EMPTY;
        if (piece != null) {
            switch (piece.getPieceType()) {
                case PAWN:
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pieceString = WHITE_PAWN;
                    } else {
                        pieceString = BLACK_PAWN;
                    }
                    break;

                case ROOK:
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pieceString = WHITE_ROOK;
                    } else {
                        pieceString = BLACK_ROOK;
                    }
                    break;

                case KNIGHT:
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pieceString = WHITE_KNIGHT;
                    } else {
                        pieceString = BLACK_KNIGHT;
                    }
                    break;

                case BISHOP:
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pieceString = WHITE_BISHOP;
                    } else {
                        pieceString = BLACK_BISHOP;
                    }
                    break;

                case QUEEN:
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pieceString = WHITE_QUEEN;
                    } else {
                        pieceString = BLACK_QUEEN;
                    }
                    break;

                case KING:
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pieceString = WHITE_KING;
                    } else {
                        pieceString = BLACK_KING;
                    }
                    break;
            }
        }
        return pieceString;
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(player);

        setWhite(out);
    }
}
