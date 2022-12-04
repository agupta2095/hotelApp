package hotelapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AppInterface {
    /** set of allowed queries **/
    final private HashSet<String> allowedQueries;
    /** To execute find query **/
    final private HotelFinder hotelFinderObj;
    /** To execute findReviews query **/
    final private ReviewsFinder reviewsFinderObj;
    /** To execute findWord query **/
    final private WordFinder wordFinderObj;

    /** Constructor**/
    public AppInterface() {
        allowedQueries = new HashSet<>();
        allowedQueries.add("find");
        allowedQueries.add("findReviews");
        allowedQueries.add("findWord");
        hotelFinderObj = new ThreadSafeHotelFinder();
        reviewsFinderObj = new ThreadSafeReviewFinder();
        wordFinderObj = new ThreadSafeWordFinder();
    }
    /** Execute Queries **/
    private void callQueries(String[] optArr) {
        if(optArr.length > 1) {
            if (!isValidOption(optArr[0])) {
                System.out.println("You have entered invalid query. Please re-enter.");
                return;
            }
            if(optArr.length == 2) {
                switch (optArr[0]) {
                    case "find": {
                        hotelFinderObj.findHotel(optArr[1]);
                        break;
                    }
                    case "findReviews": {
                        reviewsFinderObj.findReview(optArr[1], this);
                        break;
                    }
                    case "findWord": {
                        wordFinderObj.findWord(optArr[1], this);
                        break;
                    }
                }
            }
        } else {
            System.out.println("You have entered the wrong query.It takes two arguments, command name and its option");
        }

    }
    /** Check if valid query **/
    private boolean isValidOption(String opt) {
        return allowedQueries.contains(opt);
    }

    /** Welcome message **/
    public void welcomeMessage() {
        System.out.println("You can do the following query operations with this application");
        System.out.println("  1. Find Hotel through its id. Type 'find' followed by hotel id. " +
                "For example:-> find 11793");
        System.out.println("  2. Find all the reviews for a hotel with given id. Type 'findReviews' followed by hotel id. " +
                "For example:-> findReviews 5830");
        System.out.println("  3. Find all the reviews that contain a given word. Type 'findWord' followed by the word. " +
                "For example:-> findWord clean");
        System.out.println(" To quit, type 'q' or 'quit'.");
    }
    /** Fill the invert index map and take query input from the user **/
    public void takeInputFromUser() {
        welcomeMessage();
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()) {
            String inp1 = sc.nextLine();
            String [] optArr = inp1.split(" ");
            if (optArr.length == 1) {
                String opt = optArr[0];
                if(opt.equals("q") || opt.equals("quit"))  {return;}
                else if(isValidOption(opt)) {
                    System.out.println("Please provide argument for the option '" + opt + "'");
                    System.out.print(opt + " ");
                    String optVal = sc.nextLine();
                    String [] newOptArr = new String[2];
                    newOptArr[0] = optArr[0];
                    newOptArr[1] = optVal;
                    callQueries(newOptArr);
                } else {
                    System.out.println("You have entered wrong query command");
                }
            } else {
                callQueries(optArr);
            }
        }
    }
    /** Helper methods to fill the required maps **/
    public void addHotel(HotelInformation hotelObj) {
        hotelFinderObj.putInfoInMap(hotelObj.getHotelId(), hotelObj);
    }
    public void addReviews(Set<Review> reviewSet, String hotelId) {
        reviewsFinderObj.putInfoInMap(hotelId, reviewSet);
    }
    public void fillStopWordsMap() {
        wordFinderObj.populateStopWords("input/stopwords.txt");
    }
    public void addInvertIndex() {
        fillStopWordsMap();
        HashSet<Review> allReviewObjs = reviewsFinderObj.getAllReviews();
        for(Review reviewObj : allReviewObjs) {
            wordFinderObj.parseReviewTextAndFillMap(reviewObj);
        }
        wordFinderObj.sortWordReviews();
    }
    public void printInOutputFile(String outputFilePath) {
        if(outputFilePath.isEmpty()) {
            return;
        }
        try {
            PrintWriter pr = new PrintWriter(outputFilePath, StandardCharsets.UTF_8);
            pr.println();
            hotelFinderObj.printInOutputFile(pr, reviewsFinderObj);
            pr.close();
        } catch(IOException except) {
            System.out.println("Can't open file for writing"+ except);
        }
    }
    public List<HotelInformation> getHotelsWithKeyWordInName(String keyword) {
       return hotelFinderObj.searchHotels(keyword);
    }
    public HotelInformation getHotel(String hotelId) {
        return hotelFinderObj.getHotel(hotelId);
    }

    public void fillAvgRating() {reviewsFinderObj.fillTotalRatings();}

    public Double getAverageRating(String hotelId, Set<Review> newReviews) {
        Map<String, Double> curRating = reviewsFinderObj.getAvgRating(hotelId);
        double  totalRating = 0.0;
        double totalCnt = 0.0;
        if(curRating != null) {
            totalRating = curRating.get("totalRating");
            totalCnt = curRating.get("totalCnt");
        }
        if(newReviews != null) {
            for (Review review : newReviews) {
                totalRating += Double.parseDouble(review.getRatingOverall());
                totalCnt += 1.0;
            }
        }
        return totalRating/totalCnt;
    }

    public Set<Review> getReviewsForAHotel(String hotelId) {
        return reviewsFinderObj.getReviewsForAHotel(hotelId);
    }
}
