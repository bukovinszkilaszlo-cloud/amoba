package service;

import domain.Board;

public class BoardService {

    public boolean isInside(Board board, int r, int c) {
        return r >= 0 && r < board.getRows()
                && c >= 0 && c < board.getCols();
    }

    public boolean isFull(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCells()[r][c] == '.') {
                    return false;
                }
            }
        }
        return true;
    }
}
