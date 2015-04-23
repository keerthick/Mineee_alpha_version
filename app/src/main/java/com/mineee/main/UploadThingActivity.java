package com.mineee.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.mineee.util.MultiSelectSpinner;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadThingActivity extends ActionBarActivity implements View.OnClickListener{

    ProgressDialog prgDialog;
    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    Button upload;
    boolean isImageSelected = false;
    EditText prodName;
    EditText descr;
    MultiSelectSpinner cat;
    Spinner option;
    String URL_ADD= "http://mineee.com/api/upload.php";
    public static final int RESULT_LOAD_IMAGE = 101;
    public static final String TAG = UploadThingActivity.class.getSimpleName();


    private static Map<String,String> optionMap;
    static {
        optionMap = new HashMap<>();
        optionMap.put("I Own this product.", "1");
        optionMap.put("I Had(preown) this product.", "4");
        optionMap.put("I Want this product.", "3");
        optionMap.put("I Recommend this product.", "5");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String categories[] = {"Fashion & Accessories",
                "Beauty & Health",
                "Electronics",
                "Stationary",
                "Food",
                "Home & Kitchen",
                "Sports & Fitness",
                "Cigars & Drinks",
                "Vehicles",
                "Pets",
                "Collections",
                "Books & Media",
                "Movies & Music",
                "Others"
        };

        String optionValues[] = {
                "I Own this product." ,
                "I Had(preown) this product.",
                "I Want this product.",
                "I Recommend this product."
        };

        Map<String,String> optionMap = new HashMap<>();
        optionMap.put("I Own this product.","1");
        optionMap.put("I Had(preown) this product.","4");
        optionMap.put("I Want this product.","3");
        optionMap.put("I Recommend this product.","5");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_upload_thing);



        Intent i = getIntent();
        imgPath = i.getStringExtra("imgPath");

        if(imgPath != null && !"".equals(imgPath)) {
            ImageView imgView = (ImageView) findViewById(R.id.imageView);
            // Set the Image in ImageView
            imgView.setImageBitmap(BitmapFactory
                    .decodeFile(imgPath));
        }

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        //Button browse = (Button)findViewById(R.id.browse);
        upload = (Button)findViewById(R.id.send);
        upload.setActivated(false);
//        browse.setOnClickListener(this);
        upload.setOnClickListener(this);
        prodName = (EditText)findViewById(R.id.name);
        descr = (EditText)findViewById(R.id.desc);

        cat = (MultiSelectSpinner)findViewById(R.id.catSpin);
        option = (Spinner) findViewById(R.id.optionSpinner);

        cat.setPrompt("None Selected");
        cat.setItems(categories);

        Log.d(TAG,(String)cat.getPrompt());


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, optionValues);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        option.setAdapter(dataAdapter);

        option.setPrompt("dfsd");
        //encodeImagetoString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {


            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgPath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imgView = (ImageView) findViewById(R.id.imageView);
            // Set the Image in ImageView
            imgView.setImageBitmap(BitmapFactory
                    .decodeFile(imgPath));
            // Get the Image's file name
            String fileNameSegments[] = imgPath.split("/");
            fileName = fileNameSegments[fileNameSegments.length - 1];
            // Put file name in Async Http Post Param which will used in Php web app
            params.put("filename", fileName);

            //prgDialog.setMessage(fileName);

            isImageSelected = true;

            if(upload != null){
                upload.setActivated(true);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_thing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                //prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);

                Toast.makeText(getApplicationContext(),imgPath,Toast.LENGTH_LONG);
                Log.d("ASYNC",imgPath);
                try {
                   params.put("file",new File(imgPath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // Trigger Image upload
                //prgDialog.setMessage(encodedString);
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    // http://192.168.2.4:9000/imgupload/upload_image.php
    // http://192.168.2.4:9999/ImageUploadWebApp/uploadimg.jsp
    // Make Http call to upload Image to Php server
    public void makeHTTPCall() {
        prgDialog.setMessage("Invoking Php");
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        
        client.post(URL_ADD,
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        /*Toast.makeText(getApplicationContext(), "",
                                Toast.LENGTH_LONG).show();*/

                        Intent returnIntent = new Intent();
                        //returnIntent.putExtra("result",result);
                        setResult(RESULT_OK,returnIntent);
                        finish();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*case R.id.browse:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;*/

            case R.id.send:
                if(isValidRequest()) {
                    Log.d("REQUEST", prodName.getText().toString());
                    Log.d("REQUEST", descr.getText().toString());

                    String val = null;
                    for (String iter : cat.getSelectedStrings()) {
                        if (val == null)
                            val = iter;
                        else
                            val = val + "," + iter;
                    }

                    Log.d("REQUEST", cat.getSelectedStrings().toString());
                    Log.d("REQUEST", val);


                    Log.d("REQUEST", (String) option.getSelectedItem());
                    Log.d("REQUEST", optionMap.get(option.getSelectedItem()));
                    prgDialog.setMessage("Uploading");
                    prgDialog.show();

                    params.put("Name", prodName.getText().toString());
                    params.put("Descr", descr.getText().toString());
                    params.put("Category", val);
                    params.put("userid", "311");
                    params.put("Option", optionMap.get(option.getSelectedItem()));
                    encodeImagetoString();
                }
                break;

            /*case R.id.threeButton:
                // do your code
                break;*/

            default:
                if(upload != null){
                    upload.setActivated(true);
                }
                break;
        }
    }

    private boolean isNull(Object obj){
        if(obj instanceof EditText){
            EditText eObj = (EditText) obj;
            if (eObj != null){
                String val = eObj.getText().toString();
                if(val != null && "".equals(val.trim()))
                    return true;
            }
        }
        if(obj instanceof Spinner){
            Spinner eObj = (Spinner) obj;
            if (eObj != null){
                String val = (String)eObj.getSelectedItem();
                if(val != null && "".equals(val.trim()))
                    return true;
            }
        }
        if(obj instanceof MultiSelectSpinner){
            MultiSelectSpinner eObj = (MultiSelectSpinner) obj;
            if (eObj != null){
                List val = eObj.getSelectedStrings();
                if(val != null && val.isEmpty())
                    return true;
            }
        }

        return false;
    }

    private boolean isValidRequest(){

        if(isNull(prodName)){
            prodName.setFocusableInTouchMode(true);
            prodName.setFocusable(true);
            prodName.setError("Product Name is required");
            return false;
        }
        if (isNull(cat)){
            cat.setFocusableInTouchMode(true);
            cat.setFocusable(true);
            View selectedView = cat.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setError("Category is required field");

                Log.d(TAG,"Category is required field");
            }
            return false;
        }
        if (isNull(option)){
            option.setFocusableInTouchMode(true);
            option.setFocusable(true);

            return false;
        }

        return true;
    }
}
