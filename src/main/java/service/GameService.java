package service;

import display.BoardDisplayer;
import domain.Board;
import domain.Game;
import domain.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameService {

    private final ConsoleService console;
    private final BoardDisplayer displayer;
    private final Random random = new Random();

    public GameService(ConsoleService console, BoardDisplayer displayer) {
        this.console = console;
        this.displayer = displayer;
    }

    public void start(Game game) {
        Board board = game.getBoard();
        Player player = game.getPlayer();
        Player ai = game.getAi();

        // kezdő X a tábla közepén
        int midR = board.getRows() / 2;
        int midC = board.getCols() / 2;
        board.getCells()[midR][midC] = player.getSymbol();

        while (true) {
            displayer.display(board);
            console.print("Írd be a sor számát, majd az oszlopot, vagy 'esc' a mentéshez:");

            String inputRow = console.readString("Sor: ");
            if (inputRow.equalsIgnoreCase("esc")) {
                saveBoard(board, player, ai);
                continue;
            }

            String inputCol = console.readString("Oszlop: ");
            if (inputCol.equalsIgnoreCase("esc")) {
                saveBoard(board, player, ai);
                continue;
            }

            int r, c;
            try {
                r = Integer.parseInt(inputRow) - 1;
                c = inputCol.toUpperCase().charAt(0) - 'A';
            } catch (Exception e) {
                console.print("Érvénytelen input, próbáld újra!");
                continue;
            }

            if (!board.isInside(r, c) || board.getCells()[r][c] != '.') {
                console.print("Érvénytelen lépés, próbáld újra!");
                continue;
            }

            // játékos lépése
            board.getCells()[r][c] = player.getSymbol();
            if (isWinner(board, player.getSymbol())) {
                displayer.display(board);
                console.print("Nyertél!");
                return;
            }

            // AI lépése
            Move aiMove = generateAiMove(board);
            board.getCells()[aiMove.row][aiMove.col] = ai.getSymbol();
            if (isWinner(board, ai.getSymbol())) {
                displayer.display(board);
                console.print("A gép nyert!");
                return;
            }
        }
    }

    // --- AI: random üres mező kiválasztása ---
    private Move generateAiMove(Board board) {
        List<Move> possible = new ArrayList<>();
        char[][] c = board.getCells();
        for (int r = 0; r < board.getRows(); r++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (c[r][col] == '.') {
                    possible.add(new Move(r, col));
                }
            }
        }
        return possible.get(random.nextInt(possible.size()));
    }

    // --- Győzelemellenőrzés (4 irány, 5 egymás mellett) ---
    private boolean isWinner(Board board, char symbol) {
        char[][] c = board.getCells();
        int R = board.getRows();
        int C = board.getCols();
        int[][] dirs = {
                {1, 0},   // függőleges
                {0, 1},   // vízszintes
                {1, 1},   // átló
                {1, -1}   // mellékátló
        };

        for (int r = 0; r < R; r++) {
            for (int col = 0; col < C; col++) {
                if (c[r][col] != symbol) {
                    continue;
                }
                for (int[] d : dirs) {
                    int count = 0;
                    for (int k = 0; k < 5; k++) {
                        int rr = r + d[0] * k;
                        int cc = col + d[1] * k;
                        if (!board.isInside(rr, cc)) {
                            break;
                        }
                        if (c[rr][cc] == symbol) {
                            count++;
                        }
                    }
                    if (count == 5) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // --- Mentés ESC-re ---
    private void saveBoard(Board board, Player player, Player ai) {
        try (FileWriter writer = new FileWriter("amoba_save.txt")) {
            writer.write("Játékos neve: " + player.getName() + " (" + player.getSymbol() + ")\n");
            writer.write("AI neve: " + ai.getName() + " (" + ai.getSymbol() + ")\n");
            writer.write("Sorok száma: " + board.getRows() + "\n");
            writer.write("Oszlopok száma: " + board.getCols() + "\n");

            writer.write("  ");
            for (int c = 0; c < board.getCols(); c++) {
                writer.write((char) ('A' + c) + " ");
            }
            writer.write("\n");

            for (int r = 0; r < board.getRows(); r++) {
                writer.write((r + 1) + " ");
                for (int c = 0; c < board.getCols(); c++) {
                    writer.write(board.getCells()[r][c] + " ");
                }
                writer.write("\n");
            }
            console.print("Játék elmentve a(z) amoba_save.txt fájlba.");
        } catch (IOException e) {
            console.print("Hiba a mentés során: " + e.getMessage());
        }
    }

    // belső segédosztály az AI lépéshez
    private static class Move {
        private final int row;
        private final int col;

        public Move(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
