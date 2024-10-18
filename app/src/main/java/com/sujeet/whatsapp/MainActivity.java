package com.sujeet.whatsapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String sendrUID;
    FirebaseAuth auth;
    FirebaseStorage storage;
    DrawerLayout dravelstart;
    NavigationView NavigationView;
    RecyclerView mainUserRecyclerView;
    UserAdpter adapter;
    Toolbar tollbar;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    FirebaseAuth firebaseAuth;

    android.app.ProgressDialog progressDialog;

    private static final int CAMERA_IMAGE_CODE = 1;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        tollbar = findViewById(R.id.tollbar);
        dravelstart = findViewById(R.id.dravelstart);
        tollbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dravelstart.openDrawer(GravityCompat.START);

            }
        });

        getUser();

        ImageView imageView = (ImageView) findViewById(R.id.dravaleimage);
        TextView textView = (TextView) findViewById(R.id.draweltextview);
        LinearLayout layout = (LinearLayout) findViewById(R.id.logout);
        LinearLayout editprofile = (LinearLayout) findViewById(R.id.editprofile);
//        editprofile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        DatabaseReference databaseReference = database.getReference().child("user").child(auth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("userName").getValue().toString();
                String profile = snapshot.child("profilepic").getValue().toString();
                textView.setText(name);
                Picasso.get().load(profile).into(imageView);
                Log.d("ondatachange", "" + name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ondatachange", "" + error.getMessage());


            }
        });


        if (auth.getCurrentUser() == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }
        tollbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.menucamra) {
                    if (checkCameraPermission()) {
                        openCamera();
                    }
                } else if (id == R.id.menusetting) {
                    Intent intent = new Intent(MainActivity.this, DataUpdate.class);
                    startActivity(intent);
                } else if (id == R.id.menunewgroup) {
                    Toast.makeText(MainActivity.this, "Menu Group", Toast.LENGTH_SHORT).show();


                } else if (id == R.id.logout) {

                    Dialog dialog = new Dialog(MainActivity.this, R.style.dialoge);
                    dialog.setContentView(R.layout.dialog_layout);
                    Button no, yes;
                    yes = dialog.findViewById(R.id.yesbnt);
                    no = dialog.findViewById(R.id.nobnt);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                SharedPreferences share = getSharedPreferences("login", MODE_PRIVATE);
                                SharedPreferences.Editor editor = share.edit();
                                editor.putBoolean("flag", false);
                                editor.apply();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                Log.d("datafirebaseinclude", "" + FirebaseAuth.getInstance());
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Log.d("datafirebaseinclude", "Nodelete" + e.getMessage());

                            }
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                } else {
                    Toast.makeText(MainActivity.this, "Linked device", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


    }

//    private void setTextDrawal(TextView textView) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
//        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    Users users = snapshot.getValue(Users.class);
//                    textView.setText(users.getUserName());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    private void setProfileImage(ImageView imageView) {
//        Log.d("setProfileImage", "setProfileImage: UserID: " + userId);
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
//        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    Users users = snapshot.getValue(Users.class);
//                    Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.av).into(imageView);
//                    Log.d("setProfileImage", "onDataChange: " + users.getProfilepic());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("setProfileImage", "onCancelled() called with: error = [" + error.getMessage() + "]");
//            }
//        });
//
//    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            return false;
        } else {
            return true;
        }
    }

    public void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_IMAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_IMAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE_CODE && resultCode == RESULT_OK) {
            Bundle extra = data.getExtras();

            Bitmap imagebitmap = (Bitmap) extra.get("data");
            saveImageToGallery(MainActivity.this,imagebitmap);
        }
    }

    public void saveImageToGallery(Context context, Bitmap imageBitmap) {
        ContentResolver resolver = context.getContentResolver();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".png";

        OutputStream fos;

        try {
            // For Android 10 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = resolver.openOutputStream(imageUri);
            } else {
                // For Android 9 and below
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File imageFile = new File(storageDir, imageFileName);
                fos = new FileOutputStream(imageFile);

                // Notify the media scanner about the new image
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(imageFile);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            }

            // Ensure bitmap is using ARGB_8888 configuration for best quality
            if (imageBitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                imageBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);  // Use PNG for high quality
            if (fos != null) {
                fos.flush();
                fos.close();
            }

            Log.d("ImageSaveGallery", "Image saved at: " + imageFileName);
            Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }


//    public void saveImageToGallery(Context context, Bitmap imageBitmap) {
//        // Prepare the values to insert into MediaStore
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png");
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
//
//        // Insert the image into MediaStore
//        ContentResolver resolver = context.getContentResolver();
//        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        try {
//            if (uri != null) {
//                // Open an output stream to the URI
//                OutputStream outputStream = resolver.openOutputStream(uri);
//                // Compress the Bitmap into the OutputStream
//                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);  // Use PNG format for better quality
//                outputStream.flush();
//                outputStream.close();
//
//                // Notify the media scanner about the new image
//                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
//
//                Log.d("ImageSaveGallery", "Image saved at: " + uri.toString());
//                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
//        }
//    }

   void getUser() {
        usersArrayList = new ArrayList<>();
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();


        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdpter(MainActivity.this, usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        DatabaseReference reference = database.getReference().child("user");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Log.d("ondatachangea","show"+dataSnapshot);

                    progressDialog.dismiss();
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you want ti exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .show();

    }

}