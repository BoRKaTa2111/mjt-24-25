package bg.sofia.uni.fmi.mjt.glovo.delivery;

public enum DeliveryType {
    CAR(5, 3),
    BIKE(3, 5);

    private final int costPerKM;
    private final int timePerKM;

    DeliveryType(final int costPerKM, final int timePerKM) {
        this.costPerKM = costPerKM;
        this.timePerKM = timePerKM;
    }

    public int getSpeed() {
        return costPerKM;
    }

    public int geTime() {
        return timePerKM;
    }
}
