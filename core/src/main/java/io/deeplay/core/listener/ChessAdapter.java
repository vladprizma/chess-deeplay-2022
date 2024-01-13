package io.deeplay.core.listener;

import io.deeplay.core.model.GameStatus;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.model.Side;

public class ChessAdapter implements ChessListener{
    @Override
    public void gameStarted() {

    }

    @Override
    public void playerSeated(final Side side) {

    }

    @Override
    public void playerActed(final Side side, final MoveInfo moveInfo) {

    }

    @Override
    public void offerDraw(final Side side) {

    }

    @Override
    public void acceptDraw(final Side side) {

    }

    @Override
    public void playerRequestsTakeBack(final Side side) {

    }

    @Override
    public void playerAgreesTakeBack(final Side side) {

    }

    @Override
    public void playerResigned(final Side side) {

    }

    @Override
    public void draw() {

    }

    @Override
    public void playerWon(final Side side) {

    }

    @Override
    public void gameOver(final GameStatus gameStatus) {

    }
}
