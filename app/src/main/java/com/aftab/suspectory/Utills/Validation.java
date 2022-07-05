package com.aftab.suspectory.Utills;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by FA on 3/24/2021.
 */
public class Validation {/*Returns false if both names are valid and true if they aren't*/

    public static boolean errcheck(String name, int security) {
        while (true) {
            if (security >= 2 && !checklen(name)) break;
            if (security >= 3 && !checknum(name)) break;
            if (security >= 4 && !checkvowel(name)) break;
            if (security >= 5 && !checkcon(name)) break;
            return true;
        }
        return false;
    }

    /*Checks if length of both strings are less than three*/
    public static boolean checklen(String name) {
        if (name.length() < 3)
            return false;
        return true;
    }

    /*Checks if both strings contain non-alphabetic characters excluding a space character*/
    public static boolean checknum(String name) {
        int length = 0, i = 0;
        char c;
        String Fullname;

        length = name.length();


        for (i = 0; i < length; i++)  //Check for `name`
        {
            c = name.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) && c != ' ') {
                return false;
            }
        }


        return true;
    }

    /*Checks if both strings contain more than 2 consecutive vowels. Also checks if they contain more than 3 vowels. Both are done in each word*/
    public static boolean checkvowel(String name) {
        int numv = 0, conv = 0, length = name.length();
        char c;
        int i;
        char[] vowel = {'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U', '\0'};
        for (i = 0; i < length; i++) {
            c = name.charAt(i);
            for (char test : vowel) {
                if (c != ' ') {

                    if (test == c) //If current char is a vowel
                    {
                        numv++;
                        conv++; //Increase counters
                        if (conv > 2 || numv > 3) //Invalid name detected
                            return false;
                        break;
                    }
                    if (test == '\0') conv = 0;
                } else {
                    numv = conv = 0;
                    break;
                } //New word. So reset counters
            }
        }
        numv = conv = 0;

        return true;
    }

    /*Checks if both strings contain more than 2 consecutive same consonants. Also checks if they contain more than 4 consonants. Both are done in each word*/
    public static boolean checkcon(String name) {
        int num = 0, length = name.length(), con = 0;
        char c, tmp = 'a';
        int i;
        for (i = 0; i < length; i++) {
            if ((c = name.charAt(i)) != 'a' && c != 'e' && c != 'i' && c != 'o' && c != 'u' && c != 'A' && c != 'E' && c != 'I' && c != 'O' && c != 'U' && c != ' ') //If current character is not a vowel or a space
            {
                num++;
                if (tmp != 'a' && c == tmp)
                    con++;
                if (num > 3 || con > 1) //Invalid name
                    return false;
                tmp = c;
                continue;
            }
            num = 0;
            con = 0;
            tmp = 'a';
        }
        num = 0;
        con = 0;
        tmp = 'a'; //Reset everything

        return true;
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPwd(CharSequence target) {
        if (target.length() < 6)
            return false;
        else return true;
    }

    public static boolean isValidPhone(CharSequence target) {
        if (target.length() < 11)
            return false;
        else return true;
    }

    public static boolean isValidURL(String url) {
        boolean b = Patterns.WEB_URL.matcher(url).matches();
        return b;
    }

}
