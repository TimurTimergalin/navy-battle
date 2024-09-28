package com.greenatom.navybattle.client.player;

import com.greenatom.navybattle.client.Client;
import com.greenatom.navybattle.client.player.placement.NoShipsException;
import com.greenatom.navybattle.client.player.placement.NotEnoughShipsPlacedException;
import com.greenatom.navybattle.client.player.placement.ShipPlacementManager;
import com.greenatom.navybattle.client.player.placement.UnavailableSizeException;
import com.greenatom.navybattle.ships.MisplacedShipException;
import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;
import com.greenatom.navybattle.view.BattleView;
import com.greenatom.navybattle.view.ShipPlacementView;

import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PlayerClient implements Client {
    private final ShipPlacementView shipPlacementView;
    private final BattleView battleView;
    private final ShipPlacementManager shipPlacementManager;

    public PlayerClient(ShipPlacementView shipPlacementView, BattleView battleView, ShipPlacementManager shipPlacementManager) {
        this.shipPlacementView = shipPlacementView;
        this.battleView = battleView;
        this.shipPlacementManager = shipPlacementManager;
    }

    private static final Pattern putMatch = Pattern.compile("\\s*put\\s+(?<size>[1-9][0-9]*)\\s+(?<origin>[A-Za-z][1-9][0-9]*)(?:\\s+(?<direction>[UDLR]))?\\s*");

    // Возвращает null, если парсинг не удался
    private static Ship.Coordinates parseCoordinates(String s) {
        int x;
        int y;

        if ('A' <= s.charAt(0) && s.charAt(0) <= 'Z') {
            x = s.charAt(0) - 'A' + 1;
        } else if ('a' <= s.charAt(0) && s.charAt(0) <= 'z') {
            x = s.charAt(0) - 'a' + 1;
        } else {
            return null;
        }

        try {
            y = Integer.parseInt(s.substring(1));
            return new Ship.Coordinates(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Возвращает true, если надо начинать игру
    private ShipPlacement parseCommand(String command) {
        var matcher = putMatch.matcher(command);

        Consumer<ShipPlacementManager.CoordinateUpdate> updater = update ->
                shipPlacementView.getBattlefield().changeStatus(update.x(), update.y(), update.status());

        if (matcher.matches()) {  // Команда put
            int size = Integer.parseInt(matcher.group("size"));
            Ship.Coordinates coordinates = parseCoordinates(matcher.group("origin"));

            if (coordinates == null) {
                shipPlacementView.setErrorMessage("Invalid coordinates format");
                return null;
            }

            String direction = matcher.group("direction");
            if (size != 1 && direction == null) {
                shipPlacementView.setErrorMessage("You must specify a direction of the ship (U|D|L|R)");
                return null;
            }

            Ship.Direction dir = switch (direction) {
                case "D" -> Ship.Direction.DOWN;
                case "L" -> Ship.Direction.LEFT;
                case "R" -> Ship.Direction.RIGHT;
                case null, default -> Ship.Direction.UP;
            };

            try {
                shipPlacementManager.put(coordinates.x(), coordinates.y(), size, dir).forEach(updater);
                shipPlacementView.setErrorMessage("");
                return null;
            } catch (MisplacedShipException e) {
                shipPlacementView.setErrorMessage("Unable to put the ship at given position");
                return null;
            } catch (UnavailableSizeException e) {
                shipPlacementView.setErrorMessage("You don't have a ship of such size");
                return null;
            }
        } else if (command.trim().equals("undo")) {  // Команда undo
            try {
                shipPlacementManager.undo().forEach(updater);
                shipPlacementView.setErrorMessage("");
                return null;
            } catch (NoShipsException e) {
                shipPlacementView.setErrorMessage("You don't have any ships to remove");
                return null;
            }
        } else if (command.trim().equals("start")) {  // Команда start
            try {
                return shipPlacementManager.start();
            } catch (NotEnoughShipsPlacedException e) {
                shipPlacementView.setErrorMessage("You haven't put all the ships yet");
                return null;
            }
        } else if (command.trim().equals("exit")) {  // Команда exit
            System.exit(0);
            return null;
        } else {
            shipPlacementView.setErrorMessage("Unable to recognize the command");
            return null;
        }
    }

    private Stream<BattleView.ShipTile> getTiles(ShipPlacement placement) {
        Stream.Builder<BattleView.ShipTile> builder = Stream.builder();

        placement.getShips().forEach(
                ship -> ship.getTiles().forEach(
                        tile -> builder.add(
                                new BattleView.ShipTile(tile.x(), tile.y(), ship.isVertical())
                        )
                )
        );

        return builder.build();
    }

    @Override
    public ShipPlacement start() {
        shipPlacementView.draw();

        ShipPlacement res = null;
        while (res == null) {
            res = parseCommand(shipPlacementView.getCommand());
        }
        battleView.draw();
        battleView.drawAlliedBattlefield(getTiles(res));
        return res;
    }
}
