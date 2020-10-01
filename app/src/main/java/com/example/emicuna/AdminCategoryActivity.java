package com.example.emicuna;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdminCategoryActivity extends AppCompatActivity {
    private CardView cvFav, cvPeru, cvFastFood, cvAntojo, cvBebida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        cvFav = findViewById(R.id.cv_fav);
        cvPeru = findViewById(R.id.cv_peru);
        cvFastFood = findViewById(R.id.cv_fastfood);
        cvAntojo = findViewById(R.id.cv_antojo);
        cvBebida = findViewById(R.id.cv_bebida);

        cvFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "catFavorito");
                startActivity(intent);
            }
        });

        cvPeru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "catPeru");
                startActivity(intent);
            }
        });

        cvFastFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "catFastFood");
                startActivity(intent);
            }
        });

        cvAntojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "catAntojo");
                startActivity(intent);
            }
        });

        cvBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "catBebida");
                startActivity(intent);
            }
        });
    }
}