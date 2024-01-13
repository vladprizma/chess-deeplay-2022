package io.deeplay.core.api;

public class TestPinBitboardHandler {

    // TODO: Дописать тесты
    /*
    @Test
    public void testPinRookAndBishop() {
//        FENBoard fenBoard = new FENBoard("4r1k1/1pp4p/p7/B5p1/1PQ5/5P1b/P5PP/3K1R2 b - - 0 1");
        FENBoard fenBoard = new FENBoard("4r1k1/1pp4p/p7/B5pb/1PQ5/5P2/P5PP/3K1R2 w - - 0 1");
        Coord from = new Coord(BitUtils.BitIndex.G8_IDX.ordinal());
        Map<Side, SideBitboards> sideBitboards = FENParser.parseFENToBitboardsOld(fenBoard.getFenNotation());

        ChessBitboard chessBitboard = null;
        // Определяем стороны
        if (containsSameBits(sideBitboards.get(Side.WHITE).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.WHITE), sideBitboards.get(Side.BLACK));
        if (containsSameBits(sideBitboards.get(Side.BLACK).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.BLACK), sideBitboards.get(Side.WHITE));
        if (chessBitboard == null)
            throw new IllegalArgumentException("Координата не соответствует фигуре на доске");

        CheckData checkData = BitboardHandler.getCheckData(chessBitboard);
        System.out.println(checkData.getCheckType());
        printBitboard(checkData.getThreatPieceBitboard());
        printBitboard(checkData.getThreateningPiecePositionBitboard());
        printBitboard(checkData.getAllAttacks());

        Map<Integer, Long> allPossibleMoves = BitboardHandler.getAllPossibleMoves(chessBitboard, checkData);

        long allMoves = 0L;
        for (long moves : allPossibleMoves.values()) {
            allMoves |= moves;
        }
        printBitboard(allMoves);
        System.out.println(allPossibleMoves);
    }

    @Test
    public void testPinPawn() {
//        FENBoard fenBoard = new FENBoard("4k3/8/8/4p3/3N4/8/4R3/3K4 w - - 0 1");
//        FENBoard fenBoard = new FENBoard("4k3/8/8/4p3/3N3Q/8/8/3K4 w - - 0 1");
//        FENBoard fenBoard = new FENBoard("4k3/8/8/4p2Q/3N4/8/8/3K4 b - - 0 1");
        FENBoard fenBoard = new FENBoard("4k3/8/8/4p1Q1/3N4/8/8/3K4 b - - 0 1");
        Coord from = new Coord(BitUtils.BitIndex.E8_IDX.ordinal());
        Map<Side, SideBitboards> sideBitboards = FENParser.parseFENToBitboardsOld(fenBoard.getFenNotation());

        ChessBitboard chessBitboard = null;
        // Определяем стороны
        if (containsSameBits(sideBitboards.get(Side.WHITE).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.WHITE), sideBitboards.get(Side.BLACK));
        if (containsSameBits(sideBitboards.get(Side.BLACK).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.BLACK), sideBitboards.get(Side.WHITE));
        if (chessBitboard == null)
            throw new IllegalArgumentException("Координата не соответствует фигуре на доске");

        CheckData checkData = BitboardHandler.getCheckData(chessBitboard);

        Map<Integer, Long> allPossibleMoves = BitboardHandler.getAllPossibleMoves(chessBitboard, checkData);

        long allMoves = 0L;
        for (long moves : allPossibleMoves.values())
            allMoves |= moves;
        printBitboard(allMoves);
        System.out.println(allPossibleMoves);
    }

    @Test
    public void testPinQueen() {
        // Королева не может атаковать коня, т.к. связана на 4 ранге
        FENBoard fenBoard = new FENBoard("b7/1P1N3b/6B1/2n5/q1Q1KR1r/k7/8/8 b - - 0 1");
        Coord from = new Coord(BitUtils.BitIndex.E4_IDX.ordinal());
        Map<Side, SideBitboards> sideBitboards = FENParser.parseFENToBitboardsOld(fenBoard.getFenNotation());

        ChessBitboard chessBitboard = null;
        // Определяем стороны
        if (containsSameBits(sideBitboards.get(Side.WHITE).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.WHITE), sideBitboards.get(Side.BLACK));
        if (containsSameBits(sideBitboards.get(Side.BLACK).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.BLACK), sideBitboards.get(Side.WHITE));
        if (chessBitboard == null)
            throw new IllegalArgumentException("Координата не соответствует фигуре на доске");

        CheckData checkData = BitboardHandler.getCheckData(chessBitboard);

        Map<Integer, Long> allPossibleMoves = BitboardHandler.getAllPossibleMoves(chessBitboard, checkData); // не правильно выводит

        long allMoves = 0L;
        for (long moves : allPossibleMoves.values())
            allMoves |= moves;
        printBitboard(allMoves);
        System.out.println(allPossibleMoves);
    }

    @Test
    public void testPinKnight() {
        FENBoard fenBoard = new FENBoard("b7/1P1N3b/4p1B1/2n4r/4KR1r/k1q5/8/8 w - - 0 1");
        Coord from = new Coord(BitUtils.BitIndex.E4_IDX.ordinal());
        Map<Side, SideBitboards> sideBitboards = FENParser.parseFENToBitboardsOld(fenBoard.getFenNotation());

        ChessBitboard chessBitboard = null;
        // Определяем стороны
        if (containsSameBits(sideBitboards.get(Side.WHITE).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.WHITE), sideBitboards.get(Side.BLACK));
        if (containsSameBits(sideBitboards.get(Side.BLACK).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.BLACK), sideBitboards.get(Side.WHITE));
        if (chessBitboard == null)
            throw new IllegalArgumentException("Координата не соответствует фигуре на доске");

        CheckData checkData = BitboardHandler.getCheckData(chessBitboard);

        Map<Integer, Long> allPossibleMoves = BitboardHandler.getAllPossibleMoves(chessBitboard, checkData);

        long allMoves = 0L;
        for (long moves : allPossibleMoves.values())
            allMoves |= moves;
        printBitboard(allMoves);
        System.out.println(allPossibleMoves);
    }

    @Test
    public void testNotPinnedBishop() {
        FENBoard fenBoard = new FENBoard("2k5/8/8/8/b7/4r3/3B4/4K3 w - - 0 1");
        Coord from = new Coord(BitUtils.BitIndex.E1_IDX.ordinal());
        Map<Side, SideBitboards> sideBitboards = FENParser.parseFENToBitboardsOld(fenBoard.getFenNotation());

        ChessBitboard chessBitboard = null;
        // Определяем стороны
        if (containsSameBits(sideBitboards.get(Side.WHITE).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.WHITE), sideBitboards.get(Side.BLACK));
        if (containsSameBits(sideBitboards.get(Side.BLACK).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.BLACK), sideBitboards.get(Side.WHITE));
        if (chessBitboard == null)
            throw new IllegalArgumentException("Координата не соответствует фигуре на доске");

        CheckData checkData = BitboardHandler.getCheckData(chessBitboard);

        Map<Integer, Long> allPossibleMoves = BitboardHandler.getAllPossibleMoves(chessBitboard, checkData);

        long allMoves = 0L;
        for (long moves : allPossibleMoves.values())
            allMoves |= moves;
        printBitboard(allMoves);
        System.out.println(allPossibleMoves);
    }

    @Test
    public void testAll() {
//        FENBoard fenBoard = new FENBoard("8/1n1k4/5b2/8/8/1q6/7r/3R1K2 w - - 0 1");
        FENBoard fenBoard = new FENBoard("k7/8/7Q/8/8/5b2/8/2R4K w - - 0 1");
//        Coord from = new Coord(BitUtils.BitIndex.D7_IDX.ordinal());
        Coord from = new Coord(BitUtils.BitIndex.H1_IDX.ordinal());
        Map<Side, SideBitboards> sideBitboards = FENParser.parseFENToBitboardsOld(fenBoard.getFenNotation());

        ChessBitboard chessBitboard = null;
        // Определяем стороны
        if (containsSameBits(sideBitboards.get(Side.WHITE).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.WHITE), sideBitboards.get(Side.BLACK));
        if (containsSameBits(sideBitboards.get(Side.BLACK).getKing(), 1L << from.getIndexAsOneDimension()))
            chessBitboard = new ChessBitboard(sideBitboards.get(Side.BLACK), sideBitboards.get(Side.WHITE));
        if (chessBitboard == null)
            throw new IllegalArgumentException("Координата не соответствует фигуре на доске");

        CheckData checkData = BitboardHandler.getCheckData(chessBitboard);

        Map<Integer, Long> allPossibleMoves = BitboardHandler.getAllPossibleMoves(chessBitboard, checkData);

        long allMoves = 0L;
        for (long moves : allPossibleMoves.values())
            allMoves |= moves;
        printBitboard(allMoves);
        System.out.println(allPossibleMoves);
    }
     */
}
