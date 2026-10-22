package dev.privatebed;

/** Plain data shared between the Minecraft adapters and durable logout storage. */
public record Location(String dimension, int x, int y, int z) {
    public Location {
        if (dimension == null || !dimension.matches("[a-z0-9_.-]+:[a-z0-9/._-]+")) {
            throw new IllegalArgumentException("Invalid dimension identifier");
        }
    }

    public String coordinates() {
        return "X: " + x + "  Y: " + y + "  Z: " + z + "  (" + dimension + ")";
    }
}
