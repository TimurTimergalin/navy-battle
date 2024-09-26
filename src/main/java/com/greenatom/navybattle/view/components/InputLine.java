package com.greenatom.navybattle.view.components;

import com.greenatom.navybattle.view.utils.Printer;

import java.util.Scanner;

public class InputLine implements Component {
    private final int startX;
    private final int startY;

    public InputLine(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public String getInput() {
        var printer = new Printer();

        printer.goTo(startY, startX).toggleCursor(true);
        var scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        printer.goTo(startY, startX).toggleCursor(false);
        for (int i = 0; i < line.length(); ++i) {
            printer.text(" ");
        }
        return line;
    }

    @Override
    public int getTop() {
        return startY;
    }

    @Override
    public int getLeft() {
        return startX;
    }


    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}
