package service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import display.BoardDisplayer;
import domain.Board;
import domain.Game;
import domain.Player;

public class GameService {

    private final ConsoleService console;
    private final BoardDisplayer displayer;
    private final Random random = new Random();

    public GameService(ConsoleService console, BoardDisplayer displayer) {
        this.console = console;
        this.displayer = displayer;
    }

    // ---------------------------------------------------------------------
    // A tábla alapján meghatározza, hogy ki következik (X vagy O)
    // ---------------------------------------------------------------------
    private char getNextPlayer(Board board) {
        int countX = 0;
        int countO = 0;

        char[][] cells = board.getCells();
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (cells[r][c] == 'X') {
                    countX++;
                }
                if (cells[r][c] == 'O') {
                    countO++;
                }
            }
        }

        return (countX == countO) ? 'X' : 'O';
    }

    // ---------------------------------------------------------------------
    // Ellenőrzi, hogy teljesen üres-e a tábla
    // ---------------------------------------------------------------------
    private boolean isBoardEmpty(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCells()[r][c] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    // ---------------------------------------------------------------------
    // START (fő játékciklus)
    // ---------------------------------------------------------------------
    public void start(Game game) {

        Board board = game.getBoard();
        Player player = game.getPlayer();
        Player ai = game.getAi();

        if (isBoardEmpty(board)) {
            int midR = board.getRows() / 2;
            int midC = board.getCols() / 2;
            board.getCells()[midR][midC] = player.getSymbol();

            Move aiMove = generateAiMove(board);
            board.getCells()[aiMove.row][aiMove.col] = ai.getSymbol();
        }

        while (true) {

            displayer.display(board);
            char next = getNextPlayer(board);

            // AI köre
            if (next == ai.getSymbol()) {

                console.print("AI következik...");

                Move aiMove = generateAiMove(board);
                board.getCells()[aiMove.row][aiMove.col] = ai.getSymbol();

                if (isWinner(board, ai.getSymbol())) {
                    displayer.display(board);
                    console.print("A gép nyert!");
                    return;
                }

                continue;
            }

            // Játékos köre
            console.print("Következel! Írd be a sor számát, majd az oszlopot, vagy 'esc' a mentéshez:");

            String inputRow = console.readString("Sor: ");
            if (inputRow.equalsIgnoreCase("esc")) {
                String saveAns = console.readString("Szeretnéd menteni a játékot? (i/n): ");
                if (saveAns.equalsIgnoreCase("i")) {
                    saveBoard(board, player, ai);
                    console.print("Játék elmentve. Kilépés...");
                    return;
                } else {
                    console.print("Nincs mentés. A játék folytatódik...");
                    continue;
                }
            }

            String inputCol = console.readString("Oszlop: ");
            if (inputCol.equalsIgnoreCase("esc")) {
                String saveAns = console.readString("Szeretnéd menteni a játékot? (i/n): ");
                if (saveAns.equalsIgnoreCase("i")) {
                    saveBoard(board, player, ai);
                    console.print("Játék elmentve. Kilépés...");
                    return;
                } else {
                    console.print("Nincs mentés. A játék folytatódik...");
                    continue;
                }
            }

            int r;
            int c;

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

            board.getCells()[r][c] = player.getSymbol();
            if (isWinner(board, player.getSymbol())) {
                displayer.display(board);
                console.print("Nyertél!");
                return;
            }
        }
    }

    // ---------------------------------------------------------------------
    // AI lépés generálása (random)
    // ---------------------------------------------------------------------
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

    // ---------------------------------------------------------------------
    // Győzelem ellenőrzése
    // ---------------------------------------------------------------------
    private boolean isWinner(Board board, char symbol) {
        char[][] c = board.getCells();
        int rows = board.getRows();
        int cols = board.getCols();

        int[][] dirs = {
                {1, 0},
                {0, 1},
                {1, 1},
                {1, -1}
        };

        for (int r = 0; r < rows; r++) {
            for (int col = 0; col < cols; col++) {

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

    // ---------------------------------------------------------------------
    // Mentés fájlba
    // ---------------------------------------------------------------------
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

    // ---------------------------------------------------------------------
    // AI segédosztály
    // ---------------------------------------------------------------------
    private static class Move {
        private final int row;
        private final int col;

        public Move(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
