package com.rammstein.messenger.model.web.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 28.06.2017.
 */

public class MessageResponse implements Parcelable{
    int Id;
    int ChatId;
    SenderResponse Sender;
    String Content;
    String Time;

    public MessageResponse(int id, SenderResponse sender, String content, String time) {
        Id = id;
        Sender = sender;
        Content = content;
        Time = time;
    }

    protected MessageResponse(Parcel in) {
        Id = in.readInt();
        ChatId = in.readInt();
        Sender = in.readParcelable(SenderResponse.class.getClassLoader());
        Content = in.readString();
        Time = in.readString();
    }

    public static final Creator<MessageResponse> CREATOR = new Creator<MessageResponse>() {
        @Override
        public MessageResponse createFromParcel(Parcel in) {
            return new MessageResponse(in);
        }

        @Override
        public MessageResponse[] newArray(int size) {
            return new MessageResponse[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public SenderResponse getSender() {
        return Sender;
    }

    public void setSender(SenderResponse sender) {
        Sender = sender;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getChatId() {
        return ChatId;
    }

    public void setChatId(int chatId) {
        ChatId = chatId;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "Id=" + Id +
                ", Sender=" + Sender +
                ", Content='" + Content + '\'' +
                ", Time='" + Time + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(ChatId);
        dest.writeParcelable(Sender, flags);
        dest.writeString(Content);
        dest.writeString(Time);
    }
}
