package com.greenatom.navybattle.client.bot;

import com.greenatom.navybattle.client.ShotStatus;
import com.greenatom.navybattle.ships.Coordinates;

import java.util.*;
import java.util.stream.Stream;

public class BotIntellect {
    private interface Strategy {
        Coordinates makeMove();
        void react(ShotStatus status);
    }

    private class DefaultStrategy implements Strategy {
        @Override
        public Coordinates makeMove() {
            List<Coordinates> allowed = new ArrayList<>();

            for (int i = 1; i <= size; ++i) {
                for (int j = 1; j <= size; ++j) {
                    Coordinates c = new Coordinates(i, j);
                    if (!getTile(c)) {
                        allowed.add(c);
                    }
                }
            }

            int choice = rand.nextInt(allowed.size());
            return allowed.get(choice);
        }

        @Override
        public void react(ShotStatus status) {
            strategy = switch (status) {
                case MISS, KILL -> this;
                case HIT -> new FinishOffStrategy(lastShot);
            };
        }
    }

    private class FinishOffStrategy implements Strategy {
        private final List<Coordinates> shipCoordinates = new ArrayList<>();

        public FinishOffStrategy(Coordinates initialCoordinates) {
            shipCoordinates.add(initialCoordinates);
        }

        private boolean canShoot(Coordinates c) {
            return !getTile(c) &&
                    1 <= c.x() && c.x() <= size && 1 <= c.y() && c.y() <= size;
        }

        private Coordinates singleTileCase() {
            Coordinates singleTile = shipCoordinates.getFirst();

            List<Coordinates> options = singleTile
                    .getEdgeNeighbours()
                    .filter(this::canShoot)
                    .toList();

            int choice = rand.nextInt(options.size());
            return options.get(choice);
        }

        private Coordinates multipleTilesCase() {
            List<Coordinates> options;
            if (shipCoordinates.getFirst().x() == shipCoordinates.getLast().x()) {  // Вертикальный корабль
                int x = shipCoordinates.getFirst().x();
                shipCoordinates.sort(Comparator.comparingInt(Coordinates::y));

                Stream.Builder<Coordinates> builder = Stream.builder();
                options = builder
                        .add(new Coordinates(x, shipCoordinates.getFirst().y() -1))
                        .add(new Coordinates(x, shipCoordinates.getLast().y() + 1))
                        .build()
                        .filter(this::canShoot)
                        .toList();
            } else {  // Горизонтальный корабль
                int y = shipCoordinates.getFirst().y();
                shipCoordinates.sort(Comparator.comparingInt(Coordinates::x));

                Stream.Builder<Coordinates> builder = Stream.builder();
                options = builder
                        .add(new Coordinates(shipCoordinates.getFirst().x() - 1, y))
                        .add(new Coordinates(shipCoordinates.getLast().x() + 1, y))
                        .build()
                        .filter(this::canShoot)
                        .toList();
            }

            int choice = rand.nextInt(options.size());
            return options.get(choice);
        }

        @Override
        public Coordinates makeMove() {
            if (shipCoordinates.size() == 1) {
                return singleTileCase();
            } else {
                return multipleTilesCase();
            }
        }

        @Override
        public void react(ShotStatus status) {
            switch (status) {
                case HIT -> shipCoordinates.add(lastShot);
                case KILL -> strategy = new DefaultStrategy();
            }
        }
    }

    private final boolean[][] hitTiles;
    private final int size;
    private Strategy strategy = new DefaultStrategy();
    public final Random rand = new Random();
    private Coordinates lastShot = null;

    public BotIntellect(int size) {
        hitTiles = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(hitTiles[i], false);
        }
        this.size = size;
    }

    private boolean getTile(Coordinates c) {
        return hitTiles[c.y() - 1][c.x() - 1];
    }

    private void setTile(Coordinates c) {
        hitTiles[c.y() - 1][c.x() - 1] = true;
    }

    public Coordinates makeMove() {
        lastShot = strategy.makeMove();
        return lastShot;
    }

    public void react(ShotStatus status) {
        setTile(lastShot);
        strategy.react(status);
    }
}
