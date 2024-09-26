package com.greenatom.navybattle.view.components.field;

import com.greenatom.navybattle.view.utils.Printer;

import java.util.Map;

public class Field {
    private static final int size = 10;

    private static final Map<TileStatus, String> tileChar = Map.of(
            TileStatus.FOG, "*",
            TileStatus.HIT, "#",
            TileStatus.MISS, "X",
            TileStatus.HORIZONTAL, "-",
            TileStatus.VERTICAL, "|"
    );

    private final int originX;
    private final int originY;

    public Field(int originX, int originY) {
        this.originX = originX;
        this.originY = originY;
    }

    private void drawLine(Printer printer) {
        printer.text("  +");
        for (int i = 0; i < 2 * size + 1; ++i) {
            printer.text("-");
        }
        printer.text("+");
    }

    public Field draw() {
        var printer = new Printer()
                .goTo(originX, originY)
                .text("    ");  // 4 spaces

        for (char i = 0; i < size; ++i) {
            printer.text(((char)('A' + i)) + " ");
        }

        printer.goTo(originX + 1, originY);
        drawLine(printer);

        for (int i = 1; i <= size; ++i) {
            printer.goTo(originX + 1 + i, originY);
            if (i < 10) {
                printer.text(" ");
            }
            printer.text(i + "| ");
            for (int j = 0; j < size; ++j) {
                printer
                        .text(tileChar.get(TileStatus.FOG))
                        .text(" ");
            }
            printer.text("|");
        }
        printer.goTo(originX + size + 2, originY);
        drawLine(printer);
        printer.flush();

        return this;
    }

    public Field changeStatus(int x, int y, TileStatus status) {
        int consoleX = originX + 1 + x;
        int consoleY = originY + 2 + 2 * y;

        new Printer()
                .goTo(consoleX, consoleY)
                .text(tileChar.get(status));
        return this;
    }
}
