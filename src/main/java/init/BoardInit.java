package init;

import domain.Board;
import service.ConsoleService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BoardInit {
    private final ConsoleService console;
    private final String playerName;
    private final String aiName;

    public BoardInit(ConsoleService console, String playerName, String aiName) {
        this.console = console;
        this.playerName = playerName;
        this.aiName = aiName;
    }

    public Board initBoard() {
        int option = console.readInt("1 = fájlból betöltés, 2 = új játék létrehozása");

        if (option == 1) {
            try {
                List<String> lines = Files.readAllLines(Paths.get("amoba_save.txt"));

                int rows = Integer.parseInt(lines.get(2).split(":")[1].trim());
                int cols = Integer.parseInt(lines.get(3).split(":")[1].trim());

                Board board = new Board(rows, cols);

                for (int r = 0; r < rows; r++) {
                    String line = lines.get(r + 5).substring(2).replace(" ", "");
                    for (int c = 0; c < cols; c++) {
                        board.getCells()[r][c] = line.charAt(c);
                    }
                }

                /*for (int r = 0; r < rows; r++) {
                    String line = lines.get(r + 5).replace(" ", "");
                    for (int c = 0; c < cols; c++) {
                        board.getCells()[r][c] = line.charAt(c);
                    }
                }*/

                console.print("Mentett játék betöltve.");
                return board;

            } catch (Exception e) {
                console.print("Hiba a fájl beolvasásakor, üres 10x10 pálya készül.");
                return new Board(10, 10);
            }
        } else {
            int rows = console.readInt("Add meg a sorok számát (4-25):");
            int cols = console.readInt("Add meg az oszlopok számát (4-25):");
            return new Board(rows, cols);
        }
    }
}
