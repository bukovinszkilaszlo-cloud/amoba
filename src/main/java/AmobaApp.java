import java.util.Scanner;

import display.BoardDisplayer;
import domain.Board;
import domain.Game;
import domain.Player;
import init.BoardInit;
import service.BoardService;
import service.ConsoleService;
import service.GameService;

public class AmobaApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleService console = new ConsoleService(scanner);

        console.print("==== AMŐBA JÁTÉK ====");

        String playerName = console.readString("Add meg a játékos nevét:");
        Player player = new Player(playerName, 'X');
        Player ai = new Player("Gép", 'O');

        // Pálya létrehozás vagy betöltés
        BoardInit boardInit = new BoardInit(console, playerName, "Gép");
        Board board = boardInit.initBoard();

        BoardDisplayer displayer = new BoardDisplayer(console);
        BoardService boardService = new BoardService();
        GameService gameService = new GameService(console, displayer, boardService);

        Game game = new Game(board, player, ai);

        console.print("Játék indul...");
        gameService.start(game);
    }
}
