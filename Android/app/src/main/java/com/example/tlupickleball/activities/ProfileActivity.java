package com.example.tlupickleball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.service.UserService;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private EditText nameEditText, dobEditText;
    private Button submitButton;
    UserService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.nameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        submitButton = findViewById(R.id.submitButton);

        AutoCompleteTextView genderView = findViewById(R.id.genderEditText);
        String[] genderOptions = {"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderView.setAdapter(adapter);
        genderView.setFocusable(false);
        genderView.setOnClickListener(v -> genderView.showDropDown());

        dobEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!input.equals(current)) {
                    String clean = input.replaceAll("[^\\d]", "");
                    String cleanC = current.replaceAll("[^\\d]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }

                    String formatted = clean.substring(0,2) + "/" + clean.substring(2,4) + "/" + clean.substring(4,8);

                    current = formatted;
                    dobEditText.setText(formatted);
                    dobEditText.setSelection(sel < current.length() ? sel : current.length());

                    // Only validate if all are digits and length is 8
                    if (clean.length() == 8 && clean.matches("\\d{8}")) {
                        int day = Integer.parseInt(clean.substring(0,2));
                        int month = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        Calendar now = Calendar.getInstance();
                        int currentYear = now.get(Calendar.YEAR);

                        boolean valid = true;
                        String errorMsg = null;

                        if (day < 1 || month < 1 || month > 12 || year < 1900 || year > currentYear) {
                            valid = false;
                            errorMsg = "Ngày, tháng hoặc năm không hợp lệ";
                        } else {
                            Calendar cal = Calendar.getInstance();
                            cal.setLenient(false);
                            cal.set(year, month - 1, day);
                            try {
                                cal.getTime();
                                Calendar inputDate = Calendar.getInstance();
                                inputDate.set(year, month - 1, day, 0, 0, 0);
                                inputDate.set(Calendar.MILLISECOND, 0);
                                if (inputDate.after(now)) {
                                    valid = false;
                                    errorMsg = "Không được nhập ngày trong tương lai";
                                }
                            } catch (Exception e) {
                                valid = false;
                                errorMsg = "Ngày không hợp lệ";
                            }
                        }
                        if (!valid) {
                            dobEditText.setError(errorMsg);
                        } else {
                            dobEditText.setError(null);
                        }
                    } else if (input.length() > 0 && input.length() < 10) {
                        dobEditText.setError("Định dạng: dd/MM/yyyy");
                    } else {
                        dobEditText.setError(null);
                    }
                }
            }
        });

        service = ApiClient.getClient(this).create(UserService.class);

        submitButton.setOnClickListener(v -> {
            showLoading();

            String name = nameEditText.getText().toString().trim();
            String dob = convertDateFormat(dobEditText.getText().toString().trim());

            String gender = genderView.getText().toString().trim();

            if(gender.equals("Nam")) gender = "male";
            else if(gender.equals("Nữ")) gender = "female";


            saveUserProfile(name, dob,gender);
        });
    }

    private void saveUserProfile(String name, String dob, String gender)
    {
        User user = new User();
        user.setFullName(name);
        user.setBirthDate(dob);
        user.setGender(gender);

        service.submitProfile(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, UserActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }
}

