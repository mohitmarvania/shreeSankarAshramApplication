package com.example.invoicemaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class viewPdf extends AppCompatActivity {

    PDFView pdfView;
    TextView pdfName;
    MaterialButton shareBtn;
    String mobileNoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = findViewById(R.id.viewPdf);
        pdfName = findViewById(R.id.pdfName);
        shareBtn = findViewById(R.id.sharePdfBtn);
        mobileNoPath = getIntent().getStringExtra("mobilePath");

        // POLICY
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        // VIEW PDF
        String recieptName = "reciept(" + mobileNoPath + ").pdf";
        pdfName.setText(recieptName);
        String path = getDownloadsFilePath(recieptName);

        Context context = getApplicationContext();
        savePdfToFirebase(context, recieptName, path);

        File file = new File(path);

        pdfView.fromFile(file)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .scrollHandle(null)
                .password(null)
                .load();


        // SHARE PDF
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSharePdf();

            }
        });

    }


    public void onSharePdf() {

        String recieptName = "reciept(" + mobileNoPath + ").pdf";
        String path = getDownloadsFilePath(recieptName);

        File file = new File(path);

        if (file.exists()) {

            Uri pdfUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share PDF"));

        } else {
            Toast.makeText(viewPdf.this, "Error in sharing!!", Toast.LENGTH_SHORT).show();
        }

    }

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

    public void savePdfToFirebase(Context context, String fileName, String filePath) {

        SaveToFirebase save = new SaveToFirebase(context);
        save.saveToStorage(fileName, filePath);

    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(viewPdf.this, MainActivity.class);
//        startActivity(intent);
//    }
}