package com.rammstein.messenger.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
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
import com.rammstein.messenger.fragment.dialog.DatePickerDialogFragment;
import com.rammstein.messenger.fragment.dialog.MenuDialog;
import com.rammstein.messenger.fragment.dialog.ProfileDialog;
import com.rammstein.messenger.fragment.dialog.TextInputDialogFragment;
import com.rammstein.messenger.model.Chat;
import com.rammstein.messenger.model.Message;
import com.rammstein.messenger.repository.Repository;
import com.rammstein.messenger.repository.TestChatRepository;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationTabBar.OnTabBarSelectedIndexListener,
        ViewPager.OnPageChangeListener, TextInputDialogFragment.OnClickListener,
        DatePickerDialogFragment.OnClickListener, MenuDialog.OnClickListener{
    public static final String FIRST_NAME_INPUT_DIALOG = "first_name_input_fragment";
    public static final String LAST_NAME_INPUT_DIALOG = "last_name_input_fragment";
    public static final String BIRTHDAY_PICKER_DIALOG = "birthday_picker_fragment";
    public static final String CONTACT_MENU_DIALOG = "contact_menu_fragment";
    public static final String CHAT_MENU_DIALOG = "dialog_menu_fragment";
    public static final String PROFILE_DIALOG = "profile_dialog";
    public static final String IMAGE_SOURCE_PICKER_DIALOG = "image_source_picker";
    private static final int PICK_AVATAR_FROM_GALLERY_REQUEST = 0;
    private static final int PICK_AVATAR_FOM_CAMERA_REQUEST = 1;
    public static final String ACTION_UPDATE_PROFILE_FRAGMENT = "update_profile_fragment";


    @BindView(R.id.vp_horizontal_ntb)  ViewPager mViewPager;
    @BindView(R.id.ntb_horizontal)  NavigationTabBar mNavigationTabBar;
    @BindView(R.id.fab)  FloatingActionButton mFloatingActionButton;
    private MainViewPagerAdapter mVPAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViewPager();
        initNavBar();
        mFloatingActionButton.setOnClickListener(this);
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
                i = new Intent(this, LogInActivity.class);
                i.putExtra(LogInActivity.EXTRA_ANIMATE, false);
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
        Repository<Chat> repository = TestChatRepository.getInstance();
        for(Chat chat : repository.getAll()){
            for (Message message : chat.getMessages()){
                if (!message.isSeen()){
                    count++;
                }
            }
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
                Log.i("FIRST_NAME", "click ok");
                Toast.makeText(this, textOutput, Toast.LENGTH_SHORT).show();
                //((ProfileFragment)mVPAdapter.getItem(0)).getFirstName().setText(textOutput);
                //TODO
                break;
            case LAST_NAME_INPUT_DIALOG:
                Log.i("LAST_NAME", "click ok");
                Toast.makeText(this, textOutput, Toast.LENGTH_SHORT).show();
                //((ProfileFragment)mVPAdapter.getItem(0)).getFirstName().setText(textOutput);
                //TODO
                break;
        }
    }

    @Override
    public void onOkClicked(String tag, GregorianCalendar date) {
        switch (tag){
            case BIRTHDAY_PICKER_DIALOG:
                Log.i("BIRTHDAY", "click ok");
                Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show();
                //TODO validate
                break;
        }
    }

    @Override
    public void onOptionSelected(String tag, int optionId, int itemId) {
        Intent intent;
        switch (tag){
            case CONTACT_MENU_DIALOG:
                switch (optionId){
                    case R.string.show_information:
                        ProfileDialog dialog = ProfileDialog.newInstance(itemId);
                        dialog.show(getSupportFragmentManager(), PROFILE_DIALOG);
                        break;
                    case R.string.delete:

                        break;
                }
                break;
            case CHAT_MENU_DIALOG:
                switch (optionId){
                    case R.string.delete:

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
        String text = itemId + " " + getResources().getString(optionId) + " " +tag;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
            int imageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

            //resizing image here because resizing in CropImage works inconsistently
            Bitmap resizedImage = Bitmap.createScaledBitmap(croppedImage, imageSize, imageSize, false);

            saveImageToServer(resizedImage);
        }

    }

    private void saveImageToServer(Bitmap resizedImage) {
        //TODO
        Log.i("main", "send broadcast");
        Intent i = new Intent();
        i.setAction(ACTION_UPDATE_PROFILE_FRAGMENT);
        i.putExtra("image", resizedImage);
        sendBroadcast(i);
    }
}
