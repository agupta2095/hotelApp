package hotelapp;

import com.google.gson.annotations.SerializedName;

/** Class to store information about HOTEL **/
public class HotelInformation {
    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String latitude;
    private  String longitude;

    /**
     * Constructor
     * @param id : HotelID
     * @param name : Name of Hotel
     * @param address:  Street Address
     * @param city : City
     * @param state : State
     * @param latitude : Geographical latitude
     * @param longitude : Geographical longitude
     */
    public HotelInformation(String id, String name, String address, String city, String state, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getHotelId() {return id;}

    public String getHotelName() {return name;}

    public String getAddress() {return address + System.lineSeparator() + ", " + city + ", " + state;}

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    /**
     * Get Expedia Link for a given Hotel Id
     * @param host : domain name of Expedia
     * @return: Expedia Hotel URL
     */
    public  String getExpediaLink(String host) {
        String nameStr = name.replaceAll("\\p{Punct}|\\s", "-");
        String area = city.replaceAll("\\p{Punct}|\\s", "-");
        area += "-Hotels-";
        String expediaLink = host + area + nameStr + ".h" + id + ".Hotel-Information";
        return expediaLink;
    }

}
