package hotelapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** This class represents one customer review */
public class Review {
    private final String hotelId;
    private final String reviewId;
    private final String ratingOverall;
    private final String title;
    private final String reviewText;
    private final String userNickname;
    private String timeStamp;
    private final Map<String, Integer> wordFrequencyMap;
    public String getTimeStamp() {return timeStamp;}
    public String getReviewText() {return reviewText;}
    public String getReviewId() {return reviewId;}
    public String getRatingOverall() {return ratingOverall;}
    public String getTitle() {return title;}
    public String getUserName() {return  userNickname;}


    ReentrantReadWriteLock lock;

    public Review(String hotelId, String reviewId, String ratingOverall,
                  String title, String reviewText, String userNickname, String timeStamp) {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.ratingOverall = ratingOverall;
        this.title = title;
        this.reviewText = reviewText;
        this.userNickname = userNickname.length() >0 ? userNickname : "Anonymous";
        this.timeStamp = timeStamp;
        formatTimeStamp();
        wordFrequencyMap = new HashMap<>();
        lock = new ReentrantReadWriteLock();
    }

    /** This function formats the string specifying date into readable Date form
     * Found some examples in which the date in json file is not in the form "yyyy-MM-dd'T'HH:mm:ss'Z'"
     * So handled using try catch statements**/
    protected void formatTimeStamp() {
        String beforeTimeStamp = timeStamp;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            timeStamp = String.valueOf(LocalDate.parse(timeStamp, formatter));
        } catch (DateTimeParseException e) {
            timeStamp = beforeTimeStamp;
        }
    }
}
