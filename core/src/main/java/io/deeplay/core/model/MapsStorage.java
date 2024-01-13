package io.deeplay.core.model;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public class MapsStorage {
    public static final Map<String, Figure> SYMBOLS_TO_FIGURE = Map.ofEntries(
            entry("p", Figure.B_PAWN),
            entry("r", Figure.B_ROOK),
            entry("n", Figure.B_KNIGHT),
            entry("b", Figure.B_BISHOP),
            entry("q", Figure.B_QUEEN),
            entry("k", Figure.B_KING),
            entry("P", Figure.W_PAWN),
            entry("R", Figure.W_ROOK),
            entry("N", Figure.W_KNIGHT),
            entry("B", Figure.W_BISHOP),
            entry("Q", Figure.W_QUEEN),
            entry("K", Figure.W_KING),
            entry("1", Figure.NONE)
    );

    public static final Map<Figure, String> FIGURE_TO_SYMBOL = Map.ofEntries(
            entry(Figure.B_PAWN, "p"),
            entry(Figure.B_ROOK, "r"),
            entry(Figure.B_KNIGHT, "n"),
            entry(Figure.B_BISHOP, "b"),
            entry(Figure.B_QUEEN, "q"),
            entry(Figure.B_KING, "k"),
            entry(Figure.W_PAWN, "P"),
            entry(Figure.W_ROOK, "R"),
            entry(Figure.W_KNIGHT, "N"),
            entry(Figure.W_BISHOP, "B"),
            entry(Figure.W_QUEEN, "Q"),
            entry(Figure.W_KING, "K"),
            entry(Figure.NONE, "-")
    );

    public static final Map<String, Integer> LETTERS_TO_NUMBERS = Map.ofEntries(
            entry("a", 0),
            entry("b", 1),
            entry("c", 2),
            entry("d", 3),
            entry("e", 4),
            entry("f", 5),
            entry("g", 6),
            entry("h", 7)
    );

    public static final Map<Integer, String> NUMBERS_TO_LETTERS = Map.ofEntries(
            entry(0,"a"),
            entry(1,"b"),
            entry(2,"c"),
            entry(3,"d"),
            entry(4,"e"),
            entry(5,"f"),
            entry(6,"g"),
            entry(7,"h")
    );
    public static final Set<Figure> BLACK_FIGURES = Set.of(
            Figure.B_PAWN,
            Figure.B_ROOK,
            Figure.B_KNIGHT,
            Figure.B_BISHOP,
            Figure.B_KING,
            Figure.B_QUEEN
    );
    public static final Set<Figure> WHITE_FIGURES = Set.of(
            Figure.W_PAWN,
            Figure.W_ROOK,
            Figure.W_KNIGHT,
            Figure.W_BISHOP,
            Figure.W_KING,
            Figure.W_QUEEN
    );

    public static final Set<GameStatus> END_GAME_BY_RULES = Set.of(
            GameStatus.WHITE_WON,
            GameStatus.BLACK_WON,
            GameStatus.STALEMATE,
            GameStatus.FIFTY_MOVES_RULE,
            GameStatus.THREEFOLD_REPETITION,
            GameStatus.INSUFFICIENT_MATING_MATERIAL
    );

    public static final Set<MoveType> ATTACKS = Set.of(
            MoveType.PAWN_ATTACK,
            MoveType.PAWN_ON_GO_ATTACK,
            MoveType.PAWN_TO_FIGURE_ATTACK,
            MoveType.USUAL_ATTACK
    );
}
