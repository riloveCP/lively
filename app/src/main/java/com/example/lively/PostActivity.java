package com.example.lively;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.provider.MediaStore;
import android.provider.Settings;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl = null;
    StorageTask uploadTask;
    StorageReference storageReference;
    ImageView close, imageAdded;
    TextView post;
    EditText description;
    private ActivityResultLauncher<String> mImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        progressBar = findViewById(R.id.progressBar);

        mImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                o -> imageAdded.setImageURI(o)
        );

        close = findViewById(R.id.close);
        imageAdded = findViewById(R.id.imageAdded);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(v -> {
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        });

        imageAdded.setOnClickListener(v -> openFileChooser());


        post.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openFileChooser() {
        mImage.launch("image/*");
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageAdded.setImageURI(imageUri);
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        progressBar.setTag("Posting");

        StorageReference fileReference = storageReference.child(UUID.randomUUID().toString() + "." + getFileExtension(imageUri));
        uploadTask = fileReference.putFile(imageUri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            return fileReference.getDownloadUrl();
        }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                myUrl = downloadUri.toString();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                String postId = reference.push().getKey();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("postid", postId);
                hashMap.put("postimage", myUrl);
                hashMap.put("description", description.getText().toString());
                hashMap.put("publisher", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                assert postId != null;
                reference.child(postId).setValue(hashMap);

                progressBar.clearAnimation();

                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
