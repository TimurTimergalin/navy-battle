package com.greenatom.navybattle.view;

import com.greenatom.navybattle.view.components.InputLine;
import com.greenatom.navybattle.view.components.TextLine;
import com.greenatom.navybattle.view.components.field.Field;
import com.greenatom.navybattle.view.components.field.TileStatus;
import com.greenatom.navybattle.view.utils.Printer;

import java.util.ArrayList;
import java.util.List;

public class ShipPlacementView {
    private final int size;
    private final List<Integer> allowedShips;
    private Field battlefield;
    private InputLine inputLine;
    private TextLine errorMessageLine;

    public ShipPlacementView(int size, List<Integer> allowedShips) {
        this.size = size;
        this.allowedShips = new ArrayList<>(allowedShips);  // Во избежание изменений извне
    }

    public void updateBattlefield(int x, int y, TileStatus status) {
        battlefield.changeStatus(x, y, status);
    }

    public void draw() {
        var printer = new Printer().toggleCursor(false).clearAll();
        battlefield = new Field(2, 2, size);
        battlefield.draw();

        var inputLabel = new TextLine(battlefield.getLeft(), battlefield.getBottom() + 2, "Your command: ");
        inputLine = new InputLine(inputLabel.getRight() + 1, inputLabel.getTop());

        errorMessageLine = new TextLine(inputLabel.getLeft(), inputLabel.getBottom() + 1);
        drawHints();
        putAtEnd();
        printer.flush();
    }

    private void drawHints() {
        var lastHint = new TextLine(
                battlefield.getRight() + 4,
                battlefield.getTop() + 1,
                "You may use the following commands:"
        );

        lastHint = new TextLine(
                lastHint.getLeft(),
                lastHint.getBottom() + 2,
                "put (" +
                        String.join("|", allowedShips.stream().map(Object::toString).toList()) +
                        ") [A-" +
                        (char) ('A' + size - 1) +
                        "][1-" +
                        size +
                        "] (U|D|L|R)"
        );

        lastHint = new TextLine(
                lastHint.getLeft(),
                lastHint.getBottom() + 2,
                "undo"
        );

        lastHint = new TextLine(
                lastHint.getLeft(),
                lastHint.getBottom() + 2,
                "start"
        );

        new TextLine(
                lastHint.getLeft(),
                lastHint.getBottom() + 2,
                "exit"
        );
    }

    public String getCommand() {
        String inp = inputLine.getInput();
        putAtEnd();
        new Printer().flush();
        return inp;
    }

    public void setErrorMessage(String message) {
        errorMessageLine.setText(message);
        putAtEnd();
        new Printer().flush();
    }

    private void putAtEnd() {
        new Printer().goTo(errorMessageLine.getBottom() + 1, 1);
    }
}
