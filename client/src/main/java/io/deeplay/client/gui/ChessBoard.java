package io.deeplay.client.gui;

import io.deeplay.core.api.SimpleLogic;
import io.deeplay.core.api.SimpleLogicAppeal;
import io.deeplay.core.model.MoveInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChessBoard extends JPanel {
    private final Consumer<MoveInfo> sendMove;
    private static final int BOARD_SIZE = 8;
    private static final int BOARD_CELL_NUMBER = 64;
    private static final double RED_OFFSET = 1.35;
    private static final double GREEN_OFFSET = 1.4;
    private static final double BLUE_OFFSET = 1.5;
    private static final Color BLACK_CELL = new Color(181, 136, 99);
    private static final Color WHITE_CELL = new Color(240, 217, 181);
    private JButton[] cells;
    private int lastPressedButton;
    private Set<MoveInfo> moves;
    private final SimpleLogicAppeal logic;
    boolean reverse = false;
    private final Map<String, BufferedImage> figuresFromLetters = Map.ofEntries(
            Map.entry("q", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/bQ.png"))),
            Map.entry("k", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/bK.png"))),
            Map.entry("r", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/bR.png"))),
            Map.entry("n", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/bN.png"))),
            Map.entry("b", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/bB.png"))),
            Map.entry("p", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/bP.png"))),
            Map.entry("Q", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/wQ.png"))),
            Map.entry("K", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/wK.png"))),
            Map.entry("R", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/wR.png"))),
            Map.entry("N", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/wN.png"))),
            Map.entry("B", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/wB.png"))),
            Map.entry("P", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/wP.png"))),
            Map.entry("1", ImageIO.read(new File("client/src/main/resources/ChessPiecesPNG/none.png")))
    );

    public ChessBoard(Consumer<MoveInfo> sendMove) throws IOException {
        super();
        logic = new SimpleLogic();
        this.sendMove = sendMove;
        this.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        cells = new JButton[BOARD_CELL_NUMBER];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                cells[i * BOARD_SIZE + j] = new JButton();
                cells[i].setBorderPainted(false);
                if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1) {
                    cells[i * BOARD_SIZE + j].setBackground(WHITE_CELL);
                } else {
                    cells[i * BOARD_SIZE + j].setBackground(BLACK_CELL);
                }
                cells[i * BOARD_SIZE + j].setName(String.valueOf((BOARD_SIZE - 1 - i) * BOARD_SIZE + j));
                cells[i * BOARD_SIZE + j].addActionListener(this::processClick);
            }
        }
        for (JButton btn : cells) {
            this.add(btn);
        }
    }

    public void updateBoard(final String fen) {
        this.moves = logic.getMoves(fen);
        String unzippedFen = io.deeplay.core.model.ChessBoard.unzipFen(fen).split(" ", 2)[0]
                .replace("/", "");

        for (int i = 0; i < unzippedFen.length(); i++) {
            cells[!reverse ? i : BOARD_CELL_NUMBER - 1 - i].setIcon(new ImageIcon(figuresFromLetters.get(unzippedFen.substring(i, i + 1))));
        }
    }

    private void processClick(ActionEvent event) {
        if (((JButton) event.getSource()).getBackground() == BLACK_CELL ||
                ((JButton) event.getSource()).getBackground() == WHITE_CELL) {
            paintPossibleMoves(event);
        } else {
            processMove(lastPressedButton, Integer.parseInt(((JButton) event.getSource()).getName()));
        }
    }

    private void paintPossibleMoves(ActionEvent event) {
        String btnName = ((JButton) event.getSource()).getName();
        restoreBasicColor();
        lastPressedButton = Integer.parseInt(btnName);
        java.util.Set<MoveInfo> movesToHighlight = moves.stream().filter(move ->
                (move.getCellFrom().getIndexAsOneDimension() == lastPressedButton)).collect(Collectors.toSet());

        for (JButton cell : cells) {
            if (movesToHighlight.stream().anyMatch(move ->
                    (move.getCellTo().getIndexAsOneDimension() == Integer.parseInt(cell.getName())))) {
                cell.setBackground(new Color((int) (cell.getBackground().getRed() / RED_OFFSET),
                        (int) (cell.getBackground().getGreen() / GREEN_OFFSET),
                        (int) (cell.getBackground().getBlue() / BLUE_OFFSET)));
            }
        }
    }

    private void restoreBasicColor() {
        for (JButton cell : cells) {
            if (Integer.parseInt(cell.getName()) / BOARD_SIZE % 2 == 1 && Integer.parseInt(cell.getName()) % 2 == 1 ||
                    Integer.parseInt(cell.getName()) / BOARD_SIZE % 2 == 0 && Integer.parseInt(cell.getName()) % 2 == 0) {
                cell.setBackground(BLACK_CELL);
            } else {
                cell.setBackground(WHITE_CELL);
            }
        }
    }

    private void processMove(final int from, final int to) {
        Optional<MoveInfo> move = moves.stream().filter(x ->
                (x.getCellFrom().getIndexAsOneDimension() == from &&
                        x.getCellTo().getIndexAsOneDimension() == to)).findAny();
        move.ifPresent(sendMove);
        restoreBasicColor();
    }
    public void reverse() {
        java.util.List<JButton> tempCells = new ArrayList<>(List.of(cells));
        Collections.reverse(tempCells);
        cells = tempCells.toArray(new JButton[BOARD_CELL_NUMBER]);
        this.removeAll();
        for (JButton cell : cells) {
            this.add(cell);
        }
        reverse = !reverse;
    }
}
