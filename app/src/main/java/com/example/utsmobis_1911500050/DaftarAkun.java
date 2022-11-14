package com.example.utsmobis_1911500050;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DaftarAkun extends AppCompatActivity {

    EditText txtHp, txtNama, txtTglLahir, txtAlamat, txtDomisili;
    TextView tvSimTglLahir;
    EditText txtAsalSekolah;
    Button btnRegister;
    DatePickerDialog dtPd;
    SimpleDateFormat dtFor,dtFor2;

    RequestQueue requestQueue;
    StringRequest stringRequest;

    Button btnUploadFoto;
    ImageView imgUploadFoto;
    Bitmap bitmap, decoded;
    int bitmap_size = 60; //kualitas kompresi 1-100
    int PICK_IMAGE_REQUEST = 1;
    public static Integer cFoto; //untuk tahu apakah ada gambar diupload

    private void zoomGambar(String gambar){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DaftarAkun.this);
        View mView =getLayoutInflater().inflate(R.layout.dialog_zoom, null);
        PhotoView imgZoom = mView.findViewById(R.id.imgZoom);

        if (cFoto>0 && gambar.equalsIgnoreCase("foto")){
            imgZoom.setImageDrawable(imgUploadFoto.getDrawable());
        }

        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void konekDB(String aksi){
        stringRequest = new StringRequest(Request.Method.POST,
                ((ClassGlobal) getApplication()).getURL() + "daftar.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String pesan = jObj.getString("pesan");
                            boolean hasil = jObj.getBoolean("hasil");
                            Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_SHORT).show();
                            if(hasil) {
                                if(aksi.equalsIgnoreCase("daftar")){
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } //akhir onResponse
                }, //akhir Listener
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Gagal menghubungi server : " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) //akhir ErrorListener dan new StringRequest
        {   @Override
        protected Map<String,String> getParams() throws AuthFailureError {
            Map<String, String> param = new HashMap<String, String>();
            if(aksi.equalsIgnoreCase("daftar"))
                param.put("aksi","daftar");
            param.put("hp", txtHp.getText().toString().trim());
            param.put("nama", txtNama.getText().toString().trim());
            param.put("tgllahir", tvSimTglLahir.getText().toString());
            param.put("alamat", txtAlamat.getText().toString().trim());
            param.put("domisili", txtDomisili.getText().toString().trim());
            param.put("asalsekolah", txtAsalSekolah.getText().toString().trim());
            param.put("fotodaftar", getStringImage(decoded));
            return param;
        }
        }; //akhir stringRequest =
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    } //akhir method konekDB

    private void showDateDialog() {
        Calendar kalender = Calendar.getInstance();
        dtPd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtTglLahir.setText(dtFor.format(newDate.getTime())); // DD/MM/YYYY untuk user
                tvSimTglLahir.setText(dtFor2.format(newDate.getTime())); // YYYY-MM-DD untuk database
            }
        }, kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH)
        ); //akhir dtPd

        //tvSimTglLahir formatnya 2000-12-24 nanti kita ambil 2000, lalu 12, lalu 24
        //Bulan kurang 1 agar dia sinkron (bawaan sananya)
        dtPd.updateDate(Integer.parseInt(tvSimTglLahir.getText().toString().substring(0, 4)),
                Integer.parseInt(tvSimTglLahir.getText().toString().substring(
                        tvSimTglLahir.length() - 5, tvSimTglLahir.length() - 3)) - 1,
                Integer.parseInt(tvSimTglLahir.getText().toString().substring(tvSimTglLahir.length() - 2,
                        tvSimTglLahir.length()))
        );
        dtPd.show(); //Tampilkan kalender
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_akun);
        txtHp = (EditText) findViewById(R.id.txtHp);
        txtNama = (EditText) findViewById(R.id.txtNama);
        txtTglLahir = (EditText) findViewById(R.id.txtTglLahir);
        txtAlamat = (EditText) findViewById(R.id.txtAlamat);
        txtDomisili = (EditText) findViewById(R.id.txtDomisili);
        txtAsalSekolah = (EditText) findViewById(R.id.txtAsalSekolah);


        btnRegister = (Button) findViewById(R.id.btnRegister);
        tvSimTglLahir = (TextView) findViewById(R.id.tvSimTglLahir);



        //Satu ini format 31/12/1990 untuk txtTglLahir
        dtFor = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        //Ini format ke database MySQL 1990-12-31 untuk tvSimTglLahir
        dtFor2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        txtTglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        txtTglLahir.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (txtTglLahir.hasFocus()){
                    showDateDialog();
                    txtTglLahir.setKeyListener(null);
                }
            }
        });

        imgUploadFoto = (ImageView) findViewById(R.id.imgUploadFoto);
        btnUploadFoto = (Button) findViewById(R.id.btnUploadFoto);
        cFoto = 0;

        imgUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { zoomGambar("foto"); }
        });

        btnUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihGambar();
                ClassGlobal.global_gambar="foto";
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtHp.length() < 10){
                    txtHp.setError("Nomor HP minimal 10 angka!");
                    txtHp.requestFocus();
                } //akhir if txtHp
                else if(txtNama.getText().toString().trim().isEmpty()){
                    txtNama.setError("Nama tidak boleh kosong!");
                    txtNama.requestFocus();
                }
                else if(txtAlamat.getText().toString().trim().isEmpty()){
                    txtAlamat.setError("Alamat tidak boleh kosong!");
                    txtAlamat.requestFocus();
                }
                else if(txtAsalSekolah.getText().toString().trim().isEmpty()){
                    txtAsalSekolah.setError("Password minimal 4 karakter!");
                    txtAsalSekolah.requestFocus();
                }
                else if(txtDomisili.getText().toString().isEmpty()){
                    txtDomisili.setError("Jika diisi, format email harus: email@email.com!");
                    txtDomisili.requestFocus();
                }
                else if (cFoto == 0){
                    Toast.makeText(getApplicationContext(), "Harap upload Foto!",
                            Toast.LENGTH_LONG).show();
                }
                else {

/*                        String pesan = "";
                        pesan += "HP: "+ txtHp.getText().toString();
                        pesan += "\nNama: "+ txtNama.getText().toString();
                        pesan += "\nTanggal Lahir: " + tvSimTglLahir.getText();
                        pesan += "\nGender: " + genderPilih;
                        pesan += "\nAlamat: " + txtAlamat.getText();
                        if(!txtEmail.getText().toString().trim().isEmpty())
                            pesan += "\nEmail: " + txtEmail.getText();
                        pesan += "\nPassword: " + txtPassword.getText();
                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();*/
                    konekDB("daftar");
                }
            } //akhir onClick btnRegister
        });
    } //akhir onCreate

    private void pilihGambar(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto"),
                PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    } //akhir getStringImage

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            if(ClassGlobal.global_gambar.equalsIgnoreCase("foto")){
                cFoto++;
            }
            Uri filepath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                setToImageView(getResizedBitmap(bitmap, 1600));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    } // akhir onActivityResult

    private void setToImageView(Bitmap bmp){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        if(ClassGlobal.global_gambar.equalsIgnoreCase("foto")){
            decoded = BitmapFactory.decodeStream(
                    new ByteArrayInputStream(bytes.toByteArray()));
            imgUploadFoto.setImageBitmap(decoded);
            imgUploadFoto.setVisibility(View.VISIBLE);
        }
    } //akhir setToImageView

    public Bitmap getResizedBitmap(Bitmap image, int maxSize){
        int lebar = image.getWidth();
        int tinggi = image.getHeight();
        float rasio =  (float) lebar/ (float) tinggi;
        if(rasio > 1) { //jika orientasi landscape
            lebar = maxSize;
            tinggi = (int) (lebar / rasio);
        } else {
            tinggi = maxSize;
            lebar = (int) (tinggi * rasio);
        }
        return Bitmap.createScaledBitmap(image, lebar, tinggi, true);
    }

} //akhir class DaftarAkun
