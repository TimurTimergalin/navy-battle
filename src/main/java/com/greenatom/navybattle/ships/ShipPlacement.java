package com.greenatom.navybattle.ships;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ShipPlacement {
    private final Ship[][] tiles;
    private final List<Ship> ships = new ArrayList<>();

    private Ship getTile(Coordinates c) {
        return tiles[c.y() - 1][c.x() - 1];
    }

    private void setTile(Coordinates c, Ship tile) {
        tiles[c.y() - 1][c.x() - 1] = tile;
    }

    public ShipPlacement(int size) {
        tiles = new Ship[size][size];
        for (int x = 0; x < size; x++) {
            Arrays.fill(tiles[x], null);
        }
    }

    private boolean coordinatesInBounds(Coordinates c) {
        int size = tiles.length;
        return c.x() >= 1 && c.x() <= size && c.y() >= 1 && c.y() <= size;
    }

    private boolean tileAvailable(Coordinates c) {
        return coordinatesInBounds(c) &&
                getTile(c) == null &&
                c.getNeighbors()
                        .filter(this::coordinatesInBounds)
                        .allMatch(cc -> getTile(cc) == null);
    }

    public void placeShip(Ship ship) throws MisplacedShipException {
        if (!ship.getTiles().allMatch(this::tileAvailable)) {
            throw new MisplacedShipException();
        }

        ship.getTiles().forEach(c -> setTile(c, ship));
        ships.add(ship);
    }

    public void removeShip(Ship ship) {
        ship.getTiles().forEach(c -> setTile(c, null));
        ships.remove(ship);
    }

    public Ship getShipAt(Coordinates c) {
        return getTile(c);
    }

    public int getShipCount() {
        return ships.size();
    }

    public Stream<Ship> getShips() {
        return ships.stream();
    }
}
