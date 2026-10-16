package com.miophas.corruption.core.demon;

/**
 * One of the hitboxes a demon's body is made of, where it stands as the demon lies at rest: its middle {@code forward}
 * of the demon's feet along the way it faces and {@code left} to its left, its foot {@code up} over them, {@code width}
 * across and {@code height} tall, in blocks. A blow on it is a blow on the demon.
 */
public record BodyPart(String name, double forward, double left, double up, double width, double height) {
    public BodyPart {
        width = Math.max(.1, width);
        height = Math.max(.1, height);
    }
}
