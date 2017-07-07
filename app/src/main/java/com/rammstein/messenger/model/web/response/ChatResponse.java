package com.rammstein.messenger.model.web.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 28.06.2017.
 */

public class ChatResponse implements Parcelable{
    int Id;
    String Name;
    boolean IsDialog;
    UserDetailsResponse Creator;
    int Temperature;

    protected ChatResponse(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        IsDialog = in.readByte() != 0;
        Temperature = in.readInt();
    }

    public static final Parcelable.Creator<ChatResponse> CREATOR = new Creator<ChatResponse>() {
        @Override
        public ChatResponse createFromParcel(Parcel in) {
            return new ChatResponse(in);
        }

        @Override
        public ChatResponse[] newArray(int size) {
            return new ChatResponse[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean getIsDialog() {
        return IsDialog;
    }

    public void setIsDialog(boolean isDialog) {
        IsDialog = isDialog;
    }

    public UserDetailsResponse getCreator() {
        return Creator;
    }

    public void setCreator(UserDetailsResponse creator) {
        Creator = creator;
    }

    public boolean isDialog() {
        return IsDialog;
    }

    public void setDialog(boolean dialog) {
        IsDialog = dialog;
    }

    public int getTemperature() {
        return Temperature;
    }

    public void setTemperature(int temperature) {
        Temperature = temperature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatResponse that = (ChatResponse) o;

        return Id == that.Id;

    }

    @Override
    public int hashCode() {
        return Id;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "Id=" + Id +
                ", Name='" + Name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeByte((byte) (IsDialog ? 1 : 0));
        dest.writeInt(Temperature);
    }
}
