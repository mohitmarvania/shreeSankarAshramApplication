package com.example.invoicemaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;


public class AddUserActivity extends AppCompatActivity {

    TextView recieptNumber;
    EditText name, smarnarthe, address, mobileNo, bhet;
    MaterialButton invoiceBtn;
    MaterialButton selectImgBtn;

    String editedName, editedSmarnarthe, editedAddress, editedMobileNo, editedBhet, dataId;
    String uniqueID = "123";
    long recieptNo = 0;
    boolean isEdited = false;

    //Image variable
    private final int GALLERY_REQUEST_CODE = 1;
    private final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2;
    String imagePathGlobal;
    Uri imageUri;
    String imageNameInFirebase;
    private ImageView imageView;
    private Bitmap bmp;

    Realm realm;
    Data data = new Data();
    Data dataF = new Data();

    FirebaseDatabase database = FirebaseDatabase.getInstance();;
    DatabaseReference myRef = database.getReference("data");
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        name = findViewById(R.id.editName);
        smarnarthe = findViewById(R.id.editSmarnarthe);
        address = findViewById(R.id.editAddress);
        mobileNo = findViewById(R.id.editMobileNumber);
        bhet = findViewById(R.id.editBhet);
        invoiceBtn = findViewById(R.id.generateRecieptBtn);
        imageView = findViewById(R.id.imageView);
        uniqueID = UUID.randomUUID().toString();

        retriveData();

        generateRecieptFunction();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                recieptNo = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    // Image funcitons below.
    public void openImagePicker(View view) {
        // Request the READ_EXTERNAL_STORAGE permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
        } else {
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            bmp = loadBitmapFromUri(imageUri);

            if (bmp != null) {
                imageView.setImageBitmap(bmp);
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToFirebase() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        imageNameInFirebase = fileName;
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(AddUserActivity.this, "Image Uploaded :)", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        imageClass imageClassObj = new imageClass(fileName, uri.toString(), fileName);
                        databaseReference.setValue(imageClassObj);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(AddUserActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setImagePathToSavePdf() {

        System.out.println("HEREEEE !!!");
        System.out.println(imagePathGlobal);
        SavePDF savePDF = new SavePDF();
        savePDF.setImagePathOfImage(imagePathGlobal);

    }


    public void retriveData() {
        editedName = getIntent().getStringExtra("name");
        editedSmarnarthe = getIntent().getStringExtra("smarnarthe");
        dataId = getIntent().getStringExtra("dataId");
        editedAddress = getIntent().getStringExtra("address");
        editedMobileNo = getIntent().getStringExtra("mobileNo");
        editedBhet = getIntent().getStringExtra("bhet");

        if (dataId != null && !dataId.isEmpty()) {
            isEdited = true;
        }

        if (isEdited) {

            invoiceBtn.setText("Save Changes");

            // Disable mobile input keyboard.
            mobileNo.setInputType(0);

            name.setText(editedName);
            smarnarthe.setText(editedSmarnarthe);
            address.setText(editedAddress);
            mobileNo.setText(editedMobileNo);
            bhet.setText(editedBhet);
            isEdited = false;
        }

    }

    public void generateRecieptFunction() {

        invoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (invoiceBtn.getText().toString().equals("Generate Invoice")) {

                    Realm.init(getApplicationContext());
                    realm = Realm.getDefaultInstance();

                    if (name.getText().toString().length() == 0 ||
                            smarnarthe.getText().toString().length() == 0 ||
                            address.getText().toString().length() == 0 ||
                            mobileNo.getText().toString().length() == 0 ||
                            bhet.getText().toString().length() == 0) {

                        Toast.makeText(AddUserActivity.this, "SOME FIELDS ARE EMPTY !!", Toast.LENGTH_SHORT).show();
                    } else if (mobileNo.getText().toString().length() != 10) {
                        Toast.makeText(AddUserActivity.this, "MOBILE NO. INVALID !!", Toast.LENGTH_SHORT).show();
                    } else {

                        String dataName = name.getText().toString();
                        String dataSmarnarthe = smarnarthe.getText().toString();
                        String dataAddress = address.getText().toString();
                        String dataMobileNo = mobileNo.getText().toString();
                        String dataBhet = bhet.getText().toString();

                        // GET CURRENT DATE AND TIME
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String currentDate = sdf.format(new Date());
                        long createdTime = System.currentTimeMillis();

                        dataF.recieptNo = recieptNo + 1;
                        dataF.name = dataName;
                        dataF.smartheName = dataSmarnarthe;
                        dataF.address = dataAddress;
                        dataF.mobileNo = dataMobileNo;
                        dataF.donation = dataBhet;
                        dataF.createdTime = new Date().getTime();

                        myRef.child(String.valueOf(recieptNo + 1)).setValue(dataF);
                        System.out.println(myRef);

                        realm.beginTransaction();
                        data = realm.createObject(Data.class);


                        data.setName(dataName);
                        data.setSmartheName(dataSmarnarthe);
                        data.setAddress(dataAddress);
                        data.setMobileNo(dataMobileNo);
                        data.setDonation(dataBhet);
                        data.setCurrentDate(currentDate);
                        data.setCreatedTime(createdTime);
                        Bitmap selectedImage = bmp;

                        realm.commitTransaction();
                        realm.close();

                        Toast.makeText(AddUserActivity.this, "DATA SAVED :)", Toast.LENGTH_SHORT).show();

                        createPDF(selectedImage, dataName, dataSmarnarthe, dataAddress, dataMobileNo, dataBhet, currentDate);

                        Intent intent = new Intent(AddUserActivity.this, viewPdf.class);
                        intent.putExtra("mobilePath", dataMobileNo);
                        startActivity(intent);

//                        finish();
                    }
                } else {

                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    Data data = realm.where(Data.class).equalTo("mobileNo", mobileNo.getText().toString()).findFirst();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = sdf.format(new Date());
                    long createdTime = System.currentTimeMillis();

                    data.setName(name.getText().toString());
                    data.setSmartheName(smarnarthe.getText().toString());
                    data.setAddress(address.getText().toString());
                    data.setMobileNo(mobileNo.getText().toString());
                    data.setDonation(bhet.getText().toString());
                    data.setCurrentDate(currentDate);
                    data.setCreatedTime(createdTime);

                    realm.commitTransaction();

                    dataF.recieptNo = recieptNo + 1;
                    dataF.name = name.getText().toString();
                    dataF.smartheName = smarnarthe.getText().toString();
                    dataF.address = address.getText().toString();
                    dataF.mobileNo = mobileNo.getText().toString();
                    dataF.donation = bhet.getText().toString();
                    dataF.createdTime = new Date().getTime();

                    myRef.child(String.valueOf(recieptNo + 1)).setValue(dataF);
                    System.out.println(myRef);

                    Toast.makeText(AddUserActivity.this, "DATA UPDATED :)", Toast.LENGTH_SHORT).show();

                    String dataName = name.getText().toString();
                    String dataSmarnarthe = smarnarthe.getText().toString();
                    String dataAddress = address.getText().toString();
                    String dataMobileNo = mobileNo.getText().toString();
                    String dataBhet = bhet.getText().toString();
                    Bitmap selectedImage = bmp;
                    createPDF(selectedImage, dataName, dataSmarnarthe, dataAddress, dataMobileNo, dataBhet, currentDate);

                    Intent intent = new Intent(AddUserActivity.this, viewPdf.class);
                    intent.putExtra("mobilePath", dataMobileNo);
                    startActivity(intent);

                }

            }
        });

    }

    public void createPDF(Bitmap selectedImage, String name, String smarnarthe, String address, String mobileNo, String bhet, String dateCreated) {

        SavePDF saveClass = new SavePDF();
        saveClass.savePdfToStorage(selectedImage, name, smarnarthe, address, mobileNo, bhet, dateCreated);
    }


}