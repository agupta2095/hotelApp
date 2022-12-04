package hotelapp;


import java.io.PrintWriter;
import java.util.*;

/** Class to implement findReviews query
 * @author akanksha
 */
public class ReviewsFinder {
    /** {@code @hotelReviewMap} : Map to store word vs list of reviews containing it */
    final private Map<String, Set<Review>> hotelReviewMap;
    final private Map<String, Map<String, Double>> avgHotelRatings;
    /** Constructor for the class **/
    public ReviewsFinder() {
        hotelReviewMap = new TreeMap<>();
        avgHotelRatings = new HashMap<>();
    }

    /** Method to add the information in the map
     * @param reviewSet : Set of all the reviews
     * @param hotelId : Hotel ID to find the reviews
     *  **/
    public void putInfoInMap(String hotelId, Set<Review> reviewSet) {
        if(!hotelReviewMap.containsKey(hotelId)) {
            hotelReviewMap.put(hotelId, new TreeSet<>(new Comparator<Review>() {
                @Override
                public int compare(Review review1, Review review2) {
                    if(!review1.getTimeStamp().equals(review2.getTimeStamp())) {
                        return -1*(review1.getTimeStamp().compareTo(review2.getTimeStamp()));
                    }
                    else {
                        return (review1.getReviewId().compareTo(review2.getReviewId()));
                    }
                }
            }));
        }
        Set<Review> listOfReviews = hotelReviewMap.get(hotelId);
        listOfReviews.addAll(reviewSet);
    }

    /** Main function to called to find reviews in the map
     * @param hotelId : Hotel searched for
     * @param appInterface : Main interface of the application
     * **/
    public void findReview(String hotelId, AppInterface appInterface) {
        Set<Review> allReviews;
        if (hotelReviewMap.containsKey(hotelId)) {
            allReviews = hotelReviewMap.get(hotelId);
            for(Review review : allReviews) {
                System.out.println(review.printReview());
                System.out.println("--------------------");
            }
        } else {
            System.out.println("The application can't find reviews for hotel with id: " + hotelId);
        }
    }

    /**
     * Get reviews of a hotel in sorted order by date and then by review Id
     * @param hotelId : hotel to search for
     * @return
     */
    public Set<Review> getReviewsForAHotel(String hotelId) {
        Set<Review> reviews = new TreeSet<>(new Comparator<Review>() {
            @Override
            public int compare(Review review1, Review review2) {
                if(!review1.getTimeStamp().equals(review2.getTimeStamp())) {
                    return -1*(review1.getTimeStamp().compareTo(review2.getTimeStamp()));
                }
                else {
                    return (review1.getReviewId().compareTo(review2.getReviewId()));
                }
            }
        });
        if (hotelReviewMap.containsKey(hotelId)) {
            for(Review review: hotelReviewMap.get(hotelId)) {
                reviews.add(review);
            }
        } else {
            reviews = null;
        }
        return reviews;
    }

    /**
     * Write reviews of hotel in an output file
     * @param pr : Writer to write in output file
     * @param hotelId: Hotel to search for
     */
    public void printInOutputFile(PrintWriter pr, String hotelId) {
        if(hotelReviewMap.containsKey(hotelId)) {
            Set<Review> allReviews = hotelReviewMap.get(hotelId);
            for(Review review : allReviews) {
                pr.println("--------------------");
                pr.println(review.printReview());
            }
            pr.println();
        } else {
            pr.println();
        }
    }

    /**
     * Method to get all the Review Objs of all the hotels
     * @return: Set of ordered Reviews objects
     */
    public HashSet<Review> getAllReviews() {
        HashSet<Review> allReviews = new HashSet<>();
        for(Map.Entry<String, Set<Review>> keyVal: hotelReviewMap.entrySet()) {
            allReviews.addAll(keyVal.getValue());
        }
        return allReviews;
    }

    /**
     * Method to fill the average rating of each hotel
     */
    public void fillTotalRatings() {
        for(Map.Entry<String, Set<Review>> keyVal: hotelReviewMap.entrySet()) {
            Double totalRating = 0.0;
            Double totalCnt = 0.0;
            for(Review review : keyVal.getValue()) {
                totalRating += Double.parseDouble(review.getRatingOverall());
                totalCnt+=1.0;
            }
            Map<String, Double> ratingList = new HashMap<>();
            ratingList.put("totalRating", totalRating);
            ratingList.put("totalCnt", totalCnt);
            avgHotelRatings.put(keyVal.getKey(), ratingList);
        }
    }

    /**
     * Method to get the average rating of a particular hotel
     * @param hotelId : Hotel to search for
     * @return: average rating for hotel
     */
    public Map<String, Double> getAvgRating(String hotelId) {
        if(avgHotelRatings.containsKey(hotelId)) {
            return avgHotelRatings.get(hotelId);
        }
        return null;
    }
}
