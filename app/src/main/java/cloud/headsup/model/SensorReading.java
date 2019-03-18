package cloud.headsup.model;

/**
 * Created by Federica on 01/03/2019.
 */

public class SensorReading {

    private float distance;
    private String phoneModel;
    private String phoneManufacturer;
    private String userID;

    public SensorReading(float distance, String phoneModel, String phoneManufacturer, String userID) {
        this.distance = distance;
        this.phoneModel = phoneModel;
        this.phoneManufacturer = phoneManufacturer;
        this.userID = userID;
    }

    public float getDistance() {
        return distance;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public String getUserID() {
        return userID;
    }

    public String getPhoneManufacturer() {
        return phoneManufacturer;
    }
}
