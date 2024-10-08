package com.greenatom.navybattle.view;

import com.greenatom.navybattle.view.components.Chat;
import com.greenatom.navybattle.view.components.InputLine;
import com.greenatom.navybattle.view.components.TextLine;
import com.greenatom.navybattle.view.components.field.Field;
import com.greenatom.navybattle.view.components.field.TileStatus;
import com.greenatom.navybattle.view.utils.Printer;

import java.util.stream.Stream;

public class BattleView {
    private final int size;
    private Field alliedField;
    private Field enemyField;
    private Chat log;
    private InputLine coordinatesInput;
    private TextLine errorMessageLine;

    public BattleView(int size) {
        this.size = size;
    }

    private void greetInChat() {
        log.addMessage("The battle begins!");
        log.addMessage("You go first");
        log.addMessage(
                "Type coordinates ([A-" +
                        (char) ('A' + size - 1) +
                        "][1-" +
                        size +
                        "]) to shoot"
        );
        log.addMessage("Good luck!");
    }

    public void draw() {
        var printer = new Printer().toggleCursor(false).clearAll();
        alliedField = new Field(2, 2, size);
        alliedField.draw();

        enemyField = new Field(alliedField.getRight() + 8, alliedField.getTop(), size);
        enemyField.draw();
        log = new Chat(alliedField.getLeft() + 2, alliedField.getBottom() + 2, 4);
        greetInChat();

        var inputLabel = new TextLine(log.getLeft(), log.getBottom() + 2, "Input coordinates: ");
        coordinatesInput = new InputLine(inputLabel.getRight() + 1, inputLabel.getTop());

        errorMessageLine = new TextLine(inputLabel.getLeft(), inputLabel.getBottom() + 2);
        putAtEnd();
        printer.flush();
    }

    public record ShipTile(int x, int y, boolean isVertical) {}

    private void changeFieldStatus(Field field, ShipTile shipTile) {
        field.changeStatus(
                shipTile.x(),
                shipTile.y(),
                shipTile.isVertical() ? TileStatus.VERTICAL : TileStatus.HORIZONTAL
        );
    }

    public void drawAlliedBattlefield(Stream<ShipTile> tiles) {
        tiles.forEach(tile -> changeFieldStatus(alliedField, tile));
        putAtEnd();
    }

    public void updateAlliedBattlefield(int x, int y, TileStatus tileStatus) {
        alliedField.changeStatus(x, y, tileStatus);
        putAtEnd();
    }

    public void updateEnemyBattlefield(int x, int y, TileStatus tileStatus) {
        enemyField.changeStatus(x, y, tileStatus);
        putAtEnd();
    }

    public void logMessage(String message) {
        log.addMessage(message);
        putAtEnd();
        new Printer().flush();
    }

    public String getCoordinates() {
        String result = coordinatesInput.getInput();
        putAtEnd();
        new Printer().flush();
        return result;
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
