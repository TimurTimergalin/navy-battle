package com.greenatom.navybattle.view.utils;

public class Printer {
    public Printer goTo(int n, int m) {
        System.out.printf("\033[%d;%dH", n, m);
        return this;
    }

    public Printer flush() {
        System.out.flush();
        return this;
    }

    public Printer text(String s) {
        System.out.print(s);
        return this;
    }

    public Printer clearAll() {
        System.out.print("\033[H\033[2J");
        return this;
    }

    public Printer clearLine() {
        System.out.print("\033[2K");
        return this;
    }

    public Printer toggleCursor(boolean on) {
        char command = on ? 'h' : 'l';
        System.out.print("\033[?25" + command);
        return this;
    }
}
