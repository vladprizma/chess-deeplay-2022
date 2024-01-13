package io.deeplay.core.player;

import io.deeplay.core.logic.BitUtils;
import io.deeplay.core.model.Coord;
import io.deeplay.core.model.GameInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private final static String PLAYER_NAME = "HumanPlayer";
    Scanner scanner = new Scanner(System.in);
    String userInput;
    Optional<MoveInfo> userMove;
    // Функция для того, чтобы передавать ход от пользователя в selfplay.

    public HumanPlayer(final Side side) {
        super(side);
    }
    public MoveInfo getAnswer(final GameInfo gameInfo) {
        while (true){
            System.out.println("Make your move! (e2e4/E2 e4/E2E4 formats available)");
            userInput = scanner.nextLine().trim().toUpperCase().replace(" ", "");
            if ("QUIT".equals(userInput)) {
                System.exit(0);
            }
            String moveRegex = "[A-H][1-8][A-H][1-8]";
            if (!userInput.matches(moveRegex)){
                System.out.print("\033[2F\033[J");
                continue;
            }
            Coord coordFrom = new Coord(ArrayUtils.indexOf(BitUtils.SQUARES_STRING, userInput.substring(0, 2)));
            Coord coordTo = new Coord(ArrayUtils.indexOf(BitUtils.SQUARES_STRING, userInput.substring(2, 4)));
            if ((userMove = gameInfo.getAvailableMoves().stream().filter(move -> move.getCellFrom().equals(coordFrom) && move.getCellTo().equals(coordTo)).findFirst()).isPresent()) {
                return userMove.get();
            } else {
                System.out.print("\033[2F\033[J");
                System.out.println("Ход " + userInput + " невозможен");
            }
        }
    }

    @Override
    public String getName() {
        return PLAYER_NAME;
    }
}
