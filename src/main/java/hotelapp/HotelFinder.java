package hotelapp;

import java.io.PrintWriter;
import java.util.*;

public class HotelFinder {
    /**@hotelFinderMap -> Store Hotel id vs hotel object**/
    final private Map<String, HotelInformation> hotelFinderMap;

    /**Constructor **/
    public HotelFinder() {
        hotelFinderMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    /** Populate the hotelFinderMap**/
    public void putInfoInMap(String hotelId, HotelInformation hotelInfoObj) {
        hotelFinderMap.put(hotelId, hotelInfoObj);
    }

    /**Execute find Query**/
    public void findHotel(String hotelId)  {
        if(hotelFinderMap.containsKey(hotelId)) {
            System.out.println(hotelFinderMap.get(hotelId).printHotelDetails());
            System.out.println("--------------------");
        } else  {
            System.out.println("The application can't find with given Hotel Id: " + hotelId);
        }
    }

    public void printInOutputFile(PrintWriter pr, ReviewsFinder reviewsFinder) {
        for(Map.Entry<String, HotelInformation> keyValPair: hotelFinderMap.entrySet()){
            HotelInformation hotelObj = keyValPair.getValue();
            pr.println("********************");
            pr.println(hotelObj.printHotelDetails());
            reviewsFinder.printInOutputFile(pr, keyValPair.getKey());
        }
    }

    public List<HotelInformation> searchHotels(String keyword) {
        List<HotelInformation> searchedHotels = new ArrayList<>();
        for(HotelInformation hotelObj : hotelFinderMap.values()) {
            String hotelName = hotelObj.getHotelName();
            if(hotelName.contains(keyword) || keyword.isEmpty()) {
                searchedHotels.add(hotelObj);
            }
        }
        return searchedHotels;
    }
    HotelInformation getHotel(String hotelId) {
        if(hotelFinderMap.containsKey(hotelId)) {
            return hotelFinderMap.get(hotelId);
        }
        return null;
    }
}
