package hotelapp;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
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
        private Set<Review> reviewSet;
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
                for (JsonElement elem : jArr) {
                    JsonObject jsonObj = elem.getAsJsonObject();
                    String hotelId = jsonObj.get("hotelId").getAsString();
                    String reviewText = jsonObj.get("reviewText").getAsString();
                    String ratingOverall = jsonObj.get("ratingOverall").getAsString();
                    String reviewId = jsonObj.get("reviewId").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String userNickname = jsonObj.get("userNickname").getAsString();
                    String date = jsonObj.get("reviewSubmissionTime").getAsString();
                    Review reviewObj = new Review(hotelId, reviewId, ratingOverall, title,
                            reviewText, userNickname, date);
                    reviewSet.add(reviewObj);
                    this.hotelId = hotelId;
                }
                addReviewsToMainMap(reviewSet, hotelId);
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
    public void addReviewsToMainMap(Set<Review> reviewSet, String hotelId) {
        appInterface.addReviews(reviewSet, hotelId);
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
        Gson gson = new Gson();
        try (FileReader filereader = new FileReader(hotelFilePath)) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jo = (JsonObject) jsonParser.parse(filereader);
            HotelInformation [] hotelArr = gson.fromJson(jo.getAsJsonArray("sr"),HotelInformation[].class);
            for(HotelInformation obj : hotelArr) {
                appInterface.addHotel(obj);
            }
        } catch (IOException except) {
            System.out.println("Could not read the file:" + except);
        }
    }
}
