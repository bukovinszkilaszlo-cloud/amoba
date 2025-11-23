package init;

import domain.Board;
import org.junit.jupiter.api.Test;
import service.ConsoleService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardInitTest {

    @Test
    void testNewBoardCreation() {
        // GIVEN
        ConsoleService console = mock(ConsoleService.class);

        when(console.readInt(anyString()))
                .thenReturn(2)   // új játék
                .thenReturn(10)  // sorok
                .thenReturn(10); // oszlopok

        BoardInit init = new BoardInit(console, "player", "ai");

        // WHEN
        Board board = init.initBoard();

        // THEN
        assertEquals(10, board.getRows());
        assertEquals(10, board.getCols());
    }


    @Test
    void testLoadBoardFromFile() throws Exception {
        // GIVEN — ideiglenes fájl létrehozása
        Path saveFile = Path.of("amoba_save.txt");

        List<String> content = List.of(
                "Amőba mentés:",
                "Metaadatok:",
                "Rows: 3",
                "Cols: 3",
                "Tábla:",
                "1 X O X",
                "2 O X O",
                "3 X O X"
        );

        Files.write(saveFile, content);

        ConsoleService console = mock(ConsoleService.class);
        when(console.readInt(anyString())).thenReturn(1); // fájlból töltés

        BoardInit init = new BoardInit(console, "player", "ai");

        // WHEN
        Board board = init.initBoard();

        // THEN
        assertEquals(3, board.getRows());
        assertEquals(3, board.getCols());

        assertArrayEquals(new char[]{'X','O','X'}, board.getCells()[0]);
        assertArrayEquals(new char[]{'O','X','O'}, board.getCells()[1]);
        assertArrayEquals(new char[]{'X','O','X'}, board.getCells()[2]);

        // CLEANUP
        Files.deleteIfExists(saveFile);
    }


    @Test
    void testLoadBoardFromFileFails() throws Exception {
        // GIVEN
        // Biztosítsuk, hogy a fájl ne létezzen
        Path saveFile = Path.of("amoba_save.txt");
        Files.deleteIfExists(saveFile);

        ConsoleService console = mock(ConsoleService.class);
        when(console.readInt(anyString())).thenReturn(1); // fájlból töltés opció

        BoardInit init = new BoardInit(console, "player", "ai");

        // WHEN
        Board board = init.initBoard();

        // THEN
        assertEquals(10, board.getRows());
        assertEquals(10, board.getCols());

        verify(console).print("Hiba a fájl beolvasásakor, üres 10x10 pálya készül.");
    }


    @Test
    void testInvalidRowsThenValid() {
        // GIVEN
        ConsoleService console = mock(ConsoleService.class);

        when(console.readInt(anyString()))
                .thenReturn(2)    // opció = új játék
                .thenReturn(2)    // sor: HIBÁS (2 < 4)
                .thenReturn(6)    // sor: jó
                .thenReturn(10);  // oszlop: jó

        BoardInit init = new BoardInit(console, "player", "ai");

        // WHEN
        Board board = init.initBoard();

        // THEN
        assertEquals(6, board.getRows());
        assertEquals(10, board.getCols());

        verify(console).print("Hibás érték! 4 és 25 között adj meg számot!");
    }


    @Test
    void testInvalidColsThenValid() {
        // GIVEN
        ConsoleService console = mock(ConsoleService.class);

        when(console.readInt(anyString()))
                .thenReturn(2)    // opció = új játék
                .thenReturn(6)    // sor: érvényes
                .thenReturn(100)  // oszlop: HIBÁS (100 > 25)
                .thenReturn(7);   // oszlop: érvényes

        BoardInit init = new BoardInit(console, "player", "ai");

        // WHEN
        Board board = init.initBoard();

        // THEN
        assertEquals(6, board.getRows());
        assertEquals(7, board.getCols());

        verify(console).print("Hibás érték! 4 és 25 között adj meg számot!");
    }

}
