package com.greenatom.navybattle.ships;

import java.util.Arrays;

public class ShipPlacement {
    private final Ship[][] tiles;

    public ShipPlacement(int size) {
        tiles = new Ship[size][size];
        for (int x = 0; x < size; x++) {
            Arrays.fill(tiles[x], null);
        }
    }

    private boolean coordinatesInBounds(Ship.Coordinates c) {
        int size = tiles.length;
        return c.x() >= 0 && c.x() < size && c.y() >= 0 && c.y() < size;
    }

    private boolean tileAvailable(Ship.Coordinates c) {
        return coordinatesInBounds(c) &&
                tiles[c.y()][c.x()] == null &&
                c.getNeighbors()
                        .filter(this::coordinatesInBounds)
                        .allMatch(cc -> tiles[cc.y()][cc.x()] == null);
    }

    public void placeShip(Ship ship) throws MisplacedShipException {
        if (!ship.getTiles().allMatch(this::tileAvailable)) {
            throw new MisplacedShipException("Invalid placement for a ship");
        }

        ship.getTiles().forEach(c -> tiles[c.y()][c.x()] = ship);
    }

    public void removeShip(Ship ship) {
        ship.getTiles().forEach(c -> tiles[c.y()][c.x()] = null);
    }

    public Ship getShipAt(Ship.Coordinates c) {
        return tiles[c.y()][c.x()];
    }
}
