package com.example.cameratofirebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_CAPTURE = 101;

    public void startRecording(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordButton =
                (Button) findViewById(R.id.recordButton);

        if (!hasCamera())
            recordButton.setEnabled(false);
    }

    private boolean hasCamera() {
        return (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY));
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        Uri videoUri = data.getData();
        FirebaseStorage storage = FirebaseStorage.getInstance();
// too bad
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                //videoUri should contain the filename, yet it doesn't. Too bad
                Toast.makeText(this, "Video saved to:\n" +
                        videoUri, Toast.LENGTH_LONG).show();
                //The Firebase part : create a reference to the storage of Firebase
                StorageReference storageRef = storage.getReference();
                String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
                //StorageReference video = storageRef.child("videos/"+videoUri.getLastPathSegment());
                StorageReference video = storageRef.child("videos/"+currentDateTimeString);
                UploadTask uploadTask = video.putFile(videoUri);
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Put error message here. Is irrelevant in this specific exercice
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Put success message here. Is irrelevant in this specific exercice
                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
