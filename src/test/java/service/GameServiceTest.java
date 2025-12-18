
package service;

import domain.Board;
import domain.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class GameServiceTest {

    // GIVEN — WHEN — THEN
    @Test
    void testIsBoardEmpty_EmptyBoard() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);

        // WHEN
        boolean result = gs.isBoardEmpty(board);

        // THEN
        assertTrue(result);
    }

    @Test
    void testIsBoardEmpty_NotEmpty() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);
        board.getCells()[1][1] = 'X';

        // WHEN
        boolean result = gs.isBoardEmpty(board);

        // THEN
        assertFalse(result);
    }

    @Test
    void testHasNeighbor_NoNeighbors() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);   // minden '.'

        // WHEN
        boolean result = gs.hasNeighbor(board, 1, 1);

        // THEN
        assertFalse(result);
    }

    @Test
    void testHasNeighbor_HasAdjacent() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);
        board.getCells()[0][0] = 'X'; // szomszéd

        // WHEN
        boolean result = gs.hasNeighbor(board, 1, 1);

        // THEN
        assertTrue(result);
    }

    @Test
    void testHasNeighbor_CellNotEmpty_ReturnsFalse() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);
        board.getCells()[1][1] = 'X'; // nem üres

        // WHEN
        boolean result = gs.hasNeighbor(board, 1, 1);

        // THEN
        assertFalse(result);
    }

    @Test
    void testGetNextPlayer_EmptyBoard_ReturnsX() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);

        // WHEN
        char next = gs.getNextPlayer(board);

        // THEN
        assertEquals('X', next);
    }

    @Test
    void testGetNextPlayer_EqualCount_ReturnsX() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);

        board.getCells()[0][0] = 'X';
        board.getCells()[0][1] = 'O';

        // WHEN
        char next = gs.getNextPlayer(board);

        // THEN
        assertEquals('X', next);
    }

    @Test
    void testGetNextPlayer_MoreX_ReturnsO() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);

        board.getCells()[0][0] = 'X';
        board.getCells()[0][1] = 'X';
        board.getCells()[0][2] = 'O';

        // WHEN
        char next = gs.getNextPlayer(board);

        // THEN
        assertEquals('O', next);
    }

    @Test
    void testIsWinner_FiveInRow() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(5, 5);

        for (int i = 0; i < 5; i++) {
            board.getCells()[2][i] = 'X';
        }

        // WHEN
        boolean winner = gs.isWinner(board, 'X');

        // THEN
        assertTrue(winner);
    }

    @Test
    void testIsWinner_NoFiveInRow() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(5, 5);

        board.getCells()[2][0] = 'X';
        board.getCells()[2][1] = 'X';
        board.getCells()[2][2] = 'X';
        board.getCells()[2][3] = 'X';
        // csak 4 db

        // WHEN
        boolean winner = gs.isWinner(board, 'X');

        // THEN
        assertFalse(winner);
    }

    @Test
    void testGenerateAiMove_EmptyBoard_ReturnsAnyEmptyCell() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3); // mind '.' → bárhová léphet
        BoardService boardService = new BoardService();

        // WHEN
        GameService.Move move = gs.generateAiMove(board);

        // THEN
        assertTrue(boardService.isInside(board, move.row, move.col));
        assertEquals('.', board.getCells()[move.row][move.col]);
    }

    @Test
    void testGenerateAiMove_PrefersNeighborCells() {
        // GIVEN
        GameService gs = new GameService(null, null, new BoardService());
        Board board = new Board(3, 3);

        board.getCells()[1][1] = 'X'; // ez mellé fog lépni

        // WHEN
        GameService.Move move = gs.generateAiMove(board);

        // THEN
        boolean isNeighbor =
                Math.abs(move.row - 1) <= 1 &&
                        Math.abs(move.col - 1) <= 1 &&
                        !(move.row == 1 && move.col == 1);

        assertTrue(isNeighbor);
    }

    @Test
    void testPromptSaveYes() {
        // GIVEN
        ConsoleService console = mock(ConsoleService.class);
        GameService gs = new GameService(console, null, new BoardService());

        Board board = new Board(3, 3);
        Player player = new Player("P", 'X');
        Player ai = new Player("AI", 'O');

        when(console.readString(anyString())).thenReturn("i");

        // WHEN
        boolean result = gs.promptSave(board, player, ai);

        // THEN
        assertTrue(result);
    }

    @Test
    void testPromptSaveNo() {
        // GIVEN
        ConsoleService console = mock(ConsoleService.class);
        GameService gs = new GameService(console, null, new BoardService());

        Board board = new Board(3, 3);
        Player player = new Player("P", 'X');
        Player ai = new Player("AI", 'O');

        when(console.readString(anyString())).thenReturn("n");

        // WHEN
        boolean result = gs.promptSave(board, player, ai);

        // THEN
        assertFalse(result);
    }

}
