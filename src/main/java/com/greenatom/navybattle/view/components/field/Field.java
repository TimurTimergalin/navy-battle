package com.greenatom.navybattle.view.components.field;

import com.greenatom.navybattle.view.components.Component;
import com.greenatom.navybattle.view.utils.Printer;

import java.util.Map;

public class Field implements Component {
    private static final Map<TileStatus, String> tileChar = Map.of(
            TileStatus.FOG, "*",
            TileStatus.HIT, "#",
            TileStatus.MISS, "X",
            TileStatus.HORIZONTAL, "-",
            TileStatus.VERTICAL, "|"
    );
    private final int size;
    private final int originX;
    private final int originY;

    public Field(int originX, int originY, int size) {
        this.size = size;
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
                .goTo(originY, originX)
                .text("    ");  // 4 spaces

        for (char i = 0; i < size; ++i) {
            printer.text(((char) ('A' + i)) + " ");
        }

        printer.goTo(originY + 1, originX);
        drawLine(printer);

        for (int i = 1; i <= size; ++i) {
            printer.goTo(originY + 1 + i, originX);
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
        printer.goTo(originY + size + 2, originX);
        drawLine(printer);
        printer.flush();

        return this;
    }

    public Field changeStatus(int x, int y, TileStatus status) {
        int consoleX = originX + 2 + 2 * x;
        int consoleY = originY + 1 + y;

        new Printer()
                .goTo(consoleY, consoleX)
                .text(tileChar.get(status));
        return this;
    }

    @Override
    public int getTop() {
        return originY;
    }

    @Override
    public int getLeft() {
        return originX;
    }

    @Override
    public int getWidth() {
        return 2 * size + 5;
    }

    @Override
    public int getHeight() {
        return size + 3;
    }
}
