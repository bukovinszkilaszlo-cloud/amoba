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
    private final BoardService boardService;
    private final Random random = new Random();

    public GameService(ConsoleService console, BoardDisplayer displayer, BoardService boardService) {
        this.console = console;
        this.displayer = displayer;
        this.boardService = boardService;
    }

    // Megnézi, hogy a megadott üres mező szomszédos-e egy nem üressel
    boolean hasNeighbor(Board board, int row, int col) {
        if (board.getCells()[row][col] != '.') {
            return false;
        }
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < dr.length; i++) {
            int rr = row + dr[i];
            int cc = col + dc[i];
            if (boardService.isInside(board, rr, cc) && board.getCells()[rr][cc] != '.') {
                return true;
            }
        }
        return false;
    }

    // Meghatározza, ki jön: ha X és O száma egyenlő → X, különben O
    char getNextPlayer(Board board) {
        int countX = 0;
        int countO = 0;
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCells()[r][c] == 'X') {
                    countX++;
                }
                if (board.getCells()[r][c] == 'O') {
                    countO++;
                }
            }
        }
        return (countX == countO) ? 'X' : 'O';
    }

    // Ellenőrzi, hogy teljesen üres-e a pálya
    boolean isBoardEmpty(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCells()[r][c] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    // A fő játékhurok
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

            if (next == ai.getSymbol()) {
                console.print("AI következik...");
                Move aiMove = generateAiMove(board);
                board.getCells()[aiMove.row][aiMove.col] = ai.getSymbol();
                if (isWinner(board, ai.getSymbol())) {
                    displayer.display(board);
                    console.print("A gép nyert!");
                    return;
                }

                if (boardService.isFull(board)) {
                    displayer.display(board);
                    console.print("Döntetlen! A tábla megtelt.");
                    return;
                }

                continue;
            }

            console.print("Következel! (esc = kilépés/mentés)");
            String inputRow = console.readString("Sor:");
            if (inputRow.equalsIgnoreCase("esc")) {
                if (promptSave(board, player, ai)) {
                    return;
                }
                continue;
            }

            String inputCol = console.readString("Oszlop:");
            if (inputCol.equalsIgnoreCase("esc")) {
                if (promptSave(board, player, ai)) {
                    return;
                }
                continue;
            }

            int r;
            int c;
            try {
                r = Integer.parseInt(inputRow) - 1;
                c = inputCol.toUpperCase().charAt(0) - 'A';
            } catch (NumberFormatException e) {
                console.print("Érvénytelen input!");
                continue;
            }

            if (!boardService.isInside(board, r, c) || board.getCells()[r][c] != '.') {
                console.print("Érvénytelen lépés!");
                continue;
            }

            if (!hasNeighbor(board, r, c)) {
                console.print("Csak már lerakott mező mellé tehetsz! Próbáld újra!");
                continue;
            }

            board.getCells()[r][c] = player.getSymbol();
            if (isWinner(board, player.getSymbol())) {
                displayer.display(board);
                console.print("Nyertél!");
                return;
            }

            if (boardService.isFull(board)) {
                displayer.display(board);
                console.print("Döntetlen! A tábla megtelt.");
                return;
            }
        }
    }

    // AI lépést generál (szomszédos mezőkre preferálva)
    Move generateAiMove(Board board) {
        List<Move> possible = new ArrayList<>();
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCells()[r][c] == '.' && hasNeighbor(board, r, c)) {
                    possible.add(new Move(r, c));
                }
            }
        }
        // Ha nincs szomszédos üres mező → bárhova tehet
        if (possible.isEmpty()) {
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    if (board.getCells()[r][c] == '.') {
                        possible.add(new Move(r, c));
                    }
                }
            }
        }
        return possible.get(random.nextInt(possible.size()));
    }

    // 5 egymás melletti szimbólumot keres 4 irányban
    boolean isWinner(Board board, char symbol) {
        int[][] dirs = {
                {1, 0},
                {0, 1},
                {1, 1},
                {1, -1}
        };
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCells()[r][c] != symbol) {
                    continue;
                }
                for (int[] d : dirs) {
                    int count = 0;
                    for (int k = 0; k < 5; k++) {
                        int rr = r + d[0] * k;
                        int cc = c + d[1] * k;
                        if (!boardService.isInside(board, rr, cc)) {
                            break;
                        }
                        if (board.getCells()[rr][cc] == symbol) {
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

    // Bekérdezi a mentést – tesztelhető
    boolean promptSave(Board board, Player player, Player ai) {
        String ans = console.readString("Szeretnéd menteni? (i/n):");
        if (ans.equalsIgnoreCase("i")) {
            saveBoard(board, player, ai);
            console.print("Játék elmentve. Kilépés...");
            return true;
        }
        console.print("Nincs mentés. Folytatás...");
        return false;
    }

    // Kiírja fájlba a mentést
    void saveBoard(Board board, Player player, Player ai) {
        try (FileWriter writer = new FileWriter("amoba_save.txt")) {
            writer.write("Játékos neve: " + player.getName() + " (" + player.getSymbol() + ")" + System.lineSeparator());
            writer.write("AI neve: " + ai.getName() + " (" + ai.getSymbol() + ")" + System.lineSeparator());
            writer.write("Sorok száma: " + board.getRows() + System.lineSeparator());
            writer.write("Oszlopok száma: " + board.getCols() + System.lineSeparator());

            writer.write("  ");
            for (int c = 0; c < board.getCols(); c++) {
                writer.write((char) ('A' + c) + " ");
            }
            writer.write(System.lineSeparator());

            for (int r = 0; r < board.getRows(); r++) {
                writer.write((r + 1) + " ");
                for (int c = 0; c < board.getCols(); c++) {
                    writer.write(board.getCells()[r][c] + " ");
                }
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            console.print("Hiba a mentés során: " + e.getMessage());
        }
    }

    static class Move {
        final int row;
        final int col;

        Move(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
