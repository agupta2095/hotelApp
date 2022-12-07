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

    /** This function prints review for the findWord/findReviews query***/
    public String printReview() {
        String printStr = "";
        printStr += "Review by " + userNickname + " on " + timeStamp + System.lineSeparator();
        printStr += "Rating: " + ratingOverall + System.lineSeparator();
        printStr += "ReviewId: " + reviewId + System.lineSeparator();
        printStr += title + System.lineSeparator();
        printStr += reviewText;
        return printStr;
    }

    public void fillWordFreqMap(String word) {
        try {
            lock.writeLock().lock();
            if (!wordFrequencyMap.containsKey(word)) {
                wordFrequencyMap.put(word, 0);
            }
            Integer k = wordFrequencyMap.get(word);
            wordFrequencyMap.put(word, k + 1);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** This function returns the frequency of a word in the review text
     *
     * @param word : Word to be searched
     * @return : Number of times found
     */
    Integer getFrequencyForEachWordInReview(String word) {
        try {
            lock.readLock().lock();
            if (wordFrequencyMap.containsKey(word)) {
                return wordFrequencyMap.get(word);
            }
            return 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    /** This class is to compare two reviews on the basis of frequency of word for which we are searching and
     * on basis of dates posted if word frequency is same.
     */
    static public class MyComparator implements Comparator<Review> {
        private String word;
        public MyComparator(String word) {
            this.word = word;
        }
        public int compare(Review c1, Review c2) {
            Integer freq1 = c1.getFrequencyForEachWordInReview(word);
            Integer freq2 = c2.getFrequencyForEachWordInReview(word);
            if (!freq1.equals(freq2)) {
                return -1*(freq1.compareTo(freq2));
            } else {
                return -1*(c1.getTimeStamp().compareTo(c2.getTimeStamp()));
            }
        }
    }
}
