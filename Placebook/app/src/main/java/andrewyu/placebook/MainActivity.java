package andrewyu.placebook;

import android.app.Activity;
import android.content.ContentResolver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;


public class MainActivity extends Activity {

        private static String logTag = "CameraTest";
        private static final int REQUEST_PLACE_PICKER = 1003;
        private Uri imageUri;
        private Bitmap bitmap;
        private Bitmap bitmap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //told to call this in the the onCreate
        initGoogleApi();

        //camera button
        ImageButton cameraButton = (ImageButton)findViewById(R.id.imageButton);
        cameraButton.setOnClickListener(cameraListener);

        //google places button
        ImageButton pickPlaceButton = (ImageButton)findViewById(R.id.pick_place_button);
        pickPlaceButton.setOnClickListener(pickPlaceListener);

        final EditText editPlace = (EditText)findViewById(R.id.place_enter);
        final EditText editDescription = (EditText)findViewById(R.id.text_Description);
        final TextView history_Place1 = (TextView)findViewById(R.id.history_Place1);
        final TextView history_Place2 = (TextView) findViewById(R.id.history_Place2);
        final TextView history_Description1 = (TextView)findViewById(R.id.history_Description1);
        final TextView history_Description2 = (TextView)findViewById(R.id.history_Description2);
        final ImageView imageView1 = (ImageView)findViewById(R.id.history_Image1);
        final ImageView imageView2 = (ImageView)findViewById(R.id.history_Image2);
        ImageButton saveButton = (ImageButton)findViewById(R.id.saveButton);

        //storing to history button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String historyName = history_Place1.getText().toString();
                history_Place2.setText(historyName);
                String historyDesc = history_Description1.getText().toString();
                history_Description2.setText(historyDesc);
                imageView2.setImageBitmap(bitmap2);
                String placeName = editPlace.getText().toString();
                history_Place1.setText(placeName);
                String placeDesc = editDescription.getText().toString();
                history_Description1.setText(placeDesc);
                imageView1.setImageBitmap(bitmap);
                bitmap2 = bitmap;

            }
        });
    }

    private View.OnClickListener cameraListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //open the camera
            takePhoto(view);
        }
    };

    private View.OnClickListener pickPlaceListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            launchPlacePicker();
        }
    };


     private void takePhoto(View view){
        // when we take the photo we also need to get the location, will need to add another function
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"myPicture");
        //need to figure out a way to not over write the photo worry about that later
        int TAKE_PICTURE =1;
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PICTURE); //1 for rear camera
     }

    private void initGoogleApi () {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                . Builder ( this )
                . addApi(Places.GEO_DATA_API)
                . addApi(Places.PLACE_DETECTION_API)
                . addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                . addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                . build();
    }
    // Call launchPlacePicker () when the Pick -A- Place button is clicked .
    private void launchPlacePicker () {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
// Handle exception - Display a Toast message
        }catch ( GooglePlayServicesNotAvailableException e) {
// Handle exception - Display a Toast message
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK){

            Uri selectedImage = imageUri;
            if(requestCode == REQUEST_PLACE_PICKER && data!=null){
                Place place = PlacePicker.getPlace(data,this);
                //set place nem text view to place .getName()
            }
            getContentResolver().notifyChange(selectedImage, null);// notify other apps, get everything on the same page
            ImageView imageView = (ImageView)findViewById(R.id.image_camera);
            ContentResolver cr = getContentResolver();


            //when working with data, try to a try block to catch errors

            try{
                //try to get bitmap data
                bitmap = MediaStore.Images.Media.getBitmap(cr,selectedImage);
                imageView.setImageBitmap(bitmap);
                //send the app user a message
                Toast.makeText(MainActivity.this,selectedImage.toString(), Toast.LENGTH_LONG).show();

            }catch(Exception e){
                Log.e(logTag, e.toString());
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
