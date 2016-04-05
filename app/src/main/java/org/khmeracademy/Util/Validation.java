package org.khmeracademy.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sok-ngim on 1/12/16.
 */
public class Validation {
    public boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }


   /* (/^(?=.*\d)(?=.*[A-Z])([@$%&#])[0-9a-zA-Z]{4,}$/)
            (/^
            (?=.*\d)                //should contain at least one digit
            (?=.*[@$%&#])           //should contain at least one special char
            (?=.*[A-Z])             //should contain at least one upper case
            [a-zA-Z0-9]{4,}         //should contain at least 8 from the mentioned characters
    $/)*/

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*\\d).{4,8}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
