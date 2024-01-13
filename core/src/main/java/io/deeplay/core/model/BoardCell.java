package io.deeplay.core.model;


public class BoardCell {

    private Figure figure;

    public BoardCell(Figure figure) {
        this.figure = figure;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(final Figure figure) {
        this.figure = figure;
    }
}

