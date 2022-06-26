package gr.streetthugssalonika;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import gr.streetthugssalonika.Interfaces.ApiSTS;
import gr.streetthugssalonika.Models.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddUserActivity extends AppCompatActivity {

    private static final String TAG = "AddUserActivity";
    private static final String BASE_URL = "http://192.168.1.17/";

    TextInputLayout nameInput;
    Button addUserBtn;

    Retrofit retrofit;
    ApiSTS apiInterfaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        addUserBtn = findViewById(R.id.add_btn);
        nameInput = findViewById(R.id.nameTextInputLayout);

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser(nameInput.getEditText().getText().toString());
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterfaces = retrofit.create(ApiSTS.class);
    }

    private void newUser(String text) {
        Call<List<UserModel>> call = apiInterfaces.insertNewUser(text);
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body());
                    Toast.makeText(AddUserActivity.this, "User " + text + " Added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                finish();
            }
        });
    }
}