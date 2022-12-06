package server;

import hotelapp.HotelInformation;
import hotelapp.Review;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

/**
 * Class to handle queries to MYSQL Database
 */
public class DatabaseHandler {

    private final static DatabaseHandler dbHandler = new DatabaseHandler("database.properties"); // singleton pattern
    private final Properties config; // a "map" of properties
    private  String uri = null; // uri to connect to mysql using jdbc
    private final Random random = new Random(); // used in password  generation

    /**
     * DataBaseHandler is a singleton, we want to prevent other classes
     * from creating objects of this class using the constructor
     */
    private DatabaseHandler(String propertiesFile){
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        //System.out.println("uri = " + uri);
    }

    /**
     * Returns the instance of the database handler.
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return dbHandler;
    }


    /** Load info from config file database.properties
     *
     * @param propertyFile
     * @return
     */
    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        }
        catch (IOException e) {
            System.out.println(e);
        }

        return config;
    }

    /**
     * Create users table in MYSql database
     **/
    public void createTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
        }
        catch (SQLException ex) {
             System.out.println(ex);
        }
    }

    /**
     * Create reviews table in MYSql database
     */
    public void createReviewTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_REVIEW_TABLE);
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }

        return hashed;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newuser - username of new user
     * @param newpass - password of new user
     */
    public void registerUser(String newuser, String newpass) {
        // Generate salt
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32); // salt
        String passhash = getHash(newpass, usersalt); // hashed password
        System.out.println(usersalt);

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful for Register User");
            try {
                statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                statement.setString(1, newuser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Authenticate the login of existing user
     * @param username - username of existing user
     * @param password - password of existing user
     * @return boolean value
     * @throws SQLException if any issues with database connection
     */
    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            //System.out.println("dbConnection successful");
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param user - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }

    /**
     * Generate random alphanumeric string for reviews
     * @return random string
     */
    public String generateRandomString() {
        RandomStringGenerator randomStringGenerator =
                new RandomStringGenerator.Builder()
                        .withinRange('0', 'z')
                        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                        .build();
       return randomStringGenerator.generate(8).toUpperCase();
    }

    /**
     * Method to add review to SQL database
     * @param title : Title of review
     * @param reviewText : Text of review
     * @param timeStamp : Time at which review was added
     * @param username : Username of logged-in user
     * @param hotelId : ID of Hotel
     * @param rating : User rating
     */
    public void addReview(String title, String reviewText, String timeStamp,
                          String username, String hotelId, String rating, String reviewId) {
        PreparedStatement statement;
        if(reviewId.isEmpty()) {
            reviewId = generateRandomString();
        }
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.prepareStatement(PreparedStatements.ADD_REVIEW_SQL);
                statement.setString(1, reviewId);
                statement.setString(2, username);
                statement.setString(3, title);
                statement.setString(4, hotelId);
                statement.setString(5, rating);
                statement.setString(6, timeStamp);
                statement.setString(7, reviewText);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     *  Method to modify review to SQL database
     *      * @param title : Title of review
     *      * @param reviewText : Text of review
     *      * @param timeStamp : Time at which review was added
     *      * @param username : Username of logged-in user
     *      * @param hotelId : ID of Hotel
     *      * @param rating : User rating
     */
    public void modifyReview(String title, String reviewText, String timeStamp,
                          String username, String hotelId, String rating) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.prepareStatement(PreparedStatements.MODIFY_REVIEW_SQL);
                statement.setString(1, title);
                statement.setString(2, rating);
                statement.setString(3, timeStamp);
                statement.setString(4, reviewText);
                statement.setString(5, username);
                statement.setString(6, hotelId);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Method to delete review of a logged-in user
     * @param userName : username of logged-in user
     * @param hotelId : ID of Hotel
     */
    public void deleteReview(String userName, String hotelId) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.prepareStatement(PreparedStatements.DELETE_REVIEW_SQL);
                statement.setString(1, hotelId);
                statement.setString(2, userName);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Method to check for unique username while registration
     * @param userName : username entered by the new user
     * @return
     */
    public boolean userNameExists(String userName) {
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(uri,config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.UNIQUE_USER_SQL);
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * Get List of reviews for a given hotel added to MySQL database
     * @param hotelId : ID of the hotel
     * @return
     */
    public Set<Review> getReview(String hotelId) {
        PreparedStatement statement;
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
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_REVIEWS_SQL);
            statement.setString(1, hotelId);
            ResultSet results = statement.executeQuery();
             while(results.next()) {
                 String reviewId = results.getString("reviewId");
                 String title = results.getString("title");
                 int rating = results.getInt("rating");
                 String ratingOverall = String.valueOf(rating);
                 String reviewText = results.getString("text");
                 String timeStamp = results.getString("timeStamp");
                 String userName = results.getString("username");
                 Review reviewObj = new Review(hotelId, reviewId, ratingOverall, title,
                         reviewText, userName, timeStamp);
                 reviews.add(reviewObj);
             }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return reviews;
    }

    /**
     * Get List of reviews for a given hotel and username added to MySQL database
     * @param hotelId : ID for the hotel
     * @param userName : username of the logged-in user
     * @return
     */
    Review getReviewForAUser(String hotelId, String userName) {
        PreparedStatement statement;
        System.out.println("Here in Get Review For A User");

        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_USER_REVIEW_SQL);
            statement.setString(1, hotelId);
            statement.setString(2, userName);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                String reviewId = results.getString("reviewId");
                String title = results.getString("title");
                int rating = results.getInt("rating");
                String ratingOverall = String.valueOf(rating);
                String reviewText = results.getString("text");
                String timeStamp = results.getString("timeStamp");
                Review reviewObj = new Review(hotelId, reviewId, ratingOverall, title,
                        reviewText, userName, timeStamp);
                return reviewObj;
            }
        } catch (SQLException e) {
                System.out.println(e);
        }
        return null;
    }
    public void addFavouriteHotel(String username, String hotelId, String hotelName) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.ADD_FAVOURITE_HOTEL_SQL);
            statement.setString(1, username);
            statement.setString(2, hotelId);
            statement.setString(3,hotelName);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void createFavouriteHotelsTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_FAVOURITE_HOTELS_SQL);
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public Map<String, String> getFavouriteHotels(String username) {
        Map<String, String> hotels = new HashMap<>();
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_FAVOURITE_HOTELS_SQL);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String hotelId = resultSet.getString("hotelId");
                String hotelName = resultSet.getString("hotelName");
                hotels.put(hotelId, hotelName);
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return hotels;
    }

    public void clearFavHotels(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.CLEAR_FAVOURITE_HOTELS_SQL);
            statement.setString(1, username);
            statement.executeUpdate();
            statement.close();
            System.out.println("Successfully cleared Favourite Hotels");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void addExpediaLink(String username, String link, String hotelName) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.ADD_EXPEDIA_LINK_SQL);
            statement.setString(1, username);
            statement.setString(2, link);
            statement.setString(3, hotelName);
            statement.executeUpdate();
            statement.close();
            System.out.println("Successfully added Expedia Link");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void createExpediaLinksTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_EXPEDIA_SQL);
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }
    public void createHotelsTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_HOTELS_TABLE);
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public void addHotel(String name, String id, String city,
                          String state, String address, String longitude, String latitude) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.prepareStatement(PreparedStatements.ADD_HOTEL_SQL);
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setString(3, city);
                statement.setString(4, state);
                statement.setString(5, address);
                statement.setString(6, longitude);
                statement.setString(7, latitude);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    List<HotelInformation> getHotelsWithKeyWordInName(String keyword) {
        List<HotelInformation> hotels = new ArrayList<>();
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_ALL_HOTELS_SQL);
            ResultSet results = statement.executeQuery();
            while(results.next()) {
                String hotelName = results.getString("hotelName");
                if(hotelName.contains(keyword) || keyword.isEmpty()) {
                    String hotelId = results.getString("hotelId");
                    String city = results.getString("city");
                    String state = results.getString("state");
                    String address = results.getString("address");
                    String longitude = results.getString("longitude");
                    String latitude = results.getString("latitude");
                    HotelInformation hotelObj = new HotelInformation(hotelId, hotelName, city, state, address, longitude, latitude);
                    hotels.add(hotelObj);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return hotels;
    }

    HotelInformation getHotel(String hotelId) {
        HotelInformation hotelObj = null;
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_HOTEL_SQL);
            statement.setString(1, hotelId);
            ResultSet results = statement.executeQuery();
            while(results.next()) {
                String hotelName = results.getString("hotelName");
                String city = results.getString("city");
                String state = results.getString("state");
                String address = results.getString("address");
                String longitude = results.getString("longitude");
                String latitude = results.getString("latitude");
                hotelObj = new HotelInformation(hotelId, hotelName, city, state, address, latitude, longitude);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return hotelObj;
    }
    public Map<String, String> getExpediaLinks(String username) {
        Map<String, String> links = new HashMap<>();
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_EXPEDIA_LINKS_SQL);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String link = resultSet.getString("expediaLink");
                String hotelName = resultSet.getString("hotelName");
                links.put(link, hotelName);
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return links;
    }

    public void clearExpediaLinks(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.CLEAR_EXPEDIA_LINKS_SQL);
            statement.setString(1, username);
            statement.executeUpdate();
            statement.close();
            System.out.println("Successfully cleared Expedia Link");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    /**
     * Main function to create table in MYSQL database
     * @param args
     */
    public static void main(String[] args) {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        //dbHandler.createTable();
        dbHandler.createReviewTable();
        //dbHandler.createExpediaLinksTable();
        //dbHandler.createFavouriteHotelsTable();
        //dbHandler.createHotelsTable();
    }
}

