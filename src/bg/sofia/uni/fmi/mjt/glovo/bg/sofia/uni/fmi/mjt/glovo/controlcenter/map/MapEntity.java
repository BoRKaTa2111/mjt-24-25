package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

public class MapEntity {
    private Location location;
    private MapEntityType type;
    public MapEntity(Location location, MapEntityType type) {
        this.location = location;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public MapEntityType getType() {
        return type;
    }
}
