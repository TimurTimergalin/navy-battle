package com.greenatom.navybattle.client;

import com.greenatom.navybattle.client.placement.NoShipsException;
import com.greenatom.navybattle.client.placement.NotEnoughShipsPlacedException;
import com.greenatom.navybattle.client.placement.ShipPlacementManager;
import com.greenatom.navybattle.client.placement.UnavailableSizeException;
import com.greenatom.navybattle.ships.MisplacedShipException;
import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;
import com.greenatom.navybattle.view.BattleView;
import com.greenatom.navybattle.view.ShipPlacementView;
import com.greenatom.navybattle.view.components.field.TileStatus;

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

    private static final Pattern putMatch = Pattern.compile("\\s*put\\s+(?<size>[1-9][0-9]*)\\s+(?<origin>[A-Za-z][1-9][0-9]*)(?:\\s+(?<direction>[UuDdLlRr]))?\\s*");

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
                shipPlacementView.updateBattlefield(update.x(), update.y(), update.status());

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
                case "D", "d" -> Ship.Direction.DOWN;
                case "L", "l" -> Ship.Direction.LEFT;
                case "R", "r" -> Ship.Direction.RIGHT;
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

    private String formatCoordinates(int x, int y) {
        return String.valueOf((char) ('A' + x - 1)) + y;
    }

    private String formatShotStatus(ShotStatus status) {
        return switch (status) {
            case MISS -> "miss";
            case HIT -> "hit";
            case KILL -> "kill";
        };
    }

    private void announceTurn(String side, int x, int y, ShotStatus shotStatus) {
        String s = side + ": " + formatCoordinates(x, y) + " - " + formatShotStatus(shotStatus) + "!";
        battleView.logMessage(s);
    }

    @Override
    public void announceAlliedTurn(int x, int y, ShotStatus shotStatus) {
        announceTurn("You", x, y, shotStatus);
        battleView.setErrorMessage("");
    }

    @Override
    public void announceEnemyTurn(int x, int y, ShotStatus shotStatus) {
        announceTurn("Enemy", x, y, shotStatus);
    }

    private TileStatus shotToTileStatus(ShotStatus shotStatus) {
        return switch (shotStatus) {
            case MISS -> TileStatus.MISS;
            case KILL, HIT -> TileStatus.HIT;
        };
    }

    @Override
    public void registerAlliedShot(int x, int y, ShotStatus shotStatus) {
       battleView.updateEnemyBattlefield(x, y, shotToTileStatus(shotStatus));
    }

    @Override
    public void registerEnemyShot(int x, int y, ShotStatus shotStatus) {
        battleView.updateAlliedBattlefield(x, y, shotToTileStatus(shotStatus));
    }

    @Override
    public void declareVictory() {
        battleView.logMessage("You defeated your enemy - congratulations!");
    }

    @Override
    public void declareDefeat() {
        battleView.logMessage("You have been defeated!");
    }

    @Override
    public Ship.Coordinates requestShot() {
        Ship.Coordinates res = parseCoordinates(battleView.getCoordinates());
        while (res == null) {
            battleView.setErrorMessage("Invalid coordinates format");
            res = parseCoordinates(battleView.getCoordinates());
        }
        battleView.setErrorMessage("");
        return res;
    }

    @Override
    public void reportUserError(UserError error) {
        String message = switch (error) {
            case COORDINATES_OUT_OF_BOUNDS -> "Coordinates out of bounds";
            case REPEATED_MOVE -> "This tile has already been revealed";
        };

        battleView.setErrorMessage(message);
    }
}
