package io.deeplay.core.model.bitboard;

public class MagicBoard {

    /**
     * Блокируемая маска содержащая все клетки, которые могут блокировать линейно-ходящие фигуры
     * A bitboard containing all squares that can block a rook or a bishop.
     */
    public long blockerMask;

    /**
     * Сгенерированные доски передвижений.
     */
    public long[] moveBoards;

    /**
     * Предварительно вычисленное значение, для того чтобы определить корректный индекс доски передвижений.
     */
    public int shift;

}
