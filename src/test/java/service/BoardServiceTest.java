package service;

import domain.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardServiceTest
{
    private BoardService boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardService();
    }

    @Test
    void isInside_ReturnsTrue_ForValidCoordinates() {
        Board board = new Board(3, 3);

        assertTrue(boardService.isInside(board, 0, 0));
        assertTrue(boardService.isInside(board, 2, 2));
        assertTrue(boardService.isInside(board, 1, 1));
    }

    @Test
    void isInside_ReturnsFalse_ForNegativeRow() {
        Board board = new Board(3, 3);

        assertFalse(boardService.isInside(board, -1, 0));
    }

    @Test
    void isInside_ReturnsFalse_ForNegativeColumn() {
        Board board = new Board(3, 3);

        assertFalse(boardService.isInside(board, 0, -1));
    }

    @Test
    void isInside_ReturnsFalse_ForRowOutOfBounds() {
        Board board = new Board(3, 3);

        assertFalse(boardService.isInside(board, 3, 0));
    }

    @Test
    void isInside_ReturnsFalse_ForColumnOutOfBounds() {
        Board board = new Board(3, 3);

        assertFalse(boardService.isInside(board, 0, 3));
    }

    @Test
    void isFull_ReturnsFalse_ForEmptyBoard() {
        Board board = new Board(3, 3);

        assertFalse(boardService.isFull(board));
    }

    @Test
    void isFull_ReturnsFalse_ForPartiallyFilledBoard() {
        Board board = new Board(3, 3);
        board.getCells()[0][0] = 'X';

        assertFalse(boardService.isFull(board));
    }

    @Test
    void isFull_ReturnsTrue_ForCompletelyFilledBoard() {
        Board board = new Board(2, 2);
        board.getCells()[0][0] = 'X';
        board.getCells()[0][1] = 'O';
        board.getCells()[1][0] = 'O';
        board.getCells()[1][1] = 'X';

        assertTrue(boardService.isFull(board));
    }
}
