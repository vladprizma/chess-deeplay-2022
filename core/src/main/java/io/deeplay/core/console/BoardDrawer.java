package io.deeplay.core.console;

import io.deeplay.core.model.ChessBoard;

import java.io.*;

/**
 * Класс для отрисовки доски в терминал
 * Ячейка доски состоит из трех строк в каждой по семь символов.
 */
public class BoardDrawer {
    static final String BLACK_CELL = "\033[48;5;116m\033[38;5;0m ";
    static final String WHITE_CELL = "\033[48;5;230m\033[38;5;0m ";
    static final String COLUMNS = "   a      b      c      d      e      f      g      h  ";
    public static void draw(final String fen) {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        StringBuilder sb = new StringBuilder();
        // Очистить экран
        System.out.print("\033[H\033[J");
        ChessBoard board = new ChessBoard(fen);
        String unzipBoard = new StringBuilder(ChessBoard.unzipFen(board.getFEN()).replace("/", "").split(" ", 2)[0]).reverse().toString();

        // Замена символов строки фен на аски обозначения фигур.
        unzipBoard = unzipBoard.replace("p", "\u265F");
        unzipBoard = unzipBoard.replace("r", "\u265C");
        unzipBoard = unzipBoard.replace("n", "\u265E");
        unzipBoard = unzipBoard.replace("b", "\u265D");
        unzipBoard = unzipBoard.replace("q", "\u265B");
        unzipBoard = unzipBoard.replace("k", "\u265A");
        unzipBoard = unzipBoard.replace("P", "\u2659");
        unzipBoard = unzipBoard.replace("R", "\u2656");
        unzipBoard = unzipBoard.replace("N", "\u2658");
        unzipBoard = unzipBoard.replace("B", "\u2657");
        unzipBoard = unzipBoard.replace("Q", "\u2655");
        unzipBoard = unzipBoard.replace("K", "\u2654");
        unzipBoard = unzipBoard.replace("1", " ");

        // Перенос курсора в позицию (0:0)
        sb.append("\033[0;0H");
        for (int i = 0; i < 8; i++) {
            for (int l = 0; l < 3; l++) {
                if (l == 1) {
                    sb.append(8 - i);
                    sb.append(" ");
                } else {
                    sb.append("  ");
                }
                for (int j = 0; j < 8; j++) {
                    for (int k = 0; k < 3; k++) {
                        boolean isWhiteCell = i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1;
                        if (l != 1) {
                            if (isWhiteCell) {
                                sb.append(BLACK_CELL.repeat(7));
                            } else {
                                sb.append(WHITE_CELL.repeat(7));
                            }
                            break;
                        }
                        if (k == 1) {
                            char currentFenSymbol = unzipBoard.charAt(i * 8 + j);
                            if (isWhiteCell) {
                                sb.append(BLACK_CELL).append(currentFenSymbol).append(BLACK_CELL);
                            } else {
                                sb.append(WHITE_CELL).append(currentFenSymbol).append(WHITE_CELL);
                            }
                        } else {
                            if (isWhiteCell) {
                                sb.append(BLACK_CELL.repeat(2));
                            } else {
                                sb.append(WHITE_CELL.repeat(2));
                            }
                        }
                    }
                }
                // Очистка форматирования, чтобы оставшаяся часть строки не окрашивалась.
                sb.append("\033[m\n");
            }
        }
        sb.append("  ").append(COLUMNS);
        sb.append("\033[m\n");
        try {
            writer.write(sb.toString(), 0, sb.length());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
