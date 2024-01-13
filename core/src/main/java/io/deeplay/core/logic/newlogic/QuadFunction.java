package io.deeplay.core.logic.newlogic;

/**
 * Используется в SimpleBitboardHandler.java,
 * а так же хранится внутри фигуры(PieceBitboard.java), чтобы обрабатывать ход.
 */
@FunctionalInterface
public interface QuadFunction<A, B, C, E, R> {
    R apply(A a, B b, C c, E e);
}
