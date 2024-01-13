package io.deeplay.core.player;

public enum PlayerType {
    HUMAN("Человек"),
    EVGEN_BOT("Бот Евгения"),
    RANDOM_BOT("Рандомный бот"),
    ALICE_BOT("Бот Алиса"),
    CHAD_BOT("Бот Андрея"),
    EVALUATION_BOT("Простой оценочный бот");

    private final String description;

    PlayerType(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
