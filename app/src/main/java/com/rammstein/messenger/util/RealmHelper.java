package com.rammstein.messenger.util;

import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.Gender;
import com.rammstein.messenger.model.local.Message;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.response.ChatResponse;
import com.rammstein.messenger.model.web.response.MessageResponse;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Response;

/**
 * Created by user on 20.06.2017.
 */

public class RealmHelper {

    public static Chat findPrivateChat(int userId){
        AppUser appUser = getCurrentUser();
        RealmList<Chat> appUserChats = appUser.getChats();
        Chat privateChat = appUserChats.where()
                .equalTo(Chat.IS_PRIVATE, true)
                .equalTo(Chat.CHAT_MEMBER_ID, userId)
                .findFirst();
        return privateChat;
    }

    public static RealmResults<UserDetails> getSortedContactList() {
        RealmRepository repository = RealmRepository.getInstance();
        AppUser currentUser = getCurrentUser();
        RealmResults<UserDetails> contacts = currentUser.getFriends().sort(UserDetails.FIRST_NAME, Sort.ASCENDING, UserDetails.LAST_NAME, Sort.ASCENDING);
        return contacts;
    }

    public static RealmResults<Chat> getChatList(){
        AppUser currentUser = getCurrentUser();
        return currentUser.getChats().where().findAll();
    }

    public static AppUser getCurrentUser() {
        RealmRepository repository = RealmRepository.getInstance();
        int currentUserId = SharedPreferencesRepository.getInstance().getCurrentUserId();
        return repository.getById(AppUser.class, currentUserId);
    }

    public static void deleteChatHistory(int chatId) {
        Chat chat = RealmRepository.getInstance().getById(Chat.class, chatId);
        RealmRepository repository = RealmRepository.getInstance();
        AppUser currentUser = getCurrentUser();
        RealmList<Message> messages = chat.getMessages();

        repository.beginTransaction();
        messages.deleteAllFromRealm();
        repository.commitTransaction();

        if (!chat.getChatMembers().contains(currentUser.getUserDetails())){
            repository.beginTransaction();
            chat.deleteFromRealm();
            repository.commitTransaction();
        }
    }

    public static void addOrUpdateAppUser(String username, Response<UserDetailsResponse> response, String token, String password) {
        UserDetailsResponse userData = response.body();
        int id = userData.getId();
        String fName = userData.getFirstName();
        String lName = userData.getLastName();
        int genderId = userData.getGender();
        String birthday = userData.getDateOfBirth();
        String lastModif = userData.getLastModif();
        String registration = userData.getRegDate();

        Gender gender = getGender(genderId);
        Date birthdayDate = SimpleDateUtils.parseDateString(birthday);
        Date lastModifDate = SimpleDateUtils.parseDateString(lastModif);
        Date registrationDate = SimpleDateUtils.parseDateString(registration);

        RealmRepository realm = RealmRepository.getInstance();
        AppUser appUser = realm.getById(AppUser.class, id);
        UserDetails userDetails;

        realm.beginTransaction();
        if (appUser == null){
            appUser = realm.getRealm().createObject(AppUser.class, id);
            userDetails = realm.getById(UserDetails.class, id);
            if (userDetails == null){
                userDetails = realm.getRealm().createObject(UserDetails.class, id);
            }
            appUser.setUserDetails(userDetails);
        }
        userDetails = appUser.getUserDetails();
        userDetails.setUsername(username);
        userDetails.setFirstName(fName);
        userDetails.setLastName(lName);
        userDetails.setGender(gender);
        userDetails.setBirthday(birthdayDate);
        userDetails.setLastModif(lastModifDate);
        userDetails.setRegistrationDate(registrationDate);
        appUser.setPassword(password);
        appUser.setAccessToken(token);
        realm.commitTransaction();
    }


    public static UserDetails addOrUpdateUser(UserDetailsResponse userData, boolean shortResponse) {
        RealmRepository realm = RealmRepository.getInstance();
        realm.beginTransaction();
        UserDetails user = addOrUpdateUserRaw(userData, shortResponse);
        realm.commitTransaction();
        return user;
    }

    public static UserDetails addOrUpdateUserRaw(UserDetailsResponse userData, boolean shortResponse) {
        int id = userData.getId();
        String fName = userData.getFirstName();
        String lName = userData.getLastName();
        int genderId = userData.getGender();
        String birthday = userData.getDateOfBirth();
        String lastModif = userData.getLastModif();
        String registration = userData.getRegDate();

        Gender gender = getGender(genderId);
        Date birthdayDate = SimpleDateUtils.parseDateString(birthday);
        Date lastModifDate = SimpleDateUtils.parseDateString(lastModif);
        Date registrationDate = SimpleDateUtils.parseDateString(registration);

        RealmRepository realm = RealmRepository.getInstance();
        UserDetails userDetails;

        userDetails = realm.getById(UserDetails.class, id);
        if (userDetails == null){
            userDetails = realm.getRealm().createObject(UserDetails.class, id);
        }
        userDetails.setFirstName(fName);
        userDetails.setLastName(lName);
        if (!shortResponse){
            userDetails.setGender(gender);
            userDetails.setBirthday(birthdayDate);
            userDetails.setRegistrationDate(registrationDate);
        }
        userDetails.setLastModif(lastModifDate);

        return userDetails;
    }


        private static Gender getGender(int genderId) {
        if (genderId == 0 || genderId == 1){
            return Gender.values()[genderId];
        } else {
            return null;
        }
    }

    public static void addUserToContacts(UserDetails userDetails) {
        RealmRepository repository = RealmRepository.getInstance();
        repository.beginTransaction();
        getCurrentUser().getFriends().add(userDetails);
        repository.commitTransaction();
    }

    public static void removeContact(UserDetails userDetails) {
        RealmRepository repository = RealmRepository.getInstance();
        repository.beginTransaction();
        getCurrentUser().getFriends().remove(userDetails);
        repository.commitTransaction();
    }

    public static Message addOrUpdateMessage(MessageResponse messageResponse) {
        RealmRepository repository = RealmRepository.getInstance();
        Message message = new Message(messageResponse);
        repository.beginTransaction();
        message = repository.add(message);
        repository.commitTransaction();
        return message;
    }

    public static void addUserToChat(int memberId, int chatId) {
        RealmRepository repository = RealmRepository.getInstance();
        repository.beginTransaction();
        Chat chat = repository.getById(Chat.class, chatId);
        UserDetails user = repository.getById(UserDetails.class, memberId);
        chat.getChatMembers().add(user);
        repository.commitTransaction();
    }

    public static void removeUserFromChat(int memberId, int chatId){
        RealmRepository repository = RealmRepository.getInstance();
        repository.beginTransaction();
        Chat chat = repository.getById(Chat.class, chatId);
        UserDetails user = repository.getById(UserDetails.class, memberId);
        chat.getChatMembers().remove(user);
        repository.commitTransaction();
    }

    public static void addOrUpdateChat(final ChatResponse response) {
        final RealmRepository repository = RealmRepository.getInstance();
        Chat managedChat = repository.getById(Chat.class, response.getId());
        Chat chat = new Chat(response);
        repository.beginTransaction();
        if (managedChat == null){
            managedChat = repository.add(chat);
            getCurrentUser().getChats().add(managedChat);
        } else {
            if (!getCurrentUser().getChats().contains(managedChat))
            {
                getCurrentUser().getChats().add(managedChat);
            }
            managedChat.setChatName(chat.getChatName());
            managedChat.setTemperature(chat.getTemperature());
        }
        repository.commitTransaction();
    }
}
