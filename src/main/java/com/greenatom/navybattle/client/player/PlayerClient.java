package com.greenatom.navybattle.client.player;

import com.greenatom.navybattle.client.Client;
import com.greenatom.navybattle.client.ShotStatus;
import com.greenatom.navybattle.client.placement.NoShipsException;
import com.greenatom.navybattle.client.placement.NotEnoughShipsPlacedException;
import com.greenatom.navybattle.client.placement.ShipPlacementManager;
import com.greenatom.navybattle.client.placement.UnavailableSizeException;
import com.greenatom.navybattle.ships.Coordinates;
import com.greenatom.navybattle.ships.MisplacedShipException;
import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;
import com.greenatom.navybattle.view.BattleView;
import com.greenatom.navybattle.view.ShipPlacementView;
import com.greenatom.navybattle.view.components.field.TileStatus;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PlayerClient implements Client {
    public class Parser implements PlayerCommandParser {
        private static final Pattern putPattern = Pattern.compile("\\s*put\\s+(?<size>[1-9][0-9]*)\\s+(?<origin>[A-Za-z][1-9][0-9]*)(?:\\s+(?<direction>[UuDdLlRr]))?\\s*");

        // Возвращает null, если парсинг не удался
        public Coordinates parseCoordinates(String s) {
            s = s.trim();
            if (s.length() < 2) {
                return null;
            }

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
                return new Coordinates(x, y);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        private void updateBattlefield(ShipPlacementManager.CoordinateUpdate update) {
            shipPlacementView.updateBattlefield(update.x(), update.y(), update.status());
        }

        private void put(Matcher matcher) {
            int size = Integer.parseInt(matcher.group("size"));
            Coordinates coordinates = parseCoordinates(matcher.group("origin"));

            if (coordinates == null) {
                shipPlacementView.setErrorMessage("Invalid coordinates format");
                return;
            }

            String direction = matcher.group("direction");
            if (size != 1 && direction == null) {
                shipPlacementView.setErrorMessage("You must specify a direction of the ship (U|D|L|R)");
                return;
            }

            Ship.Direction dir = switch (direction) {
                case "D", "d" -> Ship.Direction.DOWN;
                case "L", "l" -> Ship.Direction.LEFT;
                case "R", "r" -> Ship.Direction.RIGHT;
                case null, default -> Ship.Direction.UP;
            };

            try {
                shipPlacementManager
                        .put(coordinates.x(), coordinates.y(), size, dir)
                        .forEach(this::updateBattlefield);
                shipPlacementView.setErrorMessage("");
            } catch (MisplacedShipException e) {
                shipPlacementView.setErrorMessage("Unable to put the ship at given position");
            } catch (UnavailableSizeException e) {
                shipPlacementView.setErrorMessage("You don't have a ship of such size");
            }
        }

        private void undo() {
            try {
                shipPlacementManager.undo().forEach(this::updateBattlefield);
                shipPlacementView.setErrorMessage("");
            } catch (NoShipsException e) {
                shipPlacementView.setErrorMessage("You don't have any ships to remove");
            }
        }

        private ShipPlacement start() {
            try {
                return shipPlacementManager.start();
            } catch (NotEnoughShipsPlacedException e) {
                shipPlacementView.setErrorMessage("You haven't put all the ships yet");
                return null;
            }
        }

        private void exit() {
            shipPlacementView.setErrorMessage("");
            System.exit(0);
        }

        // Возвращает true, если надо начинать игру
        public ShipPlacement parseCommand(String command) {
            var matcher = putPattern.matcher(command);

            if (matcher.matches()) {  // Команда put
                put(matcher);
                return null;
            } else if (command.trim().equals("undo")) {  // Команда undo
                undo();
                return null;
            } else if (command.trim().equals("start")) {  // Команда start
                return start();
            } else if (command.trim().equals("exit")) {  // Команда exit
                exit();
                return null;
            } else {
                shipPlacementView.setErrorMessage("Unable to recognize the command");
                return null;
            }
        }
    }

    private final ShipPlacementView shipPlacementView;
    private final BattleView battleView;
    private final ShipPlacementManager shipPlacementManager;
    private final PlayerCommandParser parser;

    public PlayerClient(
            ShipPlacementView shipPlacementView,
            BattleView battleView,
            ShipPlacementManager shipPlacementManager,
            Function<PlayerClient, PlayerCommandParser> parserFactory) {
        this.shipPlacementView = shipPlacementView;
        this.battleView = battleView;
        this.shipPlacementManager = shipPlacementManager;
        this.parser = parserFactory.apply(this);
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
            res = parser.parseCommand(shipPlacementView.getCommand());
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
    public Coordinates requestShot() {
        Coordinates res = parser.parseCoordinates(battleView.getCoordinates());
        while (res == null) {
            battleView.setErrorMessage("Invalid coordinates format");
            res = parser.parseCoordinates(battleView.getCoordinates());
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
