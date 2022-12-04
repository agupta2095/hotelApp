package server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Password {

    /**
     * Method to validate password String entered by user
     * Checks if it contains at-least 1 alphabet, 1
     * digit and 1 special character and minimum length 6
     * @param password : password string entered by the user
     * @return: boolean value
     */
    public boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$"
        );
        Matcher matcher = pattern.matcher(password);
        if(!matcher.find()) {
            return false;
        }
        return true;
    }

}
