package io.deeplay.client.gui;

import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import io.deeplay.core.player.PlayerType;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Gui extends JFrame {
    private final JSplitPane splitPane;
    private final ChessBoard chessBoard;
    private final JPanel moveHistory;
    private final JScrollPane scrollPane;
    private final JTextArea textArea;
    private final JPanel inputPanel;
    private final JTextField textField;
    private final JButton nextMove;
    private final JButton prevMove;
    private final JMenuBar menubar;
    private final MoveHistory moveHistoryText;
    private GameInfo gameInfo;

    public Gui(final GameInfo gameInfo, final Consumer<MoveInfo> sendMove, final BiConsumer<Side, PlayerType> sendNewGameRequest) {
        super("Chess");
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        menubar = new MenuBar(sendNewGameRequest);
        splitPane = new JSplitPane();

        try {
            chessBoard = new ChessBoard(sendMove);
            chessBoard.updateBoard(io.deeplay.core.model.ChessBoard.DEFAULT_FEN_STRING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        moveHistory = new JPanel();      // Правая панель
        moveHistoryText = new MoveHistory();

        scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textArea = new JTextArea();


        inputPanel = new JPanel();
        textField = new JTextField(2);
        nextMove = new JButton(">");
        prevMove = new JButton("<");

        // Определение параметров окна
        setPreferredSize(new Dimension(1000, 800));
        // contentPane содержит все элементы главного окна
        getContentPane().setLayout(new GridLayout());  // Стандартная решетка для размещения объектов
        getContentPane().add(splitPane);               // Разделитель окна

        // let configure our splitPane:
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);
        splitPane.setLeftComponent(chessBoard);
        splitPane.setRightComponent(moveHistory);

        moveHistory.setLayout(new BoxLayout(moveHistory, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(moveHistoryText);
        moveHistory.add(moveHistoryText);
        moveHistory.add(scrollPane);
        moveHistory.add(textArea);
        textArea.append("Файл -> Новая игра\n");
        moveHistory.add(inputPanel);
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        inputPanel.add(prevMove);
        inputPanel.add(nextMove);
        inputPanel.add(createFlipButton());
        setJMenuBar(menubar);

        pack();// calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }

    public void updateBoard(final GameInfo gameInfo, final MoveInfo moveInfo) {
        chessBoard.updateBoard(gameInfo.getFenBoard());
        moveHistoryText.update(gameInfo, moveInfo);
        pack();
    }

    private JButton createFlipButton() {
        JButton flip = new JButton("flip");
        flip.addActionListener(e -> {
            chessBoard.reverse();
            super.setVisible(true);
        });
        return flip;
    }

    public void restart(final GameInfo gameInfo) {
        moveHistoryText.restart();
        chessBoard.updateBoard(gameInfo.getFenBoard());
        textArea.setText(null);
    }

    public void gameOver(final String message) {
        textArea.append("Игра окончена: " + message + "\n");
    }
}
