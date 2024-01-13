package io.deeplay.core.api;

import io.deeplay.core.model.BoardSituationInfo;
import io.deeplay.core.model.MoveInfo;
import io.deeplay.core.parser.FENParser;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс реализует интерфейс логики на основе SimpleLogic.
 * Сохраняет ситуации(мат, пат и т.д.) на доске по первой(до первого пробела) части нотации fen,
 * при повторении возвращает уже посчитанный до этого результат.
 */
public class SimpleLogicCache implements SimpleLogicAppeal {
    private final Map<String, BoardSituationInfo> boardSituationInfoMap;
    private final SimpleLogicAppeal simpleLogicAppeal;

    public SimpleLogicCache() {
        this.boardSituationInfoMap = new ConcurrentHashMap<>();
        this.simpleLogicAppeal = new SimpleLogic();
    }

    @Override
    public boolean isMate(final String fenNotation) {
        final String piecePlacement = FENParser.getNotationsFirstFourParts(fenNotation);
        // Не получится использовать computeIfAbsent, т.к. в методе, value ИСПОЛЬЗУЕТ тот же key для вычисления,
        // а key(piecePlacement) это урезанная fenNotation
        if (!boardSituationInfoMap.containsKey(piecePlacement)) {
            // Тут уже ВЫЧИСЛЯЕТСЯ getBoardSituationInfo(...), поэтому не получится использовать map.putIfAbsent
            boardSituationInfoMap.put(piecePlacement, simpleLogicAppeal.getBoardSituationInfo(fenNotation));
        }
        return boardSituationInfoMap.get(piecePlacement).isMate();
    }

    @Override
    public boolean isStalemate(final String fenNotation) {
        final String piecePlacement = FENParser.getNotationsFirstFourParts(fenNotation);
        if (!boardSituationInfoMap.containsKey(piecePlacement)) {
            boardSituationInfoMap.put(piecePlacement, simpleLogicAppeal.getBoardSituationInfo(fenNotation));
        }
        return boardSituationInfoMap.get(piecePlacement).isStalemate();
    }

    @Override
    public boolean isDrawByPieceShortage(final String fenNotation) {
        final String piecePlacement = FENParser.getNotationsFirstFourParts(fenNotation);
        if (!boardSituationInfoMap.containsKey(piecePlacement)) {
            boardSituationInfoMap.put(piecePlacement, simpleLogicAppeal.getBoardSituationInfo(fenNotation));
        }
        return boardSituationInfoMap.get(piecePlacement).isDrawByPieceShortage();
    }

    @Override
    public Set<MoveInfo> getMoves(final String fenNotation) {
        return simpleLogicAppeal.getMoves(fenNotation);
    }

    @Override
    public BoardSituationInfo getBoardSituationInfo(final String fenNotation) {
        return boardSituationInfoMap.computeIfAbsent(FENParser.getNotationsFirstFourParts(fenNotation),
                simpleLogicAppeal::getBoardSituationInfo);
    }
}
