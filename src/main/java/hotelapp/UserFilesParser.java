package hotelapp;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.DatabaseHandler;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/** This class contains method to parse the reviews directory path
 * find all the reviews files located at the path
 * Read json file to get review objects.
 *  and populate a map of them to be used in queries call
 */
public class UserFilesParser {
    private final ExecutorService executor;
    private final Logger logger = LogManager.getLogger();
    private final Phaser phaser;
    private final AppInterface appInterface;

    /**
     * Constructor of the class
     * @param nThreads
     * @param appInterface
     */
    public UserFilesParser(int nThreads, AppInterface appInterface) {
        this.executor = Executors.newFixedThreadPool(nThreads);
        this.phaser = new Phaser();
        this.appInterface = appInterface;
    }

    /**
     * Class to parse each review file and create review objects
     **/
    public class ReviewFileWorker implements  Runnable {
        private Set<Map<String, String>> reviewSet;
        private String reviewFilePath;
        private String hotelId = "";
        public ReviewFileWorker(String reviewFilePath) {
            this.reviewFilePath = reviewFilePath;
            reviewSet = new HashSet<>();
        }
        @Override
        public void run() {
            try (FileReader filereader = new FileReader(reviewFilePath)) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jo = (JsonObject) jsonParser.parse(filereader);
                JsonObject reviewDetailsObj = (JsonObject) jo.get("reviewDetails");
                JsonObject reviewCollectionObj = (JsonObject) reviewDetailsObj.get("reviewCollection");
                JsonArray jArr = reviewCollectionObj.getAsJsonArray("review");
                DatabaseHandler handler = DatabaseHandler.getInstance();
                for (JsonElement elem : jArr) {
                    JsonObject jsonObj = elem.getAsJsonObject();
                    String hotelId = jsonObj.get("hotelId").getAsString();
                    String reviewText = jsonObj.get("reviewText").getAsString();
                    String ratingOverall = jsonObj.get("ratingOverall").getAsString();
                    String reviewId = jsonObj.get("reviewId").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String userNickname = jsonObj.get("userNickname").getAsString();
                    String date = jsonObj.get("reviewSubmissionTime").getAsString();

                    Map<String, String> propertyMap = new HashMap<>();
                    propertyMap.put("id", reviewId);
                    propertyMap.put("hotelId", hotelId);
                    propertyMap.put("text", reviewText);
                    propertyMap.put("rating", ratingOverall);
                    propertyMap.put("title", title);
                    propertyMap.put("user", userNickname);
                    propertyMap.put("time", date);
                    reviewSet.add(propertyMap);
                    this.hotelId = hotelId;
                }
                addReviewsToDB(reviewSet, hotelId);
                logger.debug("Worker is done processing " + reviewFilePath + "  totalReviews = " + reviewSet.size());
            } catch (IOException except) {
                System.out.println("Could not read the file:" + except);
            } finally {
                phaser.arriveAndDeregister();
            }
        }
    }

    /**
     * @param reviewSet : reviews processed by a single thread
     * @param hotelId : Hotel ID of the hotel for which reviews are processed
    */
    public void addReviewsToDB(Set<Map<String, String>> reviewSet, String hotelId) {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        for(Map<String, String> propertyMap : reviewSet) {
            String reviewText = propertyMap.get("text");
            String ratingOverall = propertyMap.get("rating");
            String reviewId = propertyMap.get("id");
            String title = propertyMap.get("title");
            String userNickname = propertyMap.get("user");
            String date = propertyMap.get("time");
            handler.addReview(title, reviewText,date, userNickname, hotelId, ratingOverall, reviewId);
        }
    }

    /**
     * Shut down Executor
     */
    public void shutdownExecutor() {
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    /** Main Function to find all the review json files in a directory
     * @param dirPath : Path of directory which contains reviews
     * **/
    private void parseReviewFiles(Path dirPath) {
        try (DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(dirPath)) {
            for (Path path : pathsInDir) {
                if (Files.isDirectory(path)) {
                    parseReviewFiles(path);
                } else if(path.toString().endsWith(".json")) {
                    ReviewFileWorker worker =  new ReviewFileWorker(path.toString());
                    logger.debug("Created a worker for " + path);
                    phaser.register();
                    executor.submit(worker);
                }
            }
        } catch (IOException except) {
            System.out.println("Can not open directory: " + except);
        }
    }

    /** Wrapper Function to find all the review json files
     * @param dirPathStr : Path of directory which contains reviews
     * in a directory **/
    public void findAndParseReviewFiles(String dirPathStr) {
        if(dirPathStr.isEmpty()) {
            return;
        }
        Path dirPath = Paths.get(dirPathStr);
        parseReviewFiles(dirPath);
        int phase = phaser.getPhase();
        phaser.awaitAdvance(phase);
        shutdownExecutor();
    }

    /** Function to read hotel details from the provided file path *
     * @param hotelFilePath : Path of file containing information of all the hotels
     * */
    public void parseHotelsFile(String hotelFilePath) {
        if(hotelFilePath.isEmpty()) {
            return;
        }
        try (FileReader filereader = new FileReader(hotelFilePath)) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jo = (JsonObject) jsonParser.parse(filereader);
            DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
            JsonArray jArr = jo.getAsJsonArray("sr");
            for(JsonElement element : jArr) {
                String city = "", state = "", address = "";
                JsonObject jsonObj = element.getAsJsonObject();
                String hotelId = jsonObj.get("id").getAsString();
                String hotelName = jsonObj.get("f").getAsString();
                if(jsonObj.get("ci") != null) {
                    city = jsonObj.get("ci").getAsString();
                }
                if(jsonObj.get("pr") != null) {
                    state = jsonObj.get("pr").getAsString();
                }
                if(jsonObj.get("ad") != null) {
                    address = jsonObj.get("ad").getAsString();
                }
                JsonObject longLat = jsonObj.get("ll").getAsJsonObject();
                String lat = longLat.get("lat").getAsString();
                String lng = longLat.get("lng").getAsString();
                databaseHandler.addHotel(hotelName, hotelId, city, state, address, lng, lat);
            }
        } catch (IOException except) {
            System.out.println("Could not read the file:" + except);
        }
    }
}
