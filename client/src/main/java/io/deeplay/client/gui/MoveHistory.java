package io.deeplay.client.gui;

import io.deeplay.core.api.SimpleLogic;
import io.deeplay.core.api.SimpleLogicAppeal;
import io.deeplay.core.model.*;
import io.deeplay.core.model.ChessBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class MoveHistory extends JPanel {
    List<String> moveHistory = new LinkedList<>();
    // Нужна для определения могла ли другая фигура походить в ту же клетку.
    private String previousChessBoardFen;
    private int moveNumber;

    private final SimpleLogicAppeal logic;
    public static final Color MOVE_BUTTON_BACKGROUND_COLOR = new Color(0.18f, 0.31f, 0.44f, 0.5f);

    public MoveHistory() {
        logic = new SimpleLogic();
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 1));
        setPreferredSize(new Dimension(270, 500));
        previousChessBoardFen = ChessBoard.DEFAULT_FEN_STRING;
        moveNumber = 1;
    }

    final static Map<Figure, String> FIGURE_STRING_MAP = Map.ofEntries(
            entry(Figure.B_PAWN, "♟"),
            entry(Figure.B_ROOK, "♜"),
            entry(Figure.B_KNIGHT, "♞"),
            entry(Figure.B_BISHOP, "♝"),
            entry(Figure.B_QUEEN, "♛"),
            entry(Figure.B_KING, "♚"),
            entry(Figure.W_PAWN, "♙"),
            entry(Figure.W_ROOK, "♖"),
            entry(Figure.W_KNIGHT, "♘"),
            entry(Figure.W_BISHOP, "♗"),
            entry(Figure.W_QUEEN, "♕"),
            entry(Figure.W_KING, "♔")
    );

    public void update(final GameInfo gameInfo, final MoveInfo moveInfo) {
        JButton move;
        final boolean isCheck = logic.getBoardSituationInfo(gameInfo.getFenBoard()).isCheck();
        final boolean isMate = logic.getBoardSituationInfo(gameInfo.getFenBoard()).isMate();
        move = getMoveButton(moveInfo, gameInfo.whoseMove());
        if (isMate) {
            move.setText(move.getText() + "#");
        } else if (isCheck) {
            move.setText(move.getText() + "+");
        }
        move.setFocusPainted(false);
        move.setMargin(new Insets(0, 0, 0, 0));
        move.setContentAreaFilled(false);
        move.setBorderPainted(false);
        move.setOpaque(true);
        move.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                move.setBackground(MOVE_BUTTON_BACKGROUND_COLOR);
                move.setContentAreaFilled(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                move.setContentAreaFilled(false);
            }
        });
        add(move);
        previousChessBoardFen = gameInfo.getFenBoard();
        moveNumber += gameInfo.whoseMove() == Side.BLACK ? 0 : 1;
    }

    private JButton getMoveButton(final MoveInfo moveInfo, final Side whoseMove) {
        final boolean couldOtherFigureMakeSameMove = logic.getMoves(previousChessBoardFen).stream().filter(m ->
                m.getCellTo().equals(moveInfo.getCellTo()) && m.getFigure() == moveInfo.getFigure()).count() > 1;
        String buttonText = FIGURE_STRING_MAP.get(moveInfo.getFigure());
        if (!couldOtherFigureMakeSameMove) {
            if (moveInfo.getMoveType() == MoveType.USUAL_MOVE ||
                    moveInfo.getMoveType() == MoveType.PAWN_LONG_MOVE) {
                buttonText += moveInfo.getCellTo().getCoordAsString().toLowerCase();
            } else if (moveInfo.getMoveType() == MoveType.USUAL_ATTACK) {
                buttonText += "x" + moveInfo.getCellTo().getCoordAsString().toLowerCase();
            }
        } else if (moveInfo.getMoveType() == MoveType.USUAL_MOVE) {
            buttonText += moveInfo.getCellFrom().getCoordAsString().toLowerCase() +
                            moveInfo.getCellTo().getCoordAsString().toLowerCase();
        } else if (moveInfo.getMoveType() == MoveType.USUAL_ATTACK) {
            buttonText += moveInfo.getCellFrom().getCoordAsString().toLowerCase() + "x" +
                            moveInfo.getCellTo().getCoordAsString().toLowerCase();
        }
        if (moveInfo.getMoveType() == MoveType.PAWN_ATTACK || moveInfo.getMoveType() == MoveType.PAWN_ON_GO_ATTACK) {
            buttonText = moveInfo.getCellFrom().getCoordAsString().substring(0, 1).toLowerCase() +
                    "x" + moveInfo.getCellTo().getCoordAsString().toLowerCase();
        } else if (moveInfo.getMoveType() == MoveType.PAWN_TO_FIGURE) {
            if (moveInfo.getPromoteTo() == null) {
                buttonText = moveInfo.getCellTo().getCoordAsString().toLowerCase() + "=" +
                        FIGURE_STRING_MAP.get(whoseMove == Side.WHITE ? Figure.W_QUEEN : Figure.B_QUEEN);
            } else {
                buttonText = moveInfo.getCellTo().getCoordAsString().toLowerCase() + "=" +
                        FIGURE_STRING_MAP.get(moveInfo.getPromoteTo());
            }
        } else if (moveInfo.getMoveType() == MoveType.PAWN_TO_FIGURE_ATTACK) {
            if (moveInfo.getPromoteTo() == null) {
                buttonText = moveInfo.getCellFrom().getCoordAsString().substring(0, 1).toLowerCase() +
                        "x" + moveInfo.getCellTo().getCoordAsString().toLowerCase() + "=" +
                        FIGURE_STRING_MAP.get(whoseMove == Side.WHITE ? Figure.W_QUEEN : Figure.B_QUEEN);
            } else {
                buttonText = moveInfo.getCellFrom().getCoordAsString().substring(0, 1).toLowerCase() +
                        "x" + moveInfo.getCellTo().getCoordAsString().toLowerCase() + "=" +
                        FIGURE_STRING_MAP.get(moveInfo.getPromoteTo());
            }
        } else if (moveInfo.getMoveType() == MoveType.CASTLE_SHORT) {
            buttonText = "O-O";
        } else if (moveInfo.getMoveType() == MoveType.CASTLE_LONG) {
            buttonText = "O-O-O";
        }

        buttonText = whoseMove == Side.BLACK ? " " + moveNumber + "." + buttonText : buttonText;
        return new JButton(buttonText);
    }
    public void restart() {
        removeAll();
        moveNumber = 1;
        previousChessBoardFen = ChessBoard.DEFAULT_FEN_STRING;
        repaint();
    }
}
