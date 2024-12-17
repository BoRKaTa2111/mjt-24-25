package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapSymbolException;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoValidLocationException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType.BIKE;
import static bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType.CAR;
import static bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod.FASTEST;

public class ControlCenter implements ControlCenterApi {

    private static final Set<Character> VALID_SYMBOLS = new HashSet<>();
    static {
        VALID_SYMBOLS.add('A');
        VALID_SYMBOLS.add('B');
        VALID_SYMBOLS.add('C');
        VALID_SYMBOLS.add('R');
        VALID_SYMBOLS.add('#');
        VALID_SYMBOLS.add('.');
    }

    private char[][] mapLayout;
    private Set<Location> restaurantLocations = new HashSet<>();
    private Set<Location> clientLocations = new HashSet<>();
    private Set<Location> deliveryGuyLocations = new HashSet<>();

    public ControlCenter(char[][] mapLayout) {
        this.mapLayout = mapLayout;
        setLocations();
    }

    private void setLocations() {
        for (int i = 0; i < mapLayout.length; i++) {
            for (int j = 0; j < mapLayout[i].length; j++) {
                if (mapLayout[i][j] == 'C') {
                    clientLocations.add(new Location(i, j));
                } else if (mapLayout[i][j] == 'R') {
                    restaurantLocations.add(new Location(i, j));
                } else if (mapLayout[i][j] == 'A' || mapLayout[i][j] == 'B') {
                    deliveryGuyLocations.add(new Location(i, j));
                } else if (!VALID_SYMBOLS.contains(mapLayout[i][j])) {
                    throw new InvalidMapSymbolException("Invalid map symbol: " + mapLayout[i][j]);
                }
            }
        }
        validateClientLocation();
        validateRestaurantLocation();
        validateDeliveryGuyLocation();
    }

    private void validateClientLocation() {
        if (clientLocations.isEmpty()) {
            throw new NoValidLocationException("Client location could not be found");
        }
    }

    private void validateRestaurantLocation() {
        if (restaurantLocations.isEmpty()) {
            throw new NoValidLocationException("Restaurant location could not be found");
        }
    }

    private void validateDeliveryGuyLocation() {
        if (deliveryGuyLocations.isEmpty()) {
            throw new NoAvailableDeliveryGuyException("Delivery guy location could not be found");
        }
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                               double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        validateOptimalDeliveryGuyInput(restaurantLocation, clientLocation, maxPrice, maxTime, shippingMethod);
        if (maxPrice == -1) {
            maxPrice = Double.MAX_VALUE;
        }

        if (maxTime == -1) {
            maxTime = Integer.MAX_VALUE;
        }

        Location optimalDeliveryGuy = null;
        int optimalTotalTime = Integer.MAX_VALUE;
        double optimalTotalPrice = Double.MAX_VALUE;

        for (Location deliveryGuyLocation : deliveryGuyLocations) {
            int kmtorestaurant = getShortestPath(deliveryGuyLocation, restaurantLocation);
            if (kmtorestaurant == -1) {
                continue;
            }
            int kmToClient = getShortestPath(restaurantLocation, clientLocation);
            if (kmToClient == -1) {
                continue;
            }
            int totalKM = kmtorestaurant + kmToClient;
            DeliveryType driverType = findDeliveryType(deliveryGuyLocation);
            double totalPrice = calculateCost(totalKM, driverType);
            int totalTime = calculateTime(totalKM, driverType);

            if (shippingMethod == FASTEST) {
                if (totalTime <= maxTime && totalPrice <= maxPrice) {
                    if (totalTime < optimalTotalTime ||
                            (totalTime == optimalTotalTime && totalPrice < optimalTotalPrice)) {
                        optimalDeliveryGuy = deliveryGuyLocation;
                        optimalTotalTime = totalTime;
                        optimalTotalPrice = totalPrice;
                    }
                }
            } else {
                if (totalTime <= maxTime && totalPrice <= maxPrice) {

                    if (totalPrice < optimalTotalPrice ||
                            (totalPrice == optimalTotalPrice && totalTime < optimalTotalTime)) {
                        optimalDeliveryGuy = deliveryGuyLocation;
                        optimalTotalTime = totalTime;
                        optimalTotalPrice = totalPrice;
                    }
                }
            }
        }

        return (optimalDeliveryGuy == null) ? null : new DeliveryInfo(optimalDeliveryGuy,
                optimalTotalPrice, optimalTotalTime, findDeliveryType(optimalDeliveryGuy));

    }

    private DeliveryType findDeliveryType(Location deliveryGuy) {
        return (mapLayout[deliveryGuy.getX()]
                [deliveryGuy.getY()] == 'A' ) ? CAR : BIKE;
    }

    private double calculateCost(int path, DeliveryType deliveryType) {
        return path * deliveryType.getSpeed();
    }

    private int calculateTime(int path, DeliveryType deliveryType) {
        return path * deliveryType.geTime();
    }

    private int getShortestPath(Location deliveryGuy, Location location) {
        Queue<Location> queue = new LinkedList<>();
        queue.add(deliveryGuy);
        Set<Location> visited = new HashSet<>();
        visited.add(deliveryGuy);
        int path = 0;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Location currLocation = queue.poll();

                if (Objects.requireNonNull(currLocation).equals(location)) {
                    return path;
                }
                Location[] directions = {new Location(currLocation.getX() - 1, currLocation.getY()),
                                         new Location(currLocation.getX() + 1, currLocation.getY()),
                                         new Location(currLocation.getX(), currLocation.getY() + 1),
                                         new Location(currLocation.getX(), currLocation.getY() - 1)
                    };
                for (Location currDirection : directions) {
                    if (isValid(currDirection, visited)) {
                        queue.add(currDirection);
                        visited.add(currDirection);
                    }
                }
            }
            path++;
        }
        return -1;
    }

    private boolean isValid(Location location, Set<Location> traversed) {
        int x = location.getX();
        int y = location.getY();

        if (x >= 0 && x < mapLayout.length && y >= 0 && y < mapLayout[0].length) {
            char symbol = mapLayout[x][y];
            if (symbol != '#' && !traversed.contains(location)) {
                return true;
            }

        }
        return false;
    }

    private void validateOptimalDeliveryGuyInput(Location restaurantLocation,
                                                 Location clientLocation, double maxPrice,
                                                 int maxTime, ShippingMethod shippingMethod) {
        if (restaurantLocation == null || clientLocation == null) {
            throw new InvalidOrderException("Location could not be found and no order could be made");
        }
        if (!restaurantLocations.contains(restaurantLocation) ||
                !clientLocations.contains(clientLocation)) {
            throw new InvalidOrderException("Location could not be found and no order could be made");
        }
        if ((maxPrice <= 0 && maxPrice != -1) || (maxTime <= 0 && maxTime != -1)) {
            throw new InvalidOrderException("Not a viable order");
        }
        if (shippingMethod == null) {
            throw new InvalidOrderException("You must choose a shipping method");
        }
    }

    @Override
    public MapEntity[][] getLayout() {

        MapEntity[][] layout = new MapEntity[mapLayout.length][mapLayout[0].length];

        for (int i = 0; i < mapLayout.length; i++) {
            for (int j = 0; j < mapLayout[i].length; j++) {
                Location currLoc = new Location(i, j);
                char currSymb = mapLayout[i][j];
                MapEntityType currType = getTypeBySymbol(currSymb);
                layout[i][j] = new MapEntity(currLoc, currType);
            }
        }
        return layout;
    }

    private MapEntityType getTypeBySymbol(char symbol) {
        for (MapEntityType type : MapEntityType.values()) {
            if (type.getSymbol() == symbol) {
                return type;
            }
        }
        throw new InvalidMapSymbolException("Symbol is invalid");
    }
}
