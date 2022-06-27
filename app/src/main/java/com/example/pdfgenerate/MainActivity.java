package com.example.pdfgenerate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.pdfgenerate.databinding.ActivityMainBinding;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "PDFCreatedActivity";
    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.generatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()){
                    try {
                        createPdfWrapper();
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (DocumentException e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private boolean isValid(){
        if (binding.editTextNama.getText().toString().isEmpty())
        {
            binding.editTextNama.setError(getString(R.string.namanotfound));
            binding.editTextNama.requestFocus();
            return false;
        }
        if (binding.editTextAlamat.getText().toString().isEmpty())
        {
            binding.editTextAlamat.setError(getString(R.string.alamatnotfound));
            binding.editTextAlamat.requestFocus();
            return false;
        }
        if (binding.editTextTtl.getText().toString().isEmpty())
        {
            binding.editTextTtl.setError(getString(R.string.ttlnotfound));
            binding.editTextTtl.requestFocus();
            return false;
        }
        if (binding.editTextPhone.getText().toString().isEmpty())
        {
            binding.editTextPhone.setError(getString(R.string.phonenotfound));
            binding.editTextPhone.requestFocus();
            return false;
        }
        return true;
    }

    private void createPdfWrapper() throws FileNotFoundException,DocumentException{
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }else {
            createPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Keluar", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Buat Folder PDF Baru");
        }
        pdfFile = new File(docsFolder.getAbsolutePath(),"PDFdigitalSignature.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("Nama Lengkap= "+binding.editTextNama.getText().toString()));
        document.add(new Paragraph("Alamat = " + binding.editTextAlamat.getText().toString()));
        document.add(new Paragraph("Tempat Tanggal Lahir = " + binding.editTextTtl.getText().toString()));
        document.add(new Paragraph("Nomor Handphone = " + binding.editTextPhone.getText().toString()));
        document.close();
        previewPdf();

    }

    private void previewPdf() {

        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        }else{
            Toast.makeText(this,"Download aplikasi pdf viewer untuk melihat hasil generate",Toast.LENGTH_SHORT).show();
        }
    }
}