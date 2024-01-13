package io.deeplay.core.model;

public enum GameStatus {
    // Пока создается сеанс игры, она неактивна
    INACTIVE("Сеанс игры еще не стартовал"),
    // С момента начала игры клиентом, до получения конечных результатов игра является активной
    ACTIVE("Игра активна"),
    // Если с игрой что-то произошло не по причине игроков, то игра считается прерванной
    INTERRUPTED("Игра прервана"),
    // Белые поставили мат, либо черные сдались
    WHITE_WON("Белые победили"),
    // Черные поставили мат, либо белые сдались
    BLACK_WON("Черные победили"),
    // Разные виды ничьи.
    STALEMATE("Пат"),
    FIFTY_MOVES_RULE("Ничья по правилу 50 ходов"),
    THREEFOLD_REPETITION("Ничья после трехкратного повторения"),
    INSUFFICIENT_MATING_MATERIAL("Ничья, недостаточно материла для мата"),
    DRAW("Ничья по соглашению игроков");

    private final String message;

    GameStatus(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
