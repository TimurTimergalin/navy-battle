package com.greenatom.navybattle.ships;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Ship {
    public record Coordinates(int x, int y) {
        public Stream<Coordinates> getNeighbors() {
            Stream.Builder<Coordinates> builder = Stream.builder();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        builder.add(new Coordinates(x + i, y + j));
                    }
                }
            }

            return builder.build();
        }
    }

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private final List<Coordinates> tiles;

    private Ship(List<Coordinates> tiles) {
        this.tiles = tiles;
    }

    private static IntFunction<Coordinates> getCoordinateGenerator(Direction direction, int originX, int originY) {
        return switch (direction) {
            case LEFT -> (i) -> new Coordinates(originX - i, originY);
            case RIGHT -> (i) -> new Coordinates(originX + i, originY);
            case UP -> (i) -> new Coordinates(originX, originY + i);
            case DOWN -> (i) -> new Coordinates(originX, originY - i);
        };
    }

    public Ship(int originX, int originY, int size, Direction direction) {
        this(
                IntStream.rangeClosed(1, size)
                        .mapToObj(
                                getCoordinateGenerator(direction, originX, originY)
                        )
                        .toList()
        );
    }

    public Stream<Coordinates> getTiles() {
        return tiles.stream();
    }

    public int getSize() {
        return tiles.size();
    }
}
