package com.example.invoicemaker;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SavePDF {

    String nameText, smarnartheText, addressText, mobileNoText, bhetText, dateText;
    String inWords;
    String tithiDateSelected;
    Bitmap bmp, scaledbmp, bmp1, scaledbmp1;
    int currentRecieptNo;
    int count = 1;
    public int receiptCount;

    String imagePathOfImage;

    public SavePDF() {
    }

    public String getImagePathOfImage() {
        return imagePathOfImage;
    }

    public void setImagePathOfImage(String imagePathOfImage) {
        this.imagePathOfImage = imagePathOfImage;
    }

    public void fetchReceiptCount() {

        // CODE TO FETCH THE CURRENT VALUE OF RECEIPT COUNTER FROM DATABASE.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference receiptCountRef = database.getReference("Receipt Count");
        receiptCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    receiptCount = snapshot.getValue(Integer.class);
                    System.out.println("count : " + receiptCount);
                }
                else {
                    System.out.println("NO SNAPSHOT HERE!!!!!!!!");
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error fetching receipt count", error.toException());
            }
        });


    }

    Context context=getApplicationContext();

    public void savePdfToStorage(Bitmap userImage, String name, String smarnarthe, String address, String mobileNo, String bhet,
                                 String createdDate, String tithiDate) {

        nameText = name;
        smarnartheText = smarnarthe;
        addressText = address;
        mobileNoText = mobileNo;
        bhetText = bhet;
        dateText = createdDate;
        tithiDateSelected = tithiDate;

        fetchReceiptCount();

        System.out.println("COUNT here : " + receiptCount);


        Realm realm = Realm.getDefaultInstance();
        RealmResults<Data> dataList = realm.where(Data.class).findAll().sort("createdTime", Sort.DESCENDING);


        if (dataList.size() == 0) {
            currentRecieptNo = 1;
        } else {
            currentRecieptNo = dataList.size() + 1;
        }

        // RETRIVE BHET AMOUNT AND PASS IT TO CONVERT IN WORDS
        String donation = bhetText;
        inWords = NumbersToWords.convertToIndianCurrency(donation);

        // Getting application context.
        if(userImage==null){
            System.out.println("NULL");
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.img);
        }else{
            System.out.println("Not Null");
            bmp = userImage;
        }
        scaledbmp = Bitmap.createScaledBitmap(bmp, 530, 708, false);

        bmp1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.writeimg);
        scaledbmp1 = Bitmap.createScaledBitmap(bmp1, 790, 780, false);

        PdfDocument myPdfDocument = new PdfDocument();

        Paint myPaint = new Paint();
        Paint myPaint1 = new Paint();
        Paint rsWordsPaint = new Paint();
        Paint shriSankarPaint = new Paint();
        Paint recieptNoPaint = new Paint();
        Paint sthapanaPaint = new Paint();
        Paint vishvaVikhiyatPaint = new Paint();
        Paint shriShankarMaharajPaint = new Paint();
        Paint datePaint = new Paint();
        Paint bhavdiyaPaint = new Paint();
        Paint smarnanthePaint = new Paint();
        Paint sarnamuPaint = new Paint();
        Paint mobileNoPaint = new Paint();
        Paint bhetDonatedPaint = new Paint();
        Paint svikarnarPaint = new Paint();
        Paint abharPaint = new Paint();
        Paint tithiTarikhPaint = new Paint();

        // Blue paint texts.
        Paint namePaint = new Paint();
        Paint snamePaint = new Paint();
        Paint tarikhPaint = new Paint();
        Paint addressPaint = new Paint();
        Paint numberPaint = new Paint();
        Paint bhetPaint = new Paint();



        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1320, 708, 1).create();
        PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage1.getCanvas();


        canvas.drawBitmap(scaledbmp, 0,0,myPaint);
        canvas.drawBitmap(scaledbmp1, 530, 0, myPaint1);

        // SHRI SANKAR MAHARAJ
        shriSankarPaint.setTextAlign(Paint.Align.LEFT);
        shriSankarPaint.setTextSize(25f);
        shriSankarPaint.setColor(Color.rgb(255, 165, 0));
        canvas.drawText("શ્રી શંકરમહારાજ જનસેવા ટ્રસ્ટ, રાજકોટ", 540, 50, shriSankarPaint);

        // RECIEPT NUMBER
        recieptNoPaint.setColor(Color.rgb(0, 113, 188));
        recieptNoPaint.setTextSize(25f);
        recieptNoPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("રજી. નં. E - 7715", 1320 - 50, 50, recieptNoPaint);

        // STHAPANA
        sthapanaPaint.setColor(Color.rgb(0, 71, 171));
        sthapanaPaint.setTextSize(25);
        sthapanaPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("સ્થાપના : પરમ તપોનિધી ભ્રમહલિન સંત પુ. શ્રી શંકરમહારાજ સુદામાપુરીવાળા ૧૯૯૮", 1320-380, 80, sthapanaPaint);

        // VISHWA VIKHYAT
        vishvaVikhiyatPaint.setColor(Color.rgb(255, 0, 0));
        vishvaVikhiyatPaint.setTextSize(25);
        vishvaVikhiyatPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("વિશ્વવિખપાત કથાકાર પુ. શ્રી કનૈયાલાલ ભટ સંચાલત", 1320-380, 110, vishvaVikhiyatPaint);

        // SHRI SANKARA MAHARAJ
        shriShankarMaharajPaint.setColor(Color.rgb(255, 0, 255));
        shriShankarMaharajPaint.setTextSize(30);
        shriShankarMaharajPaint.setTextAlign(Paint.Align.CENTER);
        shriShankarMaharajPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("શ્રી શંકર આશ્રમ - રાજકોટ", 940, 150, shriShankarMaharajPaint);

        // DATE
        datePaint.setColor(Color.BLACK);
        datePaint.setTextSize(25);
        datePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Dt. " + createdDate, 540, 170, datePaint);

        // BHAVDIYA
        String tempName = nameText;
        bhavdiyaPaint.setColor(Color.rgb(255, 0, 255));
        bhavdiyaPaint.setTextSize(32);
        bhavdiyaPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("ભવદિય શ્રી : ", 560, 230, bhavdiyaPaint);

        // BHAVDIYA TEXT
        namePaint.setColor(Color.rgb(0, 0, 139));
        namePaint.setTextSize(32);
        namePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(tempName.toUpperCase(Locale.ROOT), 705, 230, namePaint);

        // SMARNATHE
        String smarnathe_name = smarnartheText;
        smarnanthePaint.setColor(Color.rgb(255, 0, 255));
        smarnanthePaint.setTextSize(32);
        smarnanthePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("સ્મરણાર્થે : ", 560, 290, smarnanthePaint);

        // SMARNATHE TEXT
        snamePaint.setColor(Color.rgb(0, 0, 139));
        snamePaint.setTextSize(32);
        snamePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(smarnathe_name.toUpperCase(Locale.ROOT), 700, 290, snamePaint);

        // Tithi Tarikh
        String tempTithiTarikh = tithiDateSelected;
        tithiTarikhPaint.setColor(Color.rgb(255, 0, 255));
        tithiTarikhPaint.setTextSize(32);
        tithiTarikhPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("તિથી / તારીખ : " , 560, 350, tithiTarikhPaint);

        // Tithi Tarikh Text
        tarikhPaint.setColor(Color.rgb(0, 0, 139));
        tarikhPaint.setTextSize(32);
        tarikhPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(tempTithiTarikh, 760, 350, tarikhPaint);

        // SARNAMU
        String tempAddress = addressText;
        sarnamuPaint.setColor(Color.rgb(255, 0, 255));
        sarnamuPaint.setTextSize(32);
        sarnamuPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("સરનામું : ", 560, 410, sarnamuPaint);

        // SARNAMU TEXT
        addressPaint.setColor(Color.rgb(0, 0, 139));
        addressPaint.setTextSize(32);
        addressPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(tempAddress, 690, 410, addressPaint);

        // MOBILE NUMBER
        String contactNo = mobileNoText;
        mobileNoPaint.setColor(Color.rgb(255, 0, 255));
        mobileNoPaint.setTextSize(32f);
        mobileNoPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("મોબાઈલ નંબર : ", 560, 470, mobileNoPaint);

        // MOBILE NUMBER TEXT
        numberPaint.setColor(Color.rgb(0, 0, 139));
        numberPaint.setTextSize(32);
        numberPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(contactNo, 760, 470, numberPaint);


        // BHET
        String amount_donated = bhetText;
        bhetDonatedPaint.setColor(Color.rgb(255, 0, 255));
        bhetDonatedPaint.setTextSize(32f);
        bhetDonatedPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("ભેટ : Rs. ", 560, 525, bhetDonatedPaint);

        // BHET TEXT
        bhetPaint.setColor(Color.rgb(0, 0, 139));
        bhetPaint.setTextSize(32);
        bhetPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(amount_donated, 700, 525, bhetPaint);

        // BHET IN WORDS
        rsWordsPaint.setColor(Color.BLACK);
        rsWordsPaint.setTextSize(32f);
        rsWordsPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(inWords, 560, 580, rsWordsPaint);

        // ABHAR TEXT
        abharPaint.setColor(Color.rgb(0, 71, 171));
        abharPaint.setTextSize(28f);
        abharPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("સપ્રેમ ભેટ મળ્યા છે . આપનો હૃદયપૂર્વક આભાર ", 560, 640, abharPaint);

        // SVIKARNAR
        svikarnarPaint.setColor(Color.rgb(255, 0, 255));
        svikarnarPaint.setTextSize(28f);
        svikarnarPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("સ્વીકારનાર : ચંદ્રમણી કે ભટ્ટ", 560, 708 - 40, svikarnarPaint);

        myPdfDocument.finishPage(myPage1);

        String pdfName = "reciept(" + mobileNoText + ").pdf";
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfName);

            if (file.exists()) {
                // Append 1 to the new file name and store it.
                String newName = "reciept(" + mobileNoText + ")" + count + ".pdf";
                System.out.println("COUNT : " + count);
                System.out.println("FILENAME : " + newName);
                count = count + 1;
                File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), newName);
                myPdfDocument.writeTo(new FileOutputStream(file1));
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

                Toast.makeText(context, "File Saved:)", Toast.LENGTH_SHORT).show();
            } else {
                // Same name file does not exists then it will save with original name.
                myPdfDocument.writeTo(new FileOutputStream(file));
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

                Toast.makeText(context, "File Saved:)", Toast.LENGTH_SHORT).show();


            }
            myPdfDocument.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error Occurred in saving file!! Maybe same named file exists (Delete same name pdf)", Toast.LENGTH_SHORT).show();
        }

    }

    public File getFilePath(String pdfName) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File downloadsDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDirectory, pdfName);
        return file;
    }

}
