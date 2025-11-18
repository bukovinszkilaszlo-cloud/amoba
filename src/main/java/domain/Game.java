package domain;

public class Game {
    private final Board board;
    private final Player player;
    private final Player ai;

    public Game(Board board, Player player, Player ai) {
        this.board = board;
        this.player = player;
        this.ai = ai;
    }

    public Board getBoard() { return board; }
    public Player getPlayer() { return player; }
    public Player getAi() { return ai; }
}

