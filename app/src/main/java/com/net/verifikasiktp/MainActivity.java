package com.net.verifikasiktp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;

    // Bagian Toolbar
    ImageView iv_back;
    TextView nameToolbar;

    CardView cd_ktp, cd_selfie;

    View view1, view2;
    ImageView iv_ktp, iv_selfie;
    TextView btn_proses;
    String currentPhotoPath;

    private static final String KEY_IMAGE1 = "image1";
    private static final String KEY_IMAGE2 = "image2";
    private int REQUEST_CAMERA1 = 11;
    private int REQUEST_CAMERA2 = 22;
    private Bitmap bitmap1, bitmap2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameToolbar = findViewById(R.id.tv_toolbar);
        iv_back = findViewById(R.id.iv_back_toolbar);
        cd_ktp = findViewById(R.id.cd_ktp);
        cd_selfie = findViewById(R.id.cd_selfie);
        btn_proses = findViewById(R.id.btn_proses);
        iv_ktp = findViewById(R.id.iv_ktp);
        iv_selfie = findViewById(R.id.iv_selfie);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);

        // Set Toolbar Button Back
        iv_back.setVisibility(View.VISIBLE);
        nameToolbar.setText("Verifikasi KTP");

        // set on click item
        iv_back.setOnClickListener(this);
        btn_proses.setOnClickListener(this);
        cd_ktp.setOnClickListener(this);
        cd_selfie.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back_toolbar) { // Button Back
            Toast.makeText(this, "Kembali ke Profil Fragment", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.cd_ktp) {
            SelectImage(1);
        } else if (view.getId() == R.id.cd_selfie) {
            SelectImage(2);
        } else if (view.getId() == R.id.btn_proses) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final String image1 = getStringImage(bitmap1);
        final String image2 = getStringImage(bitmap2);
        System.out.println("Cek gambar yang diupload");
        System.out.println("Image 1 -> " + image1);
        System.out.println("Image 2 -> " + image2);

        if (image1.equals("Image : 0") || image2.equals("Image : 0")) {
            Toast.makeText(this, "Gambar Kosong", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Upload Selesai", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Main2Activity.class));
        }


        /* KODINGAN CONTOH UNTUK UPDATE DATA SI USER DI KTP (VERIFIKASI KTP)
        @SuppressLint("StaticFieldLeak")
        class UploadImage extends AsyncTask<Void, Void, String> {
            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Uploadc2Activity.this, "Mohon Menunggu...", "Uploading", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(Uploadc2Activity.this, s, Toast.LENGTH_LONG).show();
                startActivity(new Intent(Uploadc2Activity.this, BerandaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler rh = new RequestHandler();
                HashMap<String, String> param = new HashMap<>();
                param.put(KEY_SPSID, nosps);
                param.put(KEY_IMAGE1, image1);
                param.put(KEY_IMAGE2, image2);

                return rh.sendPostRequest(URL_UPLOAD, param);
            }
        }
        UploadImage u = new UploadImage();
        u.execute();*/

    }

    private void SelectImage(final int xx) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (xx == 1) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context,
                            "com.example.android.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_CAMERA1);

                }
            } else {
                /*startActivityForResult(intent, REQUEST_CAMERA2);*/
                File photoFile1 = null;
                try {
                    photoFile1 = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile1 != null) {
                    Uri photoURI1 = FileProvider.getUriForFile(context,
                            "com.example.android.fileprovider",
                            photoFile1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI1);
                    startActivityForResult(intent, REQUEST_CAMERA2);
                }
            }
        } else {
            Toast.makeText(context, "Intent Null", Toast.LENGTH_SHORT).show();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String getStringImage(Bitmap bmp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return "Image : 0";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA1 && resultCode == RESULT_OK) {
           /* setPic(1);*/

            view1.setVisibility(View.GONE);
            iv_ktp.setVisibility(View.VISIBLE);

            bitmap1 = BitmapFactory.decodeFile(currentPhotoPath);
            iv_ktp.setImageBitmap(bitmap1);
            galleryAddPic();
        }
        if (requestCode == REQUEST_CAMERA2 && resultCode == RESULT_OK) {
        /*    setPic(2);*/

            view2.setVisibility(View.GONE);
            iv_selfie.setVisibility(View.VISIBLE);

            bitmap2 = BitmapFactory.decodeFile(currentPhotoPath);
            iv_selfie.setImageBitmap(bitmap2);
            galleryAddPic();
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic(int x) {
        // Get the dimensions of the View
        if (x == 1) { // untuk ktp
            int targetW = iv_ktp.getWidth();
            int targetH = iv_ktp.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            bitmap1 = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            iv_ktp.setImageBitmap(bitmap1);
        } else { // untuk selfie
            int targetW = iv_selfie.getWidth();
            int targetH = iv_selfie.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            bitmap2 = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            iv_selfie.setImageBitmap(bitmap2);
        }

    }

}
