package com.greenatom.navybattle.view.components;

import com.greenatom.navybattle.view.utils.Printer;

import java.util.Scanner;

public class InputLine {
    private final int startX;
    private final int startY;

    public InputLine(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public String getInput() {
        var printer = new Printer();

        printer.goTo(startX, startY).toggleCursor(true);
        var scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        printer.goTo(startX, startY).toggleCursor(false);
        for (int i = 0; i < line.length(); ++i) {
            printer.text(" ");
        }
        return line;
    }
}
