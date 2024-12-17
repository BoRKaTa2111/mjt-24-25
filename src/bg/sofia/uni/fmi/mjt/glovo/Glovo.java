package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;

import static bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod.CHEAPEST;
import static bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod.FASTEST;

public class Glovo implements GlovoApi {

    private char[][] mapLayout;
    private ControlCenter control;

    public Glovo(char[][] mapLayout) {
        this.control = new ControlCenter(mapLayout);
        this.mapLayout = mapLayout;
    }

    @Override
    public Delivery getCheapestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException {
        DeliveryInfo delInf = control.findOptimalDeliveryGuy(restaurant.getLocation(),
                client.getLocation(), -1, -1, CHEAPEST);
        validate(delInf);
        return new Delivery(client.getLocation(), restaurant.getLocation(), delInf.getDeliveryGuyLocation(),
                foodItem, delInf.getPrice(), delInf.getEstimatedTime());
    }

    @Override
    public Delivery getFastestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException {
        DeliveryInfo delInf = control.findOptimalDeliveryGuy(restaurant.getLocation(),
                client.getLocation(), -1, -1, FASTEST);
        validate(delInf);
        return new Delivery(client.getLocation(), restaurant.getLocation(), delInf.getDeliveryGuyLocation(),
                foodItem, delInf.getPrice(), delInf.getEstimatedTime());
    }

    @Override
    public Delivery getFastestDeliveryUnderPrice(MapEntity client, MapEntity restaurant,
                                                 String foodItem, double maxPrice)
            throws NoAvailableDeliveryGuyException {
        DeliveryInfo delInf = control.findOptimalDeliveryGuy(restaurant.getLocation(),
                client.getLocation(), maxPrice, -1, FASTEST);
        validate(delInf);
        return new Delivery(client.getLocation(), restaurant.getLocation(), delInf.getDeliveryGuyLocation(),
                foodItem, delInf.getPrice(), delInf.getEstimatedTime());
    }

    @Override
    public Delivery getCheapestDeliveryWithinTimeLimit(MapEntity client, MapEntity restaurant,
                                                       String foodItem, int maxTime)
            throws NoAvailableDeliveryGuyException {
        DeliveryInfo delInf = control.findOptimalDeliveryGuy(restaurant.getLocation(),
                client.getLocation(), -1, maxTime, CHEAPEST);
        validate(delInf);
        return new Delivery(client.getLocation(), restaurant.getLocation(), delInf.getDeliveryGuyLocation(),
                foodItem, delInf.getPrice(), delInf.getEstimatedTime());
    }

    private void validate(DeliveryInfo delInf) throws NoAvailableDeliveryGuyException {
        if (delInf == null) {
            throw new NoAvailableDeliveryGuyException("No delivery guy available");
        }
    }
}
