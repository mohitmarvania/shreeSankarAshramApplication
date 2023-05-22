package com.example.invoicemaker;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SaveToFirebase {

    StorageReference storageReference;
    DatabaseReference databaseReference;
    Context context;

    public SaveToFirebase(Context context) {
        this.context=context;
    }

    public void saveToStorage(String fileName, String filePath) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pathToStorage = storage.getReference().child("Uploads/");
        pathToStorage.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference fileRef : listResult.getItems()) {
                // Check if the same file exists in the storage or not.
                if (fileRef.getName().equals(fileName)) {

                    // Deleting existing file and uploading new file.
                    storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference existingFileRef = storageReference.child("Uploads/" + fileName);
                    existingFileRef.delete().addOnSuccessListener(aVoid -> {

                        // Upload new file.
                        storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference pdfFileRef = storageReference.child("Uploads/" + fileName);
                        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");

                        String path = getDownloadsFilePath(fileName);
                        File file = new File(path);

                        pdfFileRef.putFile(Uri.fromFile(file))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isComplete());
                                        Uri uri = uriTask.getResult();

                                        pdfClass pdfClassObj = new pdfClass(fileName, uri.toString());
                                        setToRealtimeDatabase("PDF", pdfClassObj);
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot snapshot) {

                            }
                        });

                    }).addOnFailureListener(exception -> {

                    });


                } else {

                    // upload the file as there is no similar file
                    storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference pdfFileRef = storageReference.child("Uploads/" + fileName);
                    databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");

                    String path = getDownloadsFilePath(fileName);
                    File file = new File(path);

                    pdfFileRef.putFile(Uri.fromFile(file))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isComplete());
                                    Uri uri = uriTask.getResult();

                                    pdfClass pdfClassObj = new pdfClass(fileName, uri.toString());
                                    setToRealtimeDatabase("PDF", pdfClassObj);
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot snapshot) {

                        }
                    });

                }

            }

            Toast.makeText(context.getApplicationContext(), "File Uploaded!!!", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(exception -> {

        });



    }

    public void setToRealtimeDatabase(String key, pdfClass pdfClassObj) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        databaseReference.orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    databaseReference.child(key).removeValue()
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    databaseReference.child(key).setValue(pdfClassObj);
                                    System.out.println("SUCCESS");
                                } else  {
                                    System.out.println("FAILED");
                                }

                            });

                } else {

                    databaseReference.child(key).setValue(pdfClassObj);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    // FUNCTION TO FETCH THE PATH
    public String getDownloadsFilePath(String fileName) {
        String filePath = null;
        if (isExternalStorageAvailable()) {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloadsDir != null) {
                File file = new File(downloadsDir, fileName);
                filePath = file.getAbsolutePath();
            }
        }
        return filePath;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}
