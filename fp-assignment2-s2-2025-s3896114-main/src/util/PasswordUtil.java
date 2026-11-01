package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;


public class PasswordUtil {

    // Hash password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }


    // Check password strength
    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 8){
            return false;
    }

    boolean hasUpper = false;
    boolean hasLower = false;
    boolean hasDigit = false;
    boolean hasSpecial = false;

    String specialChars = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`";

    for(char c :password.toCharArray()){


        if (Character.isUpperCase(c)) hasUpper = true;
        else if (Character.isLowerCase(c)) hasLower = true;
        else if (Character.isDigit(c)) hasDigit = true;
        else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
    }

    return hasUpper && hasLower && hasDigit && hasSpecial;
  }

}
