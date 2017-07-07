package com.rammstein.messenger.model.web.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 28.06.2017.
 */

public class SenderResponse implements Parcelable{
    int Id;
    String FirstName;
    String LastName;

    public SenderResponse(int id, String firstName, String lastName) {
        Id = id;
        FirstName = firstName;
        LastName = lastName;
    }

    protected SenderResponse(Parcel in) {
        Id = in.readInt();
        FirstName = in.readString();
        LastName = in.readString();
    }

    public static final Creator<SenderResponse> CREATOR = new Creator<SenderResponse>() {
        @Override
        public SenderResponse createFromParcel(Parcel in) {
            return new SenderResponse(in);
        }

        @Override
        public SenderResponse[] newArray(int size) {
            return new SenderResponse[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(FirstName);
        dest.writeString(LastName);
    }
}
