package display;

import domain.Board;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class BoardDisplayerTest {

    @Test
    void displayShouldPrintBoardCorrectlyWithoutMocks() {

        //GIVEN: egy 2×2-es Board kézzel beállított cellákkal
        Board board = new Board(2, 2);
        board.getCells()[0][0] = 'X';
        board.getCells()[0][1] = 'O';
        board.getCells()[1][0] = 'O';
        board.getCells()[1][1] = 'X';

        BoardDisplayer underTest = new BoardDisplayer(null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));


        //WHEN: meghívjuk a display metódust
        underTest.display(board);

        String output = out.toString();
        System.setOut(originalOut);


        //THEN: ellenőrizzük, hogy a konzolra a helyes tábla került
        assertTrue(output.contains("  A B"), "Column headers missing");
        assertTrue(output.contains("1 X O"), "First row incorrect");
        assertTrue(output.contains("2 O X"), "Second row incorrect");
    }
}
