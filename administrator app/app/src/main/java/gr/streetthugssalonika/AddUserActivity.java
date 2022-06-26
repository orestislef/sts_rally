package gr.streetthugssalonika;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class AddUserActivity extends AppCompatActivity {

    TextInputLayout nameInput;
    Button addUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        addUserBtn = findViewById(R.id.add_btn);
        nameInput = findViewById(R.id.nameTextInputLayout);

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}