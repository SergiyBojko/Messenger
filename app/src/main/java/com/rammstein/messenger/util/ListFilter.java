package com.rammstein.messenger.util;

import com.rammstein.messenger.model.local.Gender;
import com.rammstein.messenger.model.local.UserDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.rammstein.messenger.activity.SearchActivity.UNSPECIFIED;

/**
 * Created by user on 14.06.2017.
 */

public class ListFilter {
    public static ArrayList<UserDetails> filterUserDetails(ArrayList<UserDetails> userDetailsList, String query) {
        ArrayList<UserDetails> result = new ArrayList<>();
        query = query.toLowerCase();
        String[] queries = query.split(" ");
        for (UserDetails userDetails : userDetailsList){
            String fname = userDetails.getFirstName().toLowerCase();
            String lname = userDetails.getLastName().toLowerCase();

            boolean isValid = true;
            for (String q : queries){
                if (!fname.contains(q) && !lname.contains(q)){
                    isValid = false;
                    break;
                }
            }

            if (isValid){
                result.add(userDetails);
            }
        }
        return result;
    }

    public static ArrayList<UserDetails> filterUsersByAge(ArrayList<UserDetails> userDetailsList, int minAge, int maxAge) {
        ArrayList<UserDetails> result = new ArrayList<>(userDetailsList);
        if (minAge == UNSPECIFIED && maxAge == UNSPECIFIED){
            return result;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        GregorianCalendar now = new GregorianCalendar();
        if (minAge != UNSPECIFIED){
            for (UserDetails userDetails: userDetailsList){
                if (userDetails.getBirthday() != null){
                    calendar.setTimeInMillis(userDetails.getBirthday().getTime());
                    calendar.add(Calendar.YEAR, minAge);
                    if (calendar.after(now)){
                        result.remove(userDetails);
                    }
                } else {
                    result.remove(userDetails);
                }
            }
        }

        if (maxAge != UNSPECIFIED){
            for (UserDetails userDetails: userDetailsList){
                if (userDetails.getBirthday() != null){
                    calendar.setTimeInMillis(userDetails.getBirthday().getTime());
                    calendar.add(Calendar.YEAR, maxAge);
                    if (calendar.before(now)){
                        result.remove(userDetails);
                    }
                } else {
                    result.remove(userDetails);
                }
            }
        }

        return result;
    }

    public static ArrayList<UserDetails> filterUsersByGender(ArrayList<UserDetails> userDetailsList, Gender gender) {
        ArrayList<UserDetails> result = new ArrayList<>(userDetailsList);
        if (gender != null){
            for (UserDetails userDetails : userDetailsList){
                if (!gender.equals(userDetails.getGender())){
                    result.remove(userDetails);
                }
            }
        }
        return result;
    }
}
