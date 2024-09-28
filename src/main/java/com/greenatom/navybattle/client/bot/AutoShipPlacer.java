package com.greenatom.navybattle.client.bot;

import com.greenatom.navybattle.client.placement.NotEnoughShipsPlacedException;
import com.greenatom.navybattle.client.placement.ShipPlacementManager;
import com.greenatom.navybattle.client.placement.UnavailableSizeException;
import com.greenatom.navybattle.ships.Coordinates;
import com.greenatom.navybattle.ships.MisplacedShipException;
import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoShipPlacer {
    private class RandomGenerator {
        private final Random rand = new Random();

        public Ship.Direction randomDirection() {
            return switch(rand.nextInt(4)) {
                case 0 -> Ship.Direction.UP;
                case 1 -> Ship.Direction.DOWN;
                case 2 -> Ship.Direction.LEFT;
                default -> Ship.Direction.RIGHT;
            };
        }

        public Coordinates randomCoordinates() {
            int size = fieldSize;
            int randomNum = rand.nextInt(size * size);
            return new Coordinates(randomNum / size + 1, randomNum % size + 1);
        }
    }

    private final ShipPlacementManager shipPlacementManager;
    private final List<Integer> availableSizes;
    private final int fieldSize;
    private final RandomGenerator rand = new RandomGenerator();

    public AutoShipPlacer(ShipPlacementManager shipPlacementManager, List<Integer> availableSizes, int fieldSize) {
        this.shipPlacementManager = shipPlacementManager;

        this.availableSizes = new ArrayList<>(availableSizes);
        this.availableSizes.sort((i, j) -> j - i);

        this.fieldSize = fieldSize;
    }

    public ShipPlacement generatePlacement() {
        int sizeIndex = 0;

        while (sizeIndex < availableSizes.size()) {
            while (true) {
                try {
                    Coordinates c = rand.randomCoordinates();
                    shipPlacementManager.put(c.x(), c.y(), availableSizes.get(sizeIndex), rand.randomDirection());
                } catch (MisplacedShipException ignored) {
                } catch (UnavailableSizeException e) {
                    ++sizeIndex;
                    break;
                }
            }
        }

        try {
            return shipPlacementManager.start();
        } catch (NotEnoughShipsPlacedException e) {
            throw new RuntimeException("AutoShipPlacement have not placed all the ships");
        }
    }
}
