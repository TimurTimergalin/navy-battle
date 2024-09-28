package com.greenatom.navybattle.controller;

import com.greenatom.navybattle.client.Client;
import com.greenatom.navybattle.client.ShotStatus;
import com.greenatom.navybattle.ships.Coordinates;
import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;

import java.util.Arrays;

public class ShotTracker {
    private final ShipPlacement shipPlacement;
    private int shipsKilled = 0;
    private final boolean[][] checked;
    private final int size;

    public ShotTracker(ShipPlacement shipPlacement, int size) {
        this.shipPlacement = shipPlacement;
        this.size = size;

        checked = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(checked[i], false);
        }
    }

    private boolean coordinatesInBounds(Coordinates c) {
        return 1 <= c.x() && c.x() <= size && 1 <= c.y() && c.y() <= size;
    }

    private boolean getTile(Coordinates c) {
        return checked[c.y() - 1][c.x() - 1];
    }

    private void checkTile(Coordinates c) {
        checked[c.y() - 1][c.x() - 1] = true;
    }

    public Client.UserError checkShot(Coordinates c) {
        if (!coordinatesInBounds(c)) {
            return Client.UserError.COORDINATES_OUT_OF_BOUNDS;
        }

        if (getTile(c)) {
            return Client.UserError.REPEATED_MOVE;
        }

        return null;
    }

    public ShotStatus makeShot(Coordinates c) {
        checkTile(c);

        if (shipPlacement.getShipAt(c) == null) {
            return ShotStatus.MISS;
        } else if (shipPlacement
                        .getShipAt(c)
                        .getTiles()
                        .allMatch(this::getTile)) {
            ++shipsKilled;
            return ShotStatus.KILL;
        } else {
            return ShotStatus.HIT;
        }
    }

    public boolean isDead() {
        return shipsKilled == shipPlacement.getShipCount();
    }

    public Ship getShipAt(Coordinates c) {
        return shipPlacement.getShipAt(c);
    }
}
