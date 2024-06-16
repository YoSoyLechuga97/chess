
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;
public class ChessDisplay {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";
    private static Random rand = new Random();


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawTicTacToeBoard(out);

        drawHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = { "   ", " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   " };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES + 2; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(player);

        setBlack(out);
    }

    private static void drawTicTacToeBoard(PrintStream out) {
        int borderNumber = 0;
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {//Draw squares from the top down
            drawRowOfSquares(out, borderNumber);
            borderNumber++;
        }
    }

    private static void drawRowOfSquares(PrintStream out, int borderNumber) {

        String[] sides = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_CHARS; ++squareRow) {
            for (int boardCol = -1; boardCol < BOARD_SIZE_IN_SQUARES + 1; ++boardCol) {
                setWhite(out);

                if (boardCol >= 0 && boardCol < BOARD_SIZE_IN_SQUARES) {
                    printPlayer(out, rand.nextBoolean() ? X : O);
                }

                if (boardCol == -1 || boardCol == BOARD_SIZE_IN_SQUARES) {
                    // Draw right line
                    setBorder(out);
                    out.print(sides[borderNumber]);
                }
                setBlack(out);
            }
            out.println();
        }
    }

    private static void drawVerticalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBorder(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}
