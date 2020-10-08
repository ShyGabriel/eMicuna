package com.example.emicuna.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.emicuna.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    private String categoryName, productName, description, price, restaurant, saveCurrentDate, saveCurrentTime, productRandomKey, downloasImageURL;
    private Button addProductBtn;
    private EditText inputProductName, inputProductDesc, inputProductPrice, inputProductRestaurant;
    private ImageView imageView;
    private static final int GALLERYPICK = 1;
    private Uri imageUri;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        categoryName = getIntent().getExtras().get("category").toString();
        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        imageView = findViewById(R.id.image_view);
        productImageRef = FirebaseStorage.getInstance().getReference().child("ProductImg");

        inputProductName = findViewById(R.id.product_name);
        inputProductDesc = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);
        inputProductRestaurant = findViewById(R.id.product_restaurant);

        loadingBar = new ProgressDialog(this);

        addProductBtn = findViewById(R.id.add_new_product_btn);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERYPICK && resultCode==RESULT_OK && data!= null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void validateProductData() {
        productName = inputProductName.getText().toString();
        description = inputProductDesc.getText().toString();
        price = inputProductPrice.getText().toString();
        restaurant = inputProductRestaurant.getText().toString();

        if (imageUri == null){
            Toast.makeText(this, "La Imagen es obligatoria", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(productName)){
            Toast.makeText(this, "Coloque el nombre del producto", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Coloque la descripcion del producto", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Coloque el precio del producto", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(restaurant)){
            Toast.makeText(this, "Coloque el nombre del restaurante/proveedor", Toast.LENGTH_SHORT).show();
        }
        else{
            storeProductInformation();
        }

    }

    private void storeProductInformation() {
        loadingBar.setTitle("Añadiendo Producto/Comida");
        loadingBar.setMessage("Por favor espere, estamos añadiendo el producto/comida.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment()+ productRandomKey+ ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Imagen subida con exito", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloasImageURL = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloasImageURL = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "URL de la imagen del producto guardado en la BD", Toast.LENGTH_SHORT).show();
                            
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("image", downloasImageURL);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("restaurant", restaurant);
        productMap.put("productName", productName);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class));

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "El producto se añadio con exito", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingBar.dismiss();
                            String message2 = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message2, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}