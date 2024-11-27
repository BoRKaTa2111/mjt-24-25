package bg.sofia.uni.fmi.mjt.glovo.exception;

import java.io.NotActiveException;

public class NoAvailableDeliveryGuyException extends RuntimeException {
    NoAvailableDeliveryGuyException(String s) {
        super(s);
    }
}
