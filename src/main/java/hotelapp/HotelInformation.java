package hotelapp;

import com.google.gson.annotations.SerializedName;

/** Class to store information about HOTEL about reading from json files**/
public class HotelInformation {
    private String id;
    @SerializedName(value = "f")
    private String name;
    @SerializedName(value = "ad")
    private String address;
    @SerializedName(value = "ci")
    private String city;
    @SerializedName(value = "pr")
    private String state;

    @SerializedName(value = "i")
    private String hotelNumber ;

    @SerializedName(value = "ll")
    private Location longLat;

    class Location {
        @SerializedName(value = "lat")
        private String latitude;
        @SerializedName(value ="lng" )
        private  String longitude;
    }
    public String getHotelId() {return id;}

    public String getHotelName() {return name;}

    public String getAddress() {return address + System.lineSeparator() + ", " + city + ", " + state;}

    public String getLatitude() {
        return longLat.latitude;
    }

    public String getLongitude() {
        return longLat.longitude;
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

    /**
     * Print Hotel Details for the find application in CLI mode
     * @return
     */
    public String printHotelDetails() {
        String details =
                this.name +  ": " + this.id + System.lineSeparator()+
                        this.address +  System.lineSeparator() +
                        this.city + ", " + this.state;
        return details;
    }
}
