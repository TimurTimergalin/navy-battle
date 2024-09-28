package com.greenatom.navybattle.ships;

import java.util.stream.Stream;

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

    public Stream<Coordinates> getEdgeNeighbours() {
        Stream.Builder<Coordinates> builder = Stream.builder();
        return builder
                .add(new Coordinates(x() - 1, y()))
                .add(new Coordinates(x() + 1, y()))
                .add(new Coordinates(x(), y() - 1))
                .add(new Coordinates(x(), y() + 1))
                .build();
    }
}
