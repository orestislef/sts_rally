package gr.streetthugssalonika;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import gr.streetthugssalonika.Interfaces.ApiSTS;
import gr.streetthugssalonika.Models.UserModel;
import gr.streetthugssalonika.Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiSTS apiInterface;
    private ProgressDialog progressDialog;
    TextView currentUserTV;
    LinearLayout layout;
    Button addUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiInterface = RetrofitClient.getRetrofit().create(ApiSTS.class);
        progressDialog = new ProgressDialog(this);

        currentUserTV = findViewById(R.id.current_userTV);
        layout = findViewById(R.id.users_btnLL);
        addUserBtn = findViewById(R.id.add_user_btn);

        addUserBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,AddUserActivity.class);
            startActivity(intent);
          });

        getUsersList();

    }

    private void getUsersList() {
        progressDialog.setMessage("Loading Users");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<UserModel>> call = apiInterface.getAllUsers();
        call.enqueue(new Callback<List<UserModel>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()){
                    List<UserModel> mUsers = response.body();
                    for (int i = 0; i< Objects.requireNonNull(mUsers).size(); i++){
                        Button button = new Button(getApplicationContext());
                        button.setText(mUsers.get(i).getName());
                        button.setTag(mUsers.get(i).getId());
                        button.setOnClickListener(v -> {
                            currentUserTV.setText("Current User\nID: "+button.getTag()+"\nName: "+button.getText());
                        });
                        layout.addView(button);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed to get Users", Toast.LENGTH_SHORT).show();

            }
        });
    }
}