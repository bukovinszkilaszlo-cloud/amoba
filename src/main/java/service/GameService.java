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

    //---------------------------------------------------------------------
    // ÚJ METÓDUS: A tábla alapján meghatározza, hogy ki következik (X vagy O)
    //---------------------------------------------------------------------
    private char getNextPlayer(Board board) {
        int countX = 0;
        int countO = 0;

        char[][] cells = board.getCells();
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (cells[r][c] == 'X') countX++;
                if (cells[r][c] == 'O') countO++;
            }
        }

        // Ha ugyanannyi X mint O → X következik
        // Ha egyel több X mint O → O következik
        return (countX == countO) ? 'X' : 'O';
    }

    //---------------------------------------------------------------------
    // ÚJ METÓDUS: Ellenőrzi, hogy teljesen üres-e a tábla (új játék)
    //---------------------------------------------------------------------
    private boolean isBoardEmpty(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCells()[r][c] != '.') {
                    return false; // talált már jelet → nem üres
                }
            }
        }
        return true;
    }

    //---------------------------------------------------------------------
    // START METÓDUS (fő játékciklus)
    //---------------------------------------------------------------------
    public void start(Game game) {

        Board board = game.getBoard();
        Player player = game.getPlayer();
        Player ai = game.getAi();

        //-----------------------------------------------------------------
        // ÚJ LOGIKA: csak akkor rak X-et középre és lép az AI,
        // ha a tábla teljesen ÜRES → azaz tényleg új játék indul
        //-----------------------------------------------------------------
        if (isBoardEmpty(board)) {

            // RÉGI KÓD:
            // int midR = board.getRows() / 2;
            // int midC = board.getCols() / 2;
            // board.getCells()[midR][midC] = player.getSymbol();

            // ÚJ KÓD: X középre
            int midR = board.getRows() / 2;
            int midC = board.getCols() / 2;
            board.getCells()[midR][midC] = player.getSymbol();

            // ÚJ KÓD: középre rakás után az AI azonnal jön
            Move aiMove = generateAiMove(board);
            board.getCells()[aiMove.row][aiMove.col] = ai.getSymbol();
        }

        //-----------------------------------------------------------------
        // Fő Játék Ciklus
        //-----------------------------------------------------------------
        while (true) {

            displayer.display(board);

            // KI KÖVETKEZIK?
            char next = getNextPlayer(board);

            //-----------------------------------------------------------------
            // AI KÖRE
            //-----------------------------------------------------------------
            if (next == ai.getSymbol()) {

                console.print("AI következik...");

                Move aiMove = generateAiMove(board);
                board.getCells()[aiMove.row][aiMove.col] = ai.getSymbol();

                if (isWinner(board, ai.getSymbol())) {
                    displayer.display(board);
                    console.print("A gép nyert!");
                    return;
                }

                continue; // AI lépése után vissza a ciklus elejére
            }

            //-----------------------------------------------------------------
            // JÁTÉKOS KÖRE
            //-----------------------------------------------------------------
            console.print("Következel! Írd be a sor számát, majd az oszlopot, vagy 'esc' a mentéshez:");

            // --- SOR BEOLVASÁSA ---
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

            // --- OSZLOP BEOLVASÁSA ---
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

            // Játékos lépés konvertálása
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

            // Játékos lépése
            board.getCells()[r][c] = player.getSymbol();
            if (isWinner(board, player.getSymbol())) {
                displayer.display(board);
                console.print("Nyertél!");
                return;
            }
        }
    }

    //---------------------------------------------------------------------
    // AI LÉPÉS (random)
    //---------------------------------------------------------------------
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

    //---------------------------------------------------------------------
    // GYŐZELEM ELLENŐRZÉSE (5 egymás mellett)
    //---------------------------------------------------------------------
    private boolean isWinner(Board board, char symbol) {
        char[][] c = board.getCells();
        int R = board.getRows();
        int C = board.getCols();
        int[][] dirs = {
                {1, 0},
                {0, 1},
                {1, 1},
                {1, -1}
        };

        for (int r = 0; r < R; r++) {
            for (int col = 0; col < C; col++) {
                if (c[r][col] != symbol) continue;

                for (int[] d : dirs) {
                    int count = 0;
                    for (int k = 0; k < 5; k++) {
                        int rr = r + d[0] * k;
                        int cc = col + d[1] * k;

                        if (!board.isInside(rr, cc)) break;
                        if (c[rr][cc] == symbol) count++;
                    }
                    if (count == 5) return true;
                }
            }
        }
        return false;
    }

    //---------------------------------------------------------------------
    // MENTÉS FÁJLBA
    //---------------------------------------------------------------------
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

    //---------------------------------------------------------------------
    // AI belső segédosztály
    //---------------------------------------------------------------------
    private static class Move {
        private final int row;
        private final int col;

        public Move(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
