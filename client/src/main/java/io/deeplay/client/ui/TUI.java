package io.deeplay.client.ui;

import io.deeplay.core.console.BoardDrawer;
import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.model.*;
import io.deeplay.interaction.clientToServer.StartGameRequest;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;
import java.util.Scanner;

/**
 * Консольная UI
 */
public class TUI implements UI {

    Scanner scanner = new Scanner(System.in);
    String userInput;
    Optional<MoveInfo> userMove;

    public TUI() {
    }

    @Override
    public void updateBoard(final GameInfo gameInfo) {
        BoardDrawer.draw(gameInfo.getFenBoard());
    }

    @Override
    public void gameOver(final GameStatus gameStatus) {
        System.out.println("Игра закончена. " + gameStatus.getMessage());
        while (true) {
            System.out.println("Хотите сыграть еще раз: \n 1 - да \n 2 - нет");
            userInput = scanner.nextLine();
            if (userInput.equals("1")) {
                break;
            }
            if (userInput.equals("2")) {
                System.exit(0);
            }
            System.out.println("Некорректный ввод");
        }
    }

    @Override
    public void start(final GameInfo gameInfo) {
        BoardDrawer.draw(gameInfo.getFenBoard());
    }
}
