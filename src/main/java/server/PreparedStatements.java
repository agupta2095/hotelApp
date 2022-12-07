package server;

public class PreparedStatements {
    /** Prepared Statements  */
    /** For creating the users table */
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE usersT (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL," +
                    "loginTimeStamp VARCHAR(64) NOT NULL);";

    /** Used to insert a new user into the database. */
    public static final String REGISTER_SQL =
            "INSERT INTO usersT (username, password, usersalt, loginTimeStamp) " +
                    "VALUES (?, ?, ?, ?);";

    public static final String LOGIN_TIMESTAMP_SQL=
            "UPDATE usersT SET loginTimeStamp = ? " +
                    "WHERE username = ?;";

    public static final String GET_LAST_LOGIN =
            "SELECT loginTimeStamp FROM usersT where username = ?;";
    /** Used to retrieve the salt associated with a specific user. */
    public static final String SALT_SQL =
            "SELECT usersalt FROM usersT WHERE username = ?";

    /** Used to authenticate a user. */
    public static final String AUTH_SQL =
            "SELECT username FROM usersT " +
                    "WHERE username = ? AND password = ?";

    /** Used to create review table. */
    public static final String CREATE_REVIEW_TABLE =
                                  "CREATE TABLE reviewsTable (" +
                                          "reviewId VARCHAR(32) NOT NULL PRIMARY KEY, " +
                                          "username VARCHAR(32) NOT NULL, " +
                                          "title VARCHAR(64) NOT NULL, " +
                                          "hotelId VARCHAR(64) NOT NULL, " +
                                          "rating INTEGER NOT NULL, " +
                                          "timeStamp VARCHAR(64) NOT NULL, " +
                                          "text VARCHAR(2047) NOT NULL);" ;

    /** Used to add review to the reviews table**/
    public static final String ADD_REVIEW_SQL =
            "INSERT INTO reviewsTable (reviewId, username, title, hotelId, rating, timeStamp, text)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

    /** Used to get review for a user and hotelId from reviews table **/
    public static final String GET_USER_REVIEW_SQL=
            "SELECT * from reviewsTable WHERE reviewsTable.hotelId = ? AND reviewsTable.username = ?;";

    /** Used to validate unique username **/
    public static final String  UNIQUE_USER_SQL =
            "SELECT * from usersT WHERE username = ?;";

    /** Used to get all the reviews added for a particular hotel **/
    public static final String GET_REVIEWS_SQL=
            "SELECT * from reviewsTable WHERE reviewsTable.hotelId = ?;";

    /** Used to modify reviews for a particular user and hotel **/
    public static final String MODIFY_REVIEW_SQL=
            "UPDATE reviewsTable SET title = ?, rating = ?, timeStamp = ?, text = ? " +
                    "WHERE reviewsTable.username= ? AND reviewsTable.hotelId = ?;";

    /** Used to delete review from the database**/
    public static final String DELETE_REVIEW_SQL=
            "DELETE from reviewsTable WHERE hotelId = ? AND username = ?";

    public static final String CREATE_HOTELS_TABLE =
            "CREATE TABLE hotels ("+
             "hotelId INTEGER UNIQUE NOT NULL, "+
             "hotelName VARCHAR(127) NOT NULL, " +
             "city VARCHAR(32) NOT NULL, " +
             "state VARCHAR(32) NOT NULL, " +
             "address VARCHAR(255) NOT NULL, "+
             "longitude VARCHAR(32) NOT NULL, " +
                    "latitude VARCHAR(32) NOT NULL);";

    public static final String ADD_HOTEL_SQL=
            "INSERT INTO hotels (hotelId, hotelName, city, state, address, longitude, latitude) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

    public static final String GET_ALL_HOTELS_SQL =
      "SELECT * FROM hotels";

    public static final String GET_HOTEL_SQL =
            "SELECT * FROM hotels WHERE hotelId = ?;";
    public static final String CREATE_EXPEDIA_SQL=
            "CREATE TABLE expediaLinks (" +
            "username VARCHAR(32) NOT NULL, " +
            "expediaLink VARCHAR(255) NOT NULL, " +
                    "hotelName VARCHAR(64) NOT NULL);";

    public static final String ADD_EXPEDIA_LINK_SQL=
            "INSERT INTO expediaLinks (username, expediaLink, hotelName) VALUES(?, ?, ?);";

    public static final String GET_EXPEDIA_LINKS_SQL =
            "SELECT * from expediaLinks WHERE username = ?;";

    public static final String CLEAR_EXPEDIA_LINKS_SQL=
            "DELETE FROM expediaLinks WHERE username = ?;";
    public static final String CREATE_FAVOURITE_HOTELS_SQL=
            "CREATE TABLE favHotels (" +
                    "username VARCHAR(32) NOT NULL, " +
                    "hotelId VARCHAR(32) NOT NULL, " +
                    "hotelName VARCHAR(255) NOT NULL);";

    public static final String ADD_FAVOURITE_HOTEL_SQL=
            "INSERT INTO favHotels (username, hotelId, hotelName) VALUES(?, ?, ?);";

    public static final String CLEAR_FAVOURITE_HOTELS_SQL =
            "DELETE FROM favHotels WHERE username = ?";

    public static final String GET_FAVOURITE_HOTELS_SQL =
            "SELECT * from favHotels WHERE username = ?";

}
