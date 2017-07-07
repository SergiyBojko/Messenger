package com.rammstein.messenger.model.web.request;

/**
 * Created by user on 27.06.2017.
 */

public class CreateChatRequest {
    private String name;
    private int[] membersIds;
    private String isDialog;

    public CreateChatRequest(String name, int[] membersIds, boolean isDialog) {
        this.name = name;
        this.membersIds = membersIds;
        this.isDialog = Boolean.toString(isDialog);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getMembersIds() {
        return membersIds;
    }

    public void setMembersIds(int[] membersIds) {
        this.membersIds = membersIds;
    }

    public boolean isDialog() {
        return Boolean.parseBoolean(isDialog);
    }

    public void setDialog(boolean dialog) {
        isDialog = Boolean.toString(dialog);
    }
}
