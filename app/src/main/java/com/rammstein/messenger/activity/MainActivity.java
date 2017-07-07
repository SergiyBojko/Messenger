package com.rammstein.messenger.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.adapter.MainViewPagerAdapter;
import com.rammstein.messenger.fragment.dialog.ConfirmationDialog;
import com.rammstein.messenger.fragment.dialog.ConfirmationWithOptionsDialog;
import com.rammstein.messenger.fragment.dialog.ConnectionDialog;
import com.rammstein.messenger.fragment.dialog.DatePickerDialogFragment;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.fragment.dialog.NotificationDialog;
import com.rammstein.messenger.fragment.dialog.ProfileDialog;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.Message;
import com.rammstein.messenger.model.local.UserDetails;
import com.rammstein.messenger.model.web.response.UserDetailsResponse;
import com.rammstein.messenger.repository.RealmRepository;
import com.rammstein.messenger.repository.SharedPreferencesRepository;
import com.rammstein.messenger.service.SignalRService;
import com.rammstein.messenger.util.InternetHelper;
import com.rammstein.messenger.util.RealmHelper;
import com.rammstein.messenger.util.SimpleDateUtils;
import com.rammstein.messenger.web.retrofit.RetrofitHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rammstein.messenger.model.local.Message.TIME_IN_MILLS;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationTabBar.OnTabBarSelectedIndexListener,
        ViewPager.OnPageChangeListener, TextInputDialogFragment.OnClickListener,
        DatePickerDialogFragment.OnClickListener, MenuDialog.OnClickListener,
        ConfirmationDialog.OnClickListener, ConfirmationWithOptionsDialog.OnClickListener{
    public static final String FIRST_NAME_INPUT_DIALOG = "first_name_input_fragment";
    public static final String LAST_NAME_INPUT_DIALOG = "last_name_input_fragment";
    public static final String BIRTHDAY_PICKER_DIALOG = "birthday_picker_fragment";
    public static final String CONTACT_MENU_DIALOG = "contact_menu_fragment";
    public static final String CHAT_MENU_DIALOG = "dialog_menu_fragment";
    public static final String PROFILE_DIALOG = "profile_dialog";
    public static final String IMAGE_SOURCE_PICKER_DIALOG = "image_source_picker";
    private static final int PICK_AVATAR_FROM_GALLERY_REQUEST = 0;
    private static final int PICK_AVATAR_FOM_CAMERA_REQUEST = 1;
    public static final String ACTION_UPDATE_VIEW = "update_profile_fragment";
    private static final String LEAVE_CHAT_CONFIRMATION_DIALOG = "leave_chat_confirmation_dialog";
    private static final String DELETE_HISTORY_CONFIRMATION_DIALOG = "delete_chat_history_confirmation_dialog";
    public static final String STOP_SIGNALR_SERVICE = "stop_signalr_service";


    @BindView(R.id.vp_horizontal_ntb)  ViewPager mViewPager;
    @BindView(R.id.ntb_horizontal)  NavigationTabBar mNavigationTabBar;
    @BindView(R.id.fab)  FloatingActionButton mFloatingActionButton;
    private MainViewPagerAdapter mVPAdapter;
    private AppUser mAppUser;
    private RealmRepository mRealmRepository;
    private Chat mChat;
    private RetrofitHelper mRetrofitHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRetrofitHelper = new RetrofitHelper(this);
        mRealmRepository = RealmRepository.getInstance();
        mAppUser = mRealmRepository.getById(AppUser.class, getCurrentUserId());
        InternetHelper.checkInternetConnection(MainActivity.this);
        initViewPager();
        initNavBar();
        mFloatingActionButton.setOnClickListener(this);
    }


    private int getCurrentUserId() {
        return SharedPreferencesRepository.getInstance().getCurrentUserId();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Main", "pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Main", "stop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i;
        switch (id){
            case R.id.log_out:
                finish();
                SharedPreferencesRepository.getInstance().setCurrentUserId(-1);
                i = new Intent(this, LogInActivity.class);
                i.putExtra(LogInActivity.EXTRA_ANIMATE, false);
                sendBroadcast(new Intent(STOP_SIGNALR_SERVICE));
                startActivity(i);
                break;
            case R.id.search:
                i = new Intent(this, SearchActivity.class);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initViewPager() {
        mVPAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mVPAdapter);
    }

    private void initNavBar() {
        ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_person_black_24dp),
                        getResources().getColor(R.color.colorPrimary)
                ).title("Profile")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_import_contacts_black_24dp),
                        getResources().getColor(R.color.colorPrimary)
                ).title("Contacts")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_history_black_24dp),
                        getResources().getColor(R.color.colorPrimary)
                ).title("Recent")
                        .build()
        );

        mNavigationTabBar.setModels(models);
        mNavigationTabBar.setOnPageChangeListener(this);
        mNavigationTabBar.setViewPager(mViewPager, 1);

        int newMessages = getNewMessageCount();
        if (newMessages > 0){
            NavigationTabBar.Model recent = mNavigationTabBar.getModels().get(2);
            recent.showBadge();
            recent.setBadgeTitle(Integer.toString(newMessages));
        }
    }

    private int getNewMessageCount() {
        int count = 0;
        RealmList<Chat> chats = mAppUser.getChats();
        for(Chat chat : chats){
            RealmResults<Message> newMessages = chat.getMessages().where().greaterThan(TIME_IN_MILLS, chat.getLastSeenMessageTime()).findAll();
            count += newMessages.size();
        }
        return count;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                Intent i = new Intent(this, AddChatMembersActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position){
            case 0:
            case 1:{
                mFloatingActionButton.hide();
                break;
            }
            case 2:{
                mFloatingActionButton.show();
                mNavigationTabBar.getModels().get(position).hideBadge();
                break;
            }
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onStartTabSelected(NavigationTabBar.Model model, int index) {
        Log.i("onStartTabSelected", " " + index+"");
    }

    @Override
    public void onEndTabSelected(NavigationTabBar.Model model, int index) {
        Log.i("onEndTabSelected", " " + index+"");
    }

    @Override
    public void onOkClicked(String tag, String textOutput) {
        switch (tag){
            case FIRST_NAME_INPUT_DIALOG:
                mRealmRepository.beginTransaction();
                mAppUser.getUserDetails().setFirstName(textOutput);
                mRealmRepository.commitTransaction();
                Log.i("FIRST_NAME", "click ok");
                //TODO sync with backend
                break;
            case LAST_NAME_INPUT_DIALOG:
                mRealmRepository.beginTransaction();
                mAppUser.getUserDetails().setLastName(textOutput);
                mRealmRepository.commitTransaction();
                Log.i("LAST_NAME", "click ok");
                //TODO sync with backend
                break;
        }
    }

    @Override
    public void onOkClicked(String tag, GregorianCalendar date) {
        switch (tag){
            case BIRTHDAY_PICKER_DIALOG:
                GregorianCalendar now = new GregorianCalendar();
                if (date.before(now)){
                    mRealmRepository.beginTransaction();
                    Log.i("MainActivity", mRealmRepository.getRealm().isClosed()+"");
                    mAppUser.getUserDetails().setBirthday(date.getTime());
                    mRealmRepository.commitTransaction();
                } else {
                    Toast.makeText(this, "Date invalid", Toast.LENGTH_LONG).show();
                }
                Log.i("BIRTHDAY", "click ok");
                //TODO //TODO sync with backend
                break;
        }
    }

    @Override
    public void onOptionSelected(String tag, int optionId, int itemId, int itemIndex) {
        RetrofitHelper retrofitHelper = new RetrofitHelper(this);
        Intent intent;
        switch (tag){
            case CONTACT_MENU_DIALOG:
                UserDetails user = RealmRepository.getInstance().getById(UserDetails.class, itemId);
                switch (optionId){
                    case R.string.show_information:
                        ProfileDialog dialog = ProfileDialog.newInstance(itemId);
                        dialog.show(getSupportFragmentManager(), PROFILE_DIALOG);
                        break;
                    case R.string.delete_contact:
                        retrofitHelper.removeFromContacts(this, user.getId());
                        break;
                }
                break;
            case CHAT_MENU_DIALOG:
                mChat = mRealmRepository.getById(Chat.class, itemId);
                RealmList<Message> messages;
                DialogFragment dialog;
                switch (optionId){
                    case R.string.delete_chat_history:
                        dialog = ConfirmationDialog.newInstance(getResources().getString(R.string.confirm_delete_history));
                        dialog.show(getSupportFragmentManager(), DELETE_HISTORY_CONFIRMATION_DIALOG);
                        break;
                    case R.string.leave_chat:
                        int[] options = {R.string.delete_chat_history};
                        dialog = ConfirmationWithOptionsDialog.newInstance(getResources().getString(R.string.confirm_leave_chat), options);
                        dialog.show(getSupportFragmentManager(), LEAVE_CHAT_CONFIRMATION_DIALOG);
                        break;
                    case R.string.mark_as_read:
                        if (mChat.getMessages().size() > 0){
                            long lastMessageTime = mChat.getMessages().where().max(TIME_IN_MILLS).longValue();
                            mRealmRepository.beginTransaction();
                            mChat.setLastSeenMessageTime(lastMessageTime);
                            mRealmRepository.commitTransaction();
                        }
                        break;
                }
                break;
            case IMAGE_SOURCE_PICKER_DIALOG:
                switch (optionId){
                    case R.string.gallery:
                        intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_AVATAR_FROM_GALLERY_REQUEST);
                        break;
                    case R.string.camera:
                        File tempPhotoPath = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "temp_photo.jpg");
                        Uri tempPhotoUri = Uri.fromFile(tempPhotoPath);

                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri);
                        startActivityForResult(intent, PICK_AVATAR_FOM_CAMERA_REQUEST);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PICK_AVATAR_FROM_GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    .setActivityTitle(getString(R.string.image_crop_activity_title))
                    .setFixAspectRatio(true)
                    .start(this);
        }

        if (requestCode == PICK_AVATAR_FOM_CAMERA_REQUEST && resultCode == RESULT_OK){
            File tempPhotoPath = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "temp_photo.jpg");
            Uri imageUri = Uri.fromFile(tempPhotoPath);

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    .setActivityTitle(getString(R.string.image_crop_activity_title))
                    .setFixAspectRatio(true)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri imageUri = result.getUri();
            Log.i("uri", imageUri.toString());
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap croppedImage = BitmapFactory.decodeStream(imageStream);
            int imageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            Bitmap resizedImage = Bitmap.createScaledBitmap(croppedImage, imageSize, imageSize, false);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            saveImageToServer(byteArray);
        }

    }

    private void saveImageToServer(byte[] image) {
        mRetrofitHelper.uploadImage(image, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 201){
                    mRetrofitHelper.addOrUpdateUser(MainActivity.this, mAppUser.getId());
                } else {
                    try {
                        NotificationDialog dialog = NotificationDialog.newInstance(getString(R.string.error), response.errorBody().string());
                        dialog.show(getSupportFragmentManager(), "error");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                NotificationDialog dialog = NotificationDialog.newInstance(getString(R.string.error), t.getLocalizedMessage());
                dialog.show(getSupportFragmentManager(), "error");
            }
        });
    }

    @Override
    public void onConfirm(String tag) {
        switch (tag){
            case DELETE_HISTORY_CONFIRMATION_DIALOG:
                mRetrofitHelper.deleteChatHistory(this, mChat.getId());
                break;
        }
    }

    @Override
    public void onConfirm(String tag, Bundle args) {
        AppUser appUser = RealmHelper.getCurrentUser();
        switch (tag){
            case LEAVE_CHAT_CONFIRMATION_DIALOG:
                mRetrofitHelper.removeUserFromChat(this, mChat.getId(), appUser.getId());
                boolean deleteChat = args.getBoolean(Integer.toString(R.string.delete_chat_history));
                if (deleteChat){
                    mRetrofitHelper.deleteChatHistory(this, mChat.getId());
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
