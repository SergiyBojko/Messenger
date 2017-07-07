package com.rammstein.messenger.model.web.response;

import com.rammstein.messenger.model.local.Gender;

/**
 * Created by user on 27.06.2017.
 */

public class UserDetailsResponse {
    int Id;
    String FirstName;
    String LastName;
    int Gender;
    String DateOfBirth;
    String LastModif;
    String RegDate;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public int getGender() {
        return Gender;
    }

    public com.rammstein.messenger.model.local.Gender getGenderEnum(){
        if (Gender == 0 || Gender == 1){
            return com.rammstein.messenger.model.local.Gender.values()[Gender];
        } else {
            return null;
        }
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getLastModif() {
        return LastModif;
    }

    public void setLastModif(String lastModif) {
        LastModif = lastModif;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String regDate) {
        RegDate = regDate;
    }
}
