package io.deeplay.client.console;

import io.deeplay.core.listener.ChessAdapter;
import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.model.Coord;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;
import java.util.Scanner;

public class ConsoleCommands extends ChessAdapter {
    private static final String QUIT = "QUIT";
    Scanner scanner = new Scanner(System.in);
    String userInput;
    GameInfo gameInfo = new GameInfo();
    Optional<MoveInfo> userMove;
    private final String moveRegex = "[A-H][1-8][A-H][1-8]";

    // Функция для того, чтобы передавать ход от пользователя в selfplay.
    public MoveInfo getMove() {
        while (true){
            System.out.println("Make your move! (e2e4/E2 e4/E2E4 formats available)");
            userInput = scanner.nextLine().trim().toUpperCase().replace(" ", "");
            if (!userInput.matches(moveRegex)){
                System.out.print("\033[2F\033[J");
                continue;
            }
            Coord coordFrom = new Coord(ArrayUtils.indexOf(BitUtils.SQUARES_STRING, userInput.substring(0, 2)));
            Coord coordTo = new Coord(ArrayUtils.indexOf(BitUtils.SQUARES_STRING, userInput.substring(2, 4)));
            System.out.println(this.gameInfo.getAvailableMoves());
            System.out.println(this.gameInfo.getFenBoard());
            System.out.println(this.gameInfo);
            if ((userMove = gameInfo.getAvailableMoves().stream().filter(move -> move.getCellFrom().equals(coordFrom) && move.getCellTo().equals(coordTo)).findFirst()).isPresent()) {
                return userMove.get();
            } else {
                System.out.println("there is not such a move available");
            }
        }
    }

    public void consoleCommandHandler() {
        System.out.println("Make your move! (e2e4/E2 e4/E2E4 formats available)");

        userInput = scanner.nextLine().trim().toUpperCase().replace(" ", "");
        switch (userInput) {
            case moveRegex:
                processMove(userInput);
                break;
            case QUIT:
                System.exit(0);

        }
    }

    private void processMove(final String userInput) {
    }

    @Override
    public void playerActed(Side side, MoveInfo moveInfo) {
        this.gameInfo.updateBoard(moveInfo);
    }
}

