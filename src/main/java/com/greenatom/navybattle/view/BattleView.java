package com.greenatom.navybattle.view;

import com.greenatom.navybattle.view.components.Chat;
import com.greenatom.navybattle.view.components.InputLine;
import com.greenatom.navybattle.view.components.TextLine;
import com.greenatom.navybattle.view.components.field.Field;
import com.greenatom.navybattle.view.utils.Printer;

public class BattleView {
    private Field alliedField;
    private Field enemyField;
    private Chat log;
    private InputLine coordinatesInput;
    private TextLine errorMessageLine;

    private final int size;

    public BattleView(int size) {
        this.size = size;
    }


    public Field getAlliedField() {
        return alliedField;
    }

    public Field getEnemyField() {
        return enemyField;
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
        new Printer().toggleCursor(false).clearAll();
        alliedField = new Field(2, 2, size);
        alliedField.draw();

        enemyField = new Field(alliedField.getRight() + 8, alliedField.getTop(), size);
        enemyField.draw();
        log = new Chat(alliedField.getLeft(), alliedField.getBottom() + 1, 4);
        greetInChat();

        var inputLabel = new TextLine(log.getLeft(), log.getBottom() + 2, "Input coordinates: ");
        coordinatesInput = new InputLine(inputLabel.getRight() + 1, inputLabel.getTop());

        errorMessageLine = new TextLine(inputLabel.getLeft(), inputLabel.getBottom() + 2);
        putAtEnd();
    }

    public void logMessage(String message) {
        log.addMessage(message);
        putAtEnd();
    }

    public String getCoordinates() {
        String result = coordinatesInput.getInput();
        putAtEnd();
        return result;
    }

    public void setErrorMessage(String message) {
        errorMessageLine.setText(message);
        putAtEnd();
    }

    private void putAtEnd() {
        new Printer().goTo(errorMessageLine.getBottom() + 1, 1);
    }
}
