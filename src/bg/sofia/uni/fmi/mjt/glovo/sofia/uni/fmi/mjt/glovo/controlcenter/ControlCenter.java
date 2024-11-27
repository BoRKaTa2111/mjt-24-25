package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapSymbolException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoValidLocationException;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControlCenter implements ControlCenterApi{
    char[][] mapLayout;
    List<Location> restaurantLocations = new ArrayList<>();
    List<Location> clientLocations = new ArrayList<>();
    double maxPrice;
    int maxTime;
    ShippingMethod shippingMethod;


    public ControlCenter(char[][] mapLayout){
        this.mapLayout = mapLayout;
    }

    private void setRestaurantLocations(){
        for(int i = 0;i < mapLayout.length;i++){
            for(int j = 0;j < mapLayout[i].length;j++){
                if(mapLayout[i][j]=='R'){
                    restaurantLocations.add(new Location(i,j));
                }
            }
        }
        if(restaurantLocations.isEmpty()) {
            throw new NoValidLocationException("Restaurant location could not be found");
        }
    }

    private void setClientLocations(){
        for(int i = 0;i < mapLayout.length;i++){
            for(int j = 0;j < mapLayout[i].length;j++){
                if(mapLayout[i][j]=='C'){
                    clientLocations.add(new Location(i,j));
                }
            }
        }
        if(clientLocations.isEmpty()) {
            throw new NoValidLocationException("Client location could not be found");
        }
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                               double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        return null;
    }

    @Override
    public MapEntity[][] getLayout() {

        MapEntity[][] layout = new MapEntity[mapLayout.length][mapLayout[0].length];

        for (int i = 0; i < mapLayout.length; i++) {
            for (int j = 0; j < mapLayout[i].length; j++) {
                Location currLoc = new Location(i, j);
                char currSymb = mapLayout[i][j];
                MapEntityType currType = getTypeBySymbol(currSymb);
                layout[i][j] = new MapEntity(currLoc,currType);
            }
        }
        return layout;
    }


    private MapEntityType getTypeBySymbol(char symbol){
        for(MapEntityType type : MapEntityType.values()){
            if(type.getSymbol()==symbol){
                return type;
            }
        }
        throw new InvalidMapSymbolException("Symbol is invalid");
    }
}
