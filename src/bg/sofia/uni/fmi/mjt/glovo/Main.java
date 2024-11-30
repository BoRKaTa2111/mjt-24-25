package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;

import static bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod.CHEAPEST;
import static bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod.FASTEST;

public class Main {
    public static void main(String[] args) {
        char[][] mapLayout = {
                {'#', '#', '#', '.', '#'},
                {'#', '.', 'B', 'R', 'A'},
                {'.', '.', '#', '.', '#'},
                {'#', 'C', '.', 'A', '.'},
                {'#', '.', '#', '#', '#'}
        };
        ControlCenter control = new ControlCenter(mapLayout);
        DeliveryInfo delInf = control.findOptimalDeliveryGuy(new Location(1,3),
                new Location(3,1), 1000,1000,FASTEST);
        System.out.println(delInf.getDeliveryGuyLocation().getX() + " " + delInf.getDeliveryGuyLocation().getY());
        System.out.println(delInf.getPrice());
        System.out.println(delInf.getEstimatedTime());
        Glovo glovo = new Glovo(mapLayout);
//        glovo.getCheapestDelivery()
    }
}
