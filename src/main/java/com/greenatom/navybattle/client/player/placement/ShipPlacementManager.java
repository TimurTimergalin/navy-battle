package com.greenatom.navybattle.client.player.placement;

import com.greenatom.navybattle.ships.MisplacedShipException;
import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;
import com.greenatom.navybattle.view.components.field.TileStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

public class ShipPlacementManager {
    private final ShipPlacement shipPlacement;
    private final Map<Integer, Integer> availableShipSizes;
    private final Stack<Ship> addedShips = new Stack<>();

    public ShipPlacementManager(int size, Map<Integer, Integer> availableShipSizes) {
        this.shipPlacement = new ShipPlacement(size);
        this.availableShipSizes = new HashMap<>(availableShipSizes);  // Чтобы не допустить изменений извне
    }

    public record CoordinateUpdate(int x, int y, TileStatus status) {
    }

    public Stream<CoordinateUpdate> put(int originX, int originY, int size, Ship.Direction direction)
            throws UnavailableSizeException, MisplacedShipException {
        if (!availableShipSizes.containsKey(size)) {
            throw new UnavailableSizeException();
        }

        var ship = new Ship(originX, originY, size, direction);
        shipPlacement.placeShip(ship);

        availableShipSizes.put(size, availableShipSizes.get(size) - 1);
        if (availableShipSizes.get(size) == 0) {
            availableShipSizes.remove(size);
        }

        TileStatus status = switch (direction) {
            case UP, DOWN -> TileStatus.VERTICAL;
            case LEFT, RIGHT -> TileStatus.HORIZONTAL;
        };

        addedShips.push(ship);

        return ship.getTiles().map(c -> new CoordinateUpdate(c.x(), c.y(), status));
    }

    public Stream<CoordinateUpdate> undo() throws NoShipsException {
        if (addedShips.isEmpty()) {
            throw new NoShipsException();
        }

        Ship ship = addedShips.pop();
        shipPlacement.removeShip(ship);
        availableShipSizes.put(ship.getSize(), availableShipSizes.getOrDefault(ship.getSize(), 0) + 1);

        return ship.getTiles().map(c -> new CoordinateUpdate(c.x(), c.y(), TileStatus.FOG));
    }

    public ShipPlacement start() throws NotEnoughShipsPlacedException {
        if (!availableShipSizes.isEmpty()) {
            throw new NotEnoughShipsPlacedException();
        }

        return shipPlacement;
    }
}
