package de.carloschmitt.morec.model;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;
import com.meicke.threeSpaceSensorAndroidAPI.TssMiniBluetooth;

public class tssminiWrapper extends TssMiniBluetooth {
    /**
     * Class constructor. This is a singleton class. Use getInstance method instead!
     *
     * @param address
     * @param autoConnect
     * @throws TssConnectionException Error based on the bluetooth connection with the sensor.
     */
    public tssminiWrapper(String address, boolean autoConnect) throws TssConnectionException {
        super(address, autoConnect);
    }
}
