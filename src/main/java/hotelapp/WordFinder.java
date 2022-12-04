package hotelapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WordFinder {
    /** @wordReviewsMap :-> Store information about words vs the reviews containing that word **/
    final private Map<String, List<Review>> wordReviewsMap;
    /** @stopWords :-> Set for stop words **/
    static private Set<String> stopWords;

    /** Constructor **/
    WordFinder() {
        wordReviewsMap = new HashMap<>();
        stopWords = new HashSet<>();
    }
    /** Read stop words file and populate the map **/
    void populateStopWords(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String wordLine;
            while ((wordLine = br.readLine()) != null) {
                wordLine = wordLine.strip();
                stopWords.add(wordLine);
            }
        } catch (IOException except) {
            System.out.println("Can't open the stop words file. Please provide valid path");
        }
    }

    /** Method to read review text and build a map of word vs review object**/
    public void parseReviewTextAndFillMap(Review reviewObj) {
        String reviewText = reviewObj.getReviewText();
        String[] strArr = reviewText.split(" ");
        for(String word : strArr) {
            word = word.toLowerCase();
            word = word.replaceAll("\\p{Punct}", "");
            reviewObj.fillWordFreqMap(word);
            if(!stopWords.contains(word)) {
                if(!wordReviewsMap.containsKey(word)) {
                    wordReviewsMap.put(word, new ArrayList<>());
                }
                List<Review> reviewsSet = wordReviewsMap.get(word);
                reviewsSet.add(reviewObj);
            }
        }
    }
    /** Method to sort and take unique reviews from the ArrayList fpr each word **/
    public void sortWordReviews() {
        for (Map.Entry<String, List<Review>> keyVal : wordReviewsMap.entrySet()) {
            List<Review> arrayList = keyVal.getValue();
            ArrayList<Review> arrUnique = new ArrayList<>();
            for (Review review : arrayList) {
                if (!arrUnique.contains(review)) {
                    arrUnique.add(review);
                }
            }
            arrUnique.sort(new Review.MyComparator(keyVal.getKey()));
            arrayList.clear();
            arrayList.addAll(arrUnique);
        }
    }
    /** Function called when findWord query is executed **/
    void findWord(String word, AppInterface appInterface) {
        word = word.toLowerCase();
        if(wordReviewsMap.containsKey(word)) {
            List<Review> reviewsSet = wordReviewsMap.get(word);
            for(Review review: reviewsSet) {
                System.out.println(review.printReview());
                System.out.println("Number of times word '" + word + "' found in review text: " + review.getFrequencyForEachWordInReview(word));
                System.out.println("--------------------");
            }
        } else {
            System.out.println("We can't find the given word in any of the review text");
        }
    }
}
