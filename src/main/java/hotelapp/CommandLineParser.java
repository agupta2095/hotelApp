package hotelapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to parse the command line arguments, which -hotels and -reviews option followed by path
 * We can add more options to read by adding more variable and modifying parseCommandLineArguments function
 */
public class CommandLineParser {
    /** set of options allowed in command line* */
    final private Map<String, String> hotelSearchOptions;

    public CommandLineParser() {
        hotelSearchOptions = new HashMap<>();
        hotelSearchOptions.put("-hotels", "");
        hotelSearchOptions.put("-reviews", "");
        hotelSearchOptions.put("-output", "");
        hotelSearchOptions.put("-threads", "");
    }
    public String getHotelFilePath() {return hotelSearchOptions.get("-hotels");}
    public String getReviewsDirPath() {return hotelSearchOptions.get("-reviews");}
    public String  getOutputFilePath() {return hotelSearchOptions.get("-output");}
    public String  getNumberOfThreads() {return hotelSearchOptions.get("-threads");}
    /**This method returns false if user didn't provide 4 command line arguments or provided incorrect options
     * @return: boolean value indicating if command line arguments were parsed properly or not **/
    public boolean parseCommandLineArguments(String[] args) {
        int idx = 0;
        while(idx < args.length) {
            if(hotelSearchOptions.containsKey(args[idx])) {
                hotelSearchOptions.put(args[idx], args[++idx]);
                idx++;
            } else {
                System.out.println("You have provided the option '" + args[idx] + "'." +
                        " But you can provide either '-hotels' or '-reviews' as the command line options.");
                return false;
            }
        }
        return true;
    }

    public boolean processCmdLineArguments() {
        String hotelFilePath = getHotelFilePath();
        String reviewsDirPath = getReviewsDirPath();
        String outputFilePath = getOutputFilePath();
        String noOfThreads = getNumberOfThreads();
        int noOfThreadsInt = noOfThreads.isEmpty() ? 1 : Integer.parseInt(noOfThreads);

        UserFilesParser userFilesParser = new UserFilesParser(noOfThreadsInt);
        //userFilesParser.parseHotelsFile(hotelFilePath);
        userFilesParser.findAndParseReviewFiles(reviewsDirPath);
        /*appInterface.printInOutputFile(outputFilePath);

        if(hotelFilePath.isEmpty() || reviewsDirPath.isEmpty()) {
            return false;
        }
        if(!outputFilePath.isEmpty()) {
            return false;
        }*/
        return true;
    }
}
