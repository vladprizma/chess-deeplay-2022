package io.deeplay.client.gui;

import io.deeplay.core.model.Side;
import io.deeplay.core.player.PlayerType;

import javax.swing.*;
import java.util.function.BiConsumer;

public class MenuBar extends JMenuBar {
    private final BiConsumer<Side, PlayerType> sendNewGameRequest;
    public MenuBar(final BiConsumer<Side, PlayerType> sendNewGameRequest) {
        add(createFileMenu());
        this.sendNewGameRequest = sendNewGameRequest;
    }

    public JMenu createFileMenu() {
        JMenu file = new JMenu("Файл");
        JMenuItem newGame = new JMenuItem("Новая игра");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(newGame);
        file.addSeparator();
        file.add(exit);
        newGame.addActionListener(e -> {
            JComboBox<PlayerType> opponentType = new JComboBox<>(PlayerType.values());
            JRadioButton white = new JRadioButton("Белые", true);
            JRadioButton black = new JRadioButton("Черные");
            ButtonGroup bg = new ButtonGroup();
            bg.add(white);
            bg.add(black);
            final JComponent[] inputs = new JComponent[] {
                    opponentType,
                    white,
                    black
            };
            JOptionPane.showConfirmDialog(null, inputs, "Выберите сторону:", JOptionPane.DEFAULT_OPTION);
            if (white.isSelected()) {
                sendNewGameRequest.accept(Side.WHITE, (PlayerType) opponentType.getSelectedItem());
            } else if (black.isSelected()) {
                sendNewGameRequest.accept(Side.BLACK, (PlayerType) opponentType.getSelectedItem());
            }
        });
        exit.addActionListener(e -> System.exit(0));
        return file;
    }
}
