package com.rammstein.messenger.repository;

import com.rammstein.messenger.model.Chat;
import com.rammstein.messenger.model.Message;
import com.rammstein.messenger.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17.05.2017.
 */

public class TestChatRepository implements Repository<Chat> {

    private ArrayList<Chat> mChats;
    private static TestChatRepository mInstance = new TestChatRepository();

    public TestChatRepository() {
        mChats = new ArrayList<>();
        Repository<UserDetails> users = TestUserDetailRepository.getInstance();
        Chat c1 = new Chat(1, "first Chat");
        Chat c2 = new Chat(2, "second Chat");
        Chat c3 = new Chat(3, "chat 3");
        Chat c4 = new Chat(4, "chat 4");
        Chat c5 = new Chat(5, "chat 5");
        c1.getChatMembers().add(users.getById(1));
        c1.getChatMembers().add(users.getById(2));
        c1.getChatMembers().add(users.getById(3));
        c1.getChatMembers().add(users.getById(4));
        c1.getChatMembers().add(users.getById(5));
        c1.getChatMembers().add(users.getById(6));
        c1.getChatMembers().add(users.getById(7));
        c1.getChatMembers().add(users.getById(8));

        c1.getMessages().add(new Message(0, 1, 1, "Hi!", System.currentTimeMillis()-100000));
        c1.getMessages().add(new Message(1, 1, 2, "Hello!", System.currentTimeMillis()-50000));
        c2.getChatMembers().add(users.getById(3));
        c2.getChatMembers().add(users.getById(4));
        c2.getMessages().add(new Message(2, 2, 3, "Lorem ipsum", System.currentTimeMillis()-1000*60*60*48-30000));
        c2.getMessages().add(new Message(3, 2, 4, "dolor sit amet", System.currentTimeMillis()-1000*60*60*48-20000));
        c2.getMessages().add(new Message(4, 2, 4, "consectetur adipiscing elit", System.currentTimeMillis()-1000*60*60*48-10000));
        c2.getMessages().add(new Message(5, 2, 0, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-1000*60*60*48));
        c2.getMessages().add(new Message(5, 2, 3, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-1000*60*60*48));
        c3.getChatMembers().add(users.getById(1));
        c3.getChatMembers().add(users.getById(2));
        c3.getChatMembers().add(users.getById(3));
        c3.getMessages().add(new Message(0, 1, 1, "Hi!", System.currentTimeMillis()-100000));
        c3.getMessages().add(new Message(1, 1, 2, "Hello!", System.currentTimeMillis()-50000));
        c4.getChatMembers().add(users.getById(3));
        c4.getChatMembers().add(users.getById(4));
        c4.getMessages().add(new Message(2, 2, 3, "Lorem ipsum", System.currentTimeMillis()-1000*60*60*120-30000));
        c4.getMessages().add(new Message(3, 2, 4, "dolor sit amet", System.currentTimeMillis()-1000*60*60*120-20000));
        c4.getMessages().add(new Message(4, 2, 4, "consectetur adipiscing elit", System.currentTimeMillis()-1000*60*60*120-10000));
        c4.getMessages().add(new Message(5, 2, 0, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-1000*60*60*120));
        c4.getMessages().add(new Message(5, 2, 3, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-1000*60*60*120));
        c4.getMessages().add(new Message(5, 2, 0, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-1000*60*60*120));
        c4.getMessages().add(new Message(5, 2, 3, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-1000*60*60*120));
        c4.getMessages().add(new Message(5, 2, 3, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-1000*60*60*120));
        c5.getMessages().add(new Message(5, 2, 0, "Vestibulum tellus sapien, sagittis non gravida ut, dapibus sit amet massa.", System.currentTimeMillis()-(long)1000*60*60*24*60));
        mChats.add(c1);
        mChats.add(c3);
        mChats.add(c2);
        mChats.add(c4);
        mChats.add(c5);
    }

    public static Repository getInstance() {
        return mInstance;
    }

    @Override
    public void add(Chat chat) {
        mChats.add(chat);
    }

    @Override
    public Chat get(int index) {
        return mChats.get(index);
    }

    public Chat getById(int id){
        for (Chat c: mChats) {
            if (c.getId() == id){
                return c;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Chat> getAll() {
        return mChats;
    }

    @Override
    public void remove(Chat chat) {
        mChats.remove(chat);
    }

    @Override
    public void update(Chat chat) {
        mChats.remove(chat);
        mChats.add(chat);
    }
}
