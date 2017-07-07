package com.rammstein.messenger.model.web.request;

/**
 * Created by user on 28.06.2017.
 */

public class SendMessageRequest {
    private int ChatId;
    private String Content;

    public SendMessageRequest(int chatId, String content) {
        ChatId = chatId;
        Content = content;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    @Override
    public String toString() {
        return "SendMessageRequest{" +
                "ChatId=" + ChatId +
                ", Content='" + Content + '\'' +
                '}';
    }
}
