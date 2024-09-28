package com.greenatom.navybattle.ships;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Ship {

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
            case UP -> (i) -> new Coordinates(originX, originY - i);
            case DOWN -> (i) -> new Coordinates(originX, originY + i);
        };
    }

    public Ship(int originX, int originY, int size, Direction direction) {
        this(
                IntStream.range(0, size)
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

    public boolean isVertical() {
        return getSize() == 1 || tiles.get(0).x() == tiles.get(1).x();
    }
}
