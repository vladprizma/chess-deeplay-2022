package io.deeplay.core.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestChessBoard {
    @Test
    public void zipFenTest() {
        String fen = "rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P2P/5N2/PPPP1PP1/RNBQK2R b KQkq - 0 1";
        ChessBoard board = new ChessBoard();
        assertEquals(board.zipFen(ChessBoard.unzipFen(fen)), fen);
    }

    @Test
    public void updateBoardTest() {
        String fen = "rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4";
        ChessBoard board = new ChessBoard(fen);
        MoveInfo moveinfo = new MoveInfo(new Coord(4, 0), new Coord(6, 0),
                MoveType.CASTLE_SHORT, Figure.W_KING);
        board.updateBoard(moveinfo);
        assertEquals("r n b q k - - r \n" +
                "p p p p - p p p \n" +
                "- - - - - n - - \n" +
                "- - b - p - - - \n" +
                "- - B - P - - - \n" +
                "- - - - - N - - \n" +
                "P P P P - P P P \n" +
                "R N B Q - R K - \n", board.toString());

        moveinfo = new MoveInfo(new Coord(0, 6), new Coord(0, 4),
                MoveType.PAWN_LONG_MOVE, Figure.B_PAWN);
        board.updateBoard(moveinfo);
        assertEquals("r n b q k - - r \n" +
                "- p p p - p p p \n" +
                "- - - - - n - - \n" +
                "p - b - p - - - \n" +
                "- - B - P - - - \n" +
                "- - - - - N - - \n" +
                "P P P P - P P P \n" +
                "R N B Q - R K - \n", board.toString());
        moveinfo = new MoveInfo(new Coord(0, 1), new Coord(0, 2),
                MoveType.USUAL_MOVE, Figure.W_PAWN);
        board.updateBoard(moveinfo);
        assertEquals("r n b q k - - r \n" +
                "- p p p - p p p \n" +
                "- - - - - n - - \n" +
                "p - b - p - - - \n" +
                "- - B - P - - - \n" +
                "P - - - - N - - \n" +
                "- P P P - P P P \n" +
                "R N B Q - R K - \n", board.toString());

        fen = "r3kbnr/pp2pppp/2nqb3/2pp4/2PP1B2/1QN5/PP2PPPP/R3KBNR w KQkq - 6 6";
        board = new ChessBoard(fen);
        moveinfo = new MoveInfo(new Coord(3, 3), new Coord(2, 4),
                MoveType.PAWN_ATTACK, Figure.W_PAWN);
        board.updateBoard(moveinfo);
        assertEquals("r - - - k b n r \n" +
                "p p - - p p p p \n" +
                "- - n q b - - - \n" +
                "- - P p - - - - \n" +
                "- - P - - B - - \n" +
                "- Q N - - - - - \n" +
                "P P - - P P P P \n" +
                "R - - - K B N R \n", board.toString());

        moveinfo = new MoveInfo(new Coord(1, 6), new Coord(1, 4),
                MoveType.PAWN_LONG_MOVE, Figure.B_PAWN);
        board.updateBoard(moveinfo);
        assertEquals("r - - - k b n r \n" +
                "p - - - p p p p \n" +
                "- - n q b - - - \n" +
                "- p P p - - - - \n" +
                "- - P - - B - - \n" +
                "- Q N - - - - - \n" +
                "P P - - P P P P \n" +
                "R - - - K B N R \n", board.toString());

        moveinfo = new MoveInfo(new Coord(2, 4), new Coord(1, 5),
                MoveType.PAWN_ON_GO_ATTACK, Figure.W_PAWN);
        board.updateBoard(moveinfo);
        assertEquals("r - - - k b n r \n" +
                "p - - - p p p p \n" +
                "- P n q b - - - \n" +
                "- - - p - - - - \n" +
                "- - P - - B - - \n" +
                "- Q N - - - - - \n" +
                "P P - - P P P P \n" +
                "R - - - K B N R \n", board.toString());

        moveinfo = new MoveInfo(new Coord(4, 7), new Coord(2, 7),
                MoveType.CASTLE_LONG, Figure.B_KING);
        board.updateBoard(moveinfo);
        assertEquals("- - k r - b n r \n" +
                "p - - - p p p p \n" +
                "- P n q b - - - \n" +
                "- - - p - - - - \n" +
                "- - P - - B - - \n" +
                "- Q N - - - - - \n" +
                "P P - - P P P P \n" +
                "R - - - K B N R \n", board.toString());
    }

    @Test
    public void threefoldRepetitionTest() {
        GameInfo gameInfo = new GameInfo();
        gameInfo.playerSeated(Side.WHITE);
        gameInfo.playerSeated(Side.BLACK);
        gameInfo.gameStarted();
        MoveInfo moveInfoWhiteFirst = new MoveInfo(new Coord(1, 0), new Coord(2, 2), MoveType.USUAL_MOVE, Figure.W_KNIGHT);
        MoveInfo moveInfoWhiteSecond = new MoveInfo(new Coord(2, 2), new Coord(1, 0), MoveType.USUAL_MOVE, Figure.W_KNIGHT);
        MoveInfo moveInfoBlackFirst = new MoveInfo(new Coord(6, 7), new Coord(5, 5), MoveType.USUAL_MOVE, Figure.B_KNIGHT);
        MoveInfo moveInfoBlackSecond = new MoveInfo(new Coord(5, 5), new Coord(6, 7), MoveType.USUAL_MOVE, Figure.B_KNIGHT);
        gameInfo.updateBoard(moveInfoWhiteFirst);
        gameInfo.updateBoard(moveInfoBlackFirst);
        gameInfo.updateBoard(moveInfoWhiteSecond);
        gameInfo.updateBoard(moveInfoBlackSecond);
        gameInfo.updateBoard(moveInfoWhiteFirst);
        gameInfo.updateBoard(moveInfoBlackFirst);
        gameInfo.updateBoard(moveInfoWhiteSecond);
        gameInfo.updateBoard(moveInfoBlackSecond);
        assertEquals(GameStatus.THREEFOLD_REPETITION, gameInfo.getGameStatus());
    }

    @Test
    public void fiftyMovesDrawTest() {
        GameInfo gameInfo = new GameInfo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 99 50");
        MoveInfo moveInfoWhiteFirst = new MoveInfo(new Coord(1, 0), new Coord(2, 2), MoveType.USUAL_MOVE, Figure.W_KNIGHT);
        gameInfo.updateBoard(moveInfoWhiteFirst);
        assertEquals(gameInfo.getGameStatus(), GameStatus.FIFTY_MOVES_RULE);
    }

    @Test
    public void cloneTest() {
        ChessBoard board = new ChessBoard();
        ChessBoard secondBoard = new ChessBoard(board);
        assertNotEquals(board.getBoard(), secondBoard.getBoard());
        GameInfo firstGameInfo = new GameInfo();
        GameInfo secondGameInfo = firstGameInfo.copy();
        MoveInfo moveInfoWhiteFirst = new MoveInfo(new Coord(1, 0), new Coord(2, 2), MoveType.USUAL_MOVE, Figure.W_KNIGHT);
        secondGameInfo.updateBoard(moveInfoWhiteFirst);
        assertNotEquals(firstGameInfo.getFenBoard(), secondGameInfo.getFenBoard());
    }
}
