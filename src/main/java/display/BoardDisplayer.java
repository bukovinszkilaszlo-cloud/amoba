package display;

import domain.Board;
import service.ConsoleService;

public class BoardDisplayer {
    private final ConsoleService console;

    public BoardDisplayer(ConsoleService console) {
        this.console = console;
    }

    public void display(Board board) {
        char[][] c = board.getCells();
        System.out.print("  ");
        for (int col = 0; col < board.getCols(); col++)
            System.out.print((char)('A'+col) + " ");
        System.out.println();
        for (int r = 0; r < board.getRows(); r++) {
            System.out.print((r+1) + " ");
            for (int col = 0; col < board.getCols(); col++)
                System.out.print(c[r][col] + " ");
            System.out.println();
        }
    }
}

