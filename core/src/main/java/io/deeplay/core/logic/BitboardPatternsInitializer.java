package io.deeplay.core.logic;

import io.deeplay.core.model.bitboard.MagicBoard;

import java.util.*;

/**
 * В начале работы класс инициализирует паттерны передвижений фигур, после чего не производит никаких вычислений
 */
public class BitboardPatternsInitializer {

    public static final long whiteCells = 0b0101010110101010010101011010101001010101101010100101010110101010L;
    public static final long blackCells = 0b1010101001010101101010100101010110101010010101011010101001010101L;
    public static final long[] ROOK_MAGIC_NUMBERS = {
            0x0A8002C000108020L, 0x06C00049B0002001L, 0x0100200010090040L, 0x2480041000800801L,
            0x0280028004000800L, 0x0900410008040022L, 0x0280020001001080L, 0x2880002041000080L,
            0xA000800080400034L, 0x0004808020004000L, 0x2290802004801000L, 0x0411000D00100020L,
            0x0402800800040080L, 0x000B000401004208L, 0x2409000100040200L, 0x0001002100004082L,
            0x0022878001E24000L, 0x1090810021004010L, 0x0801030040200012L, 0x0500808008001000L,
            0x0A08018014000880L, 0x8000808004000200L, 0x0201008080010200L, 0x0801020000441091L,
            0x0000800080204005L, 0x1040200040100048L, 0x0000120200402082L, 0x0D14880480100080L,
            0x0012040280080080L, 0x0100040080020080L, 0x9020010080800200L, 0x0813241200148449L,
            0x0491604001800080L, 0x0100401000402001L, 0x4820010021001040L, 0x0400402202000812L,
            0x0209009005000802L, 0x0810800601800400L, 0x4301083214000150L, 0x204026458E001401L,
            0x0040204000808000L, 0x8001008040010020L, 0x8410820820420010L, 0x1003001000090020L,
            0x0804040008008080L, 0x0012000810020004L, 0x1000100200040208L, 0x430000A044020001L,
            0x0280009023410300L, 0x00E0100040002240L, 0x0000200100401700L, 0x2244100408008080L,
            0x0008000400801980L, 0x0002000810040200L, 0x8010100228810400L, 0x2000009044210200L,
            0x4080008040102101L, 0x0040002080411D01L, 0x2005524060000901L, 0x0502001008400422L,
            0x489A000810200402L, 0x0001004400080A13L, 0x4000011008020084L, 0x0026002114058042L,
    };

    public static final long[] BISHOP_MAGIC_NUMBERS = {
            0x89a1121896040240L, 0x2004844802002010L, 0x2068080051921000L, 0x62880a0220200808L,
            0x0004042004000000L, 0x0100822020200011L, 0xc00444222012000aL, 0x0028808801216001L,
            0x0400492088408100L, 0x0201c401040c0084L, 0x00840800910a0010L, 0x0000082080240060L,
            0x2000840504006000L, 0x30010c4108405004L, 0x1008005410080802L, 0x8144042209100900L,
            0x0208081020014400L, 0x004800201208ca00L, 0x0F18140408012008L, 0x1004002802102001L,
            0x0841000820080811L, 0x0040200200a42008L, 0x0000800054042000L, 0x88010400410c9000L,
            0x0520040470104290L, 0x1004040051500081L, 0x2002081833080021L, 0x000400c00c010142L,
            0x941408200c002000L, 0x0658810000806011L, 0x0188071040440a00L, 0x4800404002011c00L,
            0x0104442040404200L, 0x0511080202091021L, 0x0004022401120400L, 0x80c0040400080120L,
            0x8040010040820802L, 0x0480810700020090L, 0x0102008e00040242L, 0x0809005202050100L,
            0x8002024220104080L, 0x0431008804142000L, 0x0019001802081400L, 0x0200014208040080L,
            0x3308082008200100L, 0x041010500040c020L, 0x4012020c04210308L, 0x208220a202004080L,
            0x0111040120082000L, 0x6803040141280a00L, 0x2101004202410000L, 0x8200000041108022L,
            0x0000021082088000L, 0x0002410204010040L, 0x0040100400809000L, 0x0822088220820214L,
            0x0040808090012004L, 0x00910224040218c9L, 0x0402814422015008L, 0x0090014004842410L,
            0x0001000042304105L, 0x0010008830412a00L, 0x2520081090008908L, 0x40102000a0a60140L,
    };


    public static final long[] kingMoveBitboards;

    public static final long[] knightMoveBitboards;
    public static final MagicBoard[] rookMagicBoards;

    public static final MagicBoard[] bishopMagicBoards;

    // В List т.к. храниться в Map
    public static final List<Long> whitePawnMoveBitboards;
    public static final List<Long> blackPawnMoveBitboards;

    static {
        kingMoveBitboards = calcKingMoveBitboards();
        knightMoveBitboards = calcKnightMoveBitboards();
        whitePawnMoveBitboards = calcWhitePawnAttackBitboards();
        blackPawnMoveBitboards = calcBlackPawnAttackBitboards();
        /*
            Rook on e4:
            -----------

            The blocker mask        A blocker board         The move board
            ----------------        ---------------         --------------
            0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0         0 0 0 0 0 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0
            0 1 1 1 0 1 1 0         0 1 1 0 0 0 0 0         0 0 1 1 0 1 1 1
            0 0 0 0 1 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 1 0 0 0
            0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0         0 0 0 0 1 0 0 0
            0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0         0 0 0 0 0 0 0 0

            The blocker mask is all of the squares that can be occupied and block your piece.
            A blocker board is a subset of the blocker mask.
            There are 2^bits blocker boards, where bits is the number of 1's in the blocker mask.
        */

        rookMagicBoards = new MagicBoard[64];
        bishopMagicBoards = new MagicBoard[64];

        calcRookBlockerMasks();
        calcBishopBlockerMasks();

        long[][] rookBlockerBoards = calcBlockerBoards(rookMagicBoards);
        long[][] bishopBlockerBoards = calcBlockerBoards(bishopMagicBoards);

        calcShifts();

        calcRookMoveBoards(rookBlockerBoards);
        calcBishopMoveBoards(bishopBlockerBoards);
    }

    /**
     * Calculate every possible position of attack for any white pawn.
     *
     * @return A precomputed lookup table.
     */
    private static List<Long> calcWhitePawnAttackBitboards() {
        List<Long> pawnAttackBitboards = new ArrayList<>();
        long squareBitboard;
        long rightBitboard;
        long leftBitboard;

        for (int i = 0; i < 64; i++) {
            squareBitboard = 1L << i;
            rightBitboard = (squareBitboard << 9) & BitUtils.CLEAR_FILE_A;
            leftBitboard = (squareBitboard << 7) & BitUtils.CLEAR_FILE_H;

            pawnAttackBitboards.add(rightBitboard | leftBitboard);
        }

        return Collections.unmodifiableList(pawnAttackBitboards);
    }

    //-------------------------------------------------
    // Calculate black pawn attacks
    //-------------------------------------------------

    /**
     * Calculate every possible position of attack for any black pawn.
     *
     * @return A precomputed lookup table.
     */
    private static List<Long> calcBlackPawnAttackBitboards() {
        List<Long> pawnAttackBitboards = new ArrayList<>();
        long squareBitboard;
        long rightBitboard;
        long leftBitboard;

        for (int i = 0; i < 64; i++) {
            squareBitboard = 1L << i;
            rightBitboard = (squareBitboard >>> 9) & BitUtils.CLEAR_FILE_H;
            leftBitboard = (squareBitboard >>> 7) & BitUtils.CLEAR_FILE_A;

            pawnAttackBitboards.add(rightBitboard | leftBitboard);
        }

        return Collections.unmodifiableList(pawnAttackBitboards);
    }

    private static long[] calcKingMoveBitboards() {
        long[] kingMoveBitboards = new long[64];

        for (int square = 0; square < 64; square++) {
            long squareBitboard = BitUtils.SQUARES[square];
            long mask =
                    (((squareBitboard >>> 1) | (squareBitboard << 7) | (squareBitboard >>> 9)) & BitUtils.CLEAR_FILE_H) |
                            (((squareBitboard << 1) | (squareBitboard << 9) | (squareBitboard >>> 7)) & BitUtils.CLEAR_FILE_A) |
                            (squareBitboard << 8) | (squareBitboard >>> 8);

            kingMoveBitboards[square] = mask;
        }

        return kingMoveBitboards;
    }

    private static long[] calcKnightMoveBitboards() {
        long[] knightMoveBitboards = new long[64];

        for (int square = 0; square < 64; square++) {
            long squareBitboard = BitUtils.SQUARES[square];
            long mask =
                    (((squareBitboard << 6) | (squareBitboard >>> 10)) & BitUtils.CLEAR_FILE_GH) |
                            (((squareBitboard << 15) | (squareBitboard >>> 17)) & BitUtils.CLEAR_FILE_H) |
                            (((squareBitboard << 17) | (squareBitboard >>> 15)) & BitUtils.CLEAR_FILE_A) |
                            (((squareBitboard << 10) | (squareBitboard >>> 6)) & BitUtils.CLEAR_FILE_AB);

            knightMoveBitboards[square] = mask;
        }

        return knightMoveBitboards;
    }

    /**
     * Предварительно вычисляем блокирующие маски для любого цвета ладьи
     */
    private static void calcRookBlockerMasks() {
        for (int square = 0; square < 64; square++) {
            rookMagicBoards[square] = new MagicBoard();

            for (int i = square + 8; i < 64 - 8; i += 8) {
                rookMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }

            for (int i = square - 8; i >= 8; i -= 8) {
                rookMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }

            for (int i = square + 1; i % 8 != 0 && i % 8 != 7; i++) {
                rookMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }

            for (int i = square - 1; i % 8 != 7 && i % 8 != 0 && i > 0; i--) {
                rookMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }
        }
    }

    /**
     * Предварительно вычисляем блокирующие маски для любого цвета слона
     */
    private static void calcBishopBlockerMasks() {
        for (int square = 0; square < 64; square++) {
            bishopMagicBoards[square] = new MagicBoard();

            for (int i = square + 7; i < 64 - 7 && i % 8 != 7 && i % 8 != 0; i += 7) {
                bishopMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }

            for (int i = square + 9; i < 64 - 9 && i % 8 != 7 && i % 8 != 0; i += 9) {
                bishopMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }

            for (int i = square - 9; i >= 9 && i % 8 != 7 && i % 8 != 0; i -= 9) {
                bishopMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }

            for (int i = square - 7; i >= 7 && i % 8 != 7 && i % 8 != 0; i -= 7) {
                bishopMagicBoards[square].blockerMask |= BitUtils.SQUARES[i];
            }
        }
    }

    /**
     * Предварительно вычисляем блокирующие доски для любого цвета ладьи или слона
     *
     * @param magicBoards An {@link MagicBoard} array object.
     * @return The precomputed blocker boards.
     * @see <a href="https://stackoverflow.com/questions/30680559/how-to-find-magic-bitboards">how-to-find-magic-bitboards</a>
     */
    private static long[][] calcBlockerBoards(MagicBoard[] magicBoards) {
        long[][] blockerBoards = new long[64][];

        int square = 0;
        for (MagicBoard magic : magicBoards) {
            int bitCount = Long.bitCount(magic.blockerMask);

            // there are 2^bitCount blocker boards
            int blockerBoardCount = 1 << bitCount;
            blockerBoards[square] = new long[blockerBoardCount];

            for (int i = 0; i < blockerBoardCount; i++)
                blockerBoards[square][i] = generateBlockerboard(i, magic.blockerMask);

            square++;
        }

        return blockerBoards;
    }

    /*
       Example:
                      52      44      36         25   20      12              i       (Square)
                      |       |       |          |    |       |
                      9       8       7     65 432    1       0               counter (i'th bit is set)
       Rook on E4:    10000000100000001000001101110000100000001000000000000

       i = 1000:      00000000000000000000000000000000000000000001111101000
       1 << 0:        00000000000000000000000000000000000000000000000000001   0 delete 12
       1 << 1:        00000000000000000000000000000000000000000000000000010   0 delete 20
       1 << 2:        00000000000000000000000000000000000000000000000000100   0 delete 25
       1 << 3:        00000000000000000000000000000000000000000000000001000   1
       1 << 4:        00000000000000000000000000000000000000000000000010000   0 delete 27
       1 << 5:        00000000000000000000000000000000000000000000000100000   1
       1 << 6:        00000000000000000000000000000000000000000000001000000   1
       1 << 7:        00000000000000000000000000000000000000000000010000000   1
       1 << 8:        00000000000000000000000000000000000000000000100000000   1
       1 << 9:        00000000000000000000000000000000000000000001000000000   1
    */

    /**
     * Генерирует уникальную блокирующую доску, по полученному индексу (0..2^(количество включенных битов))
     * и блокирующую маску
     * Generate a unique blocker board, given an index (0..2^bits) and the blocker mask.
     *
     * @param index       An index (0..2^bits).
     * @param blockerMask Containing all squares that can block a piece.
     * @return A unique blocker board.
     * @see <a href="https://stackoverflow.com/questions/30680559/how-to-find-magic-bitboards">how-to-find-magic-bitboards</a>
     */
    private static long generateBlockerboard(int index, long blockerMask) {
        // start with a blocker board identical to the mask
        long blockerBoard = blockerMask;

        int counter = 0;
        for (int i = 0; i < 64; i++) {
            // check if the i'th bit is set in the mask (and thus a potential blocker)
            if ((blockerMask & (1L << i)) != 0) {

                if ((index & (1 << counter)) == 0) {
                    // clear the i'th bit in the blockerboard if it's clear in the index at bitindex
                    blockerBoard &= ~(1L << i);
                }

                counter++;
            }
        }

        return blockerBoard;
    }

    private static void calcRookMoveBoards(long[][] rookBlockerBoards) {
        /*

        Example: Rook on A1 with second blocker board (rookBlockerBoards[0][1])

        A1 blocker mask
        ***************
        -
        x
        x
        x
        x
        x
        x
        R x x x x x x -

        blocker board - rookBlockerBoards[0][1]
        ***************************************
        -
        -
        -
        -
        -
        -
        -
        R x - - - - - -

        move board for the above blocker board
        **************************************
        x
        x
        x
        x
        x
        x
        x
        R x - - - - - -

        Nun gibt es für das Feld A1 4096 verschiedene move boards,
        wenn der Turm auf A1 steht, welches move board ist dann das richtige?

        Теперь для поля A1 доступно 4096 различных досок ходов(moveBoard),
        если ладья на A1, какая доска для хода правильная?

        1) hole moveMask für den Turm auf A1 (получаем moveMask для ладьи на A1)
        2) allPieces Bitboard & moveMask verknüpfen; man erhält ein blockBoard
           Steht z.B. eine Figur auf B1:
           (если связать все перестановки bitboard и moveMask,
           то получим блочную маску, вот например, если фигура находится на B1)
            -
            -
            -
            -
            -
            -
            -
            - x - - - - - -

            oder: 0000000000000000000000000000000000000000000000000000000000000010 (dec = 2)

        3) blocker board von 2) mit einer magic number multiplizieren
        (осталось перемножить блокирующие доски из 2) пункта на магичкое число)

        */

        for (int square = 0; square < 64; square++) {
            // a move board for each blocker board
            int blockerBoardCount = rookBlockerBoards[square].length;
            rookMagicBoards[square].moveBoards = new long[blockerBoardCount];

            Map<Integer, Integer> controlHashMap = new HashMap<>();

            // for each blocker board ...
            for (int i = 0; i < blockerBoardCount; i++) {
                long moves = 0L;

                // generate the moves
                for (int j = square + 8; j < 64; j += 8) {
                    moves |= BitUtils.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square - 8; j >= 0; j -= 8) {
                    moves |= BitUtils.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square + 1; j % 8 != 0; j++) {
                    moves |= BitUtils.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square - 1; j % 8 != 7 && j >= 0; j--) {
                    moves |= BitUtils.SQUARES[j];
                    if ((rookBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                // generate the hash key
                int key = (int) ((rookBlockerBoards[square][i] * ROOK_MAGIC_NUMBERS[square]) >>> rookMagicBoards[square].shift);
                if (controlHashMap.containsKey(key)) {
                    throw new RuntimeException("Некорректное магическое число!");
                }
                controlHashMap.put(key, key);

                rookMagicBoards[square].moveBoards[key] = moves;
            }

            if (controlHashMap.size() != blockerBoardCount) {
                throw new RuntimeException("Непредвиденная ошибка.");
            }
        }
    }

    private static void calcBishopMoveBoards(long[][] bishopBlockerBoards) {
        for (int square = 0; square < 64; square++) {
            // a move board for each blocker board
            int blockerBoardCount = bishopBlockerBoards[square].length;
            bishopMagicBoards[square].moveBoards = new long[blockerBoardCount];

            Map<Integer, Integer> controlHashMap = new HashMap<>();

            // for each blocker board ...
            for (int i = 0; i < blockerBoardCount; i++) {
                long moves = 0L;

                // generate the moves
                for (int j = square + 7; j % 8 != 7 && j < 64; j += 7) {
                    moves |= BitUtils.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square + 9; j % 8 != 0 && j < 64; j += 9) {
                    moves |= BitUtils.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square - 9; j % 8 != 7 && j >= 0; j -= 9) {
                    moves |= BitUtils.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                for (int j = square - 7; j % 8 != 0 && j >= 0; j -= 7) {
                    moves |= BitUtils.SQUARES[j];
                    if ((bishopBlockerBoards[square][i] & BitUtils.SQUARES[j]) != 0) {
                        break;
                    }
                }

                // generate the hash key
                int key = (int) ((bishopBlockerBoards[square][i] * BISHOP_MAGIC_NUMBERS[square]) >>> bishopMagicBoards[square].shift);
                if (controlHashMap.containsKey(key)) {
                    throw new RuntimeException("Некорретное магическое число!");
                }
                controlHashMap.put(key, key);

                bishopMagicBoards[square].moveBoards[key] = moves;
            }

            if (controlHashMap.size() != blockerBoardCount) {
                throw new RuntimeException("Непредвиденная ошибка.");
            }
        }
    }

    /**
     * Производим предварительные рассчеты по тому как много битов сдвигаются вправо.
     * <p>
     * Например ладье на A1 нужна 12-ти битная база данных, поэтому нам необходимо сделать
     * сдвиг вправо на 52 бита, оставляя индекс в пределах [0 - 4095].
     * For example, a rook on a1 requires a 12-bit database and we therefore have to shift right with 52 bits,
     * leaving an index in the range [0 - 4095].
     */
    private static void calcShifts() {
        for (int i = 0; i < 64; i++) {
            rookMagicBoards[i].shift = 64 - Long.bitCount(rookMagicBoards[i].blockerMask);
            bishopMagicBoards[i].shift = 64 - Long.bitCount(bishopMagicBoards[i].blockerMask);
        }
    }


}
