
package service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleServiceTest {

    @Test
    void testPrintOutputsMessage() {
        // GIVEN
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ConsoleService console = new ConsoleService(new Scanner(System.in));

        // WHEN
        console.print("Hello");

        // THEN
        assertTrue(out.toString().contains("Hello"));
    }


    @Test
    void testReadStringReadsLine() {
        // GIVEN
        Scanner scanner = new Scanner("Játékos\n");
        ConsoleService console = new ConsoleService(scanner);

        // WHEN
        String result = console.readString("Add meg a neved:");

        // THEN
        assertEquals("Játékos", result);
    }


    @Test
    void testReadIntSkipsInvalidValues() {
        // GIVEN
        // A bemenet: "alma" → invalid, majd "12" → valid
        Scanner scanner = new Scanner("alma\n12\n");
        ConsoleService console = new ConsoleService(scanner);

        // WHEN
        int value = console.readInt("Kérlek adj meg egy számot:");

        // THEN
        assertEquals(12, value);
    }
}

