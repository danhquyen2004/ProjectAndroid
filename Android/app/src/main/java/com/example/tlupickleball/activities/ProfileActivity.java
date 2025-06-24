package com.example.tlupickleball.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.model.User;
import com.example.tlupickleball.network.core.ApiClient;
import com.example.tlupickleball.network.core.SessionManager;
import com.example.tlupickleball.network.service.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private EditText nameEditText, dobEditText;
    private ImageButton ivAvatar;
    private Button submitButton;
    private AutoCompleteTextView genderView;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private UserService service;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.nameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        submitButton = findViewById(R.id.submitButton);
        ivAvatar = findViewById(R.id.imgAvatar);
        genderView = findViewById(R.id.genderEditText);

        String[] genderOptions = {"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderView.setAdapter(adapter);
        genderView.setFocusable(false);
        genderView.setOnClickListener(v -> genderView.showDropDown());

        ivAvatar.setOnClickListener(v -> {
            Intent intentAvt = new Intent(Intent.ACTION_PICK);
            intentAvt.setType("image/*");
            startActivityForResult(intentAvt, REQUEST_CODE_PICK_IMAGE);
        });

        setupDobTextWatcher();

        service = ApiClient.getClient(this).create(UserService.class);

        submitButton.setOnClickListener(v -> {
            // BƯỚC 1: KIỂM TRA DỮ LIỆU ĐẦU VÀO
            String name = nameEditText.getText().toString().trim();
            String dobString = dobEditText.getText().toString().trim();
            String gender = genderView.getText().toString().trim();

            if (name.isEmpty()) {
                nameEditText.setError("Tên không được để trống");
                nameEditText.requestFocus();
                return;
            }
            if (dobString.length() != 10 || dobEditText.getError() != null) {
                dobEditText.setError("Ngày sinh không hợp lệ");
                dobEditText.requestFocus();
                return;
            }
            if (gender.isEmpty()) {
                genderView.setError("Vui lòng chọn giới tính");
                genderView.requestFocus();
                return;
            }

            // BƯỚC 2: CHUẨN BỊ DỮ LIỆU VÀ GỌI API
            showLoading();

            String dob = convertDateFormat(dobString);
            String genderApi = gender.equals("Nam") ? "male" : "female";

            saveUserProfile(name, dob, genderApi);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(ivAvatar);
        }
    }

    private void saveUserProfile(String name, String dob, String gender) {
        User user = new User();
        user.setFullName(name);
        user.setBirthDate(dob);
        user.setGender(gender);

        service.submitProfile(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                hideLoading(); // Ẩn loading khi có kết quả
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    if (selectedImageUri != null) {
                        uploadAvatarAndUpdateProfile(SessionManager.getUid(ProfileActivity.this), selectedImageUri);
                    }
                    else{
                        Intent intent = new Intent(ProfileActivity.this, UserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi cập nhật hồ sơ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading(); // Ẩn loading khi có lỗi
                Toast.makeText(ProfileActivity.this, "Lỗi mạng khi cập nhật hồ sơ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatarAndUpdateProfile(String uid, Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File tempFile = File.createTempFile("avatar", ".png", getCacheDir());
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
            inputStream.close();

            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType == null) {
                mimeType = "image/png";
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", tempFile.getName(), requestFile);

            service.updateAvatar(uid, body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(ProfileActivity.this, UserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        hideLoading(); // Ẩn loading nếu bước này thất bại
                        Toast.makeText(ProfileActivity.this, "Lỗi tải avatar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    hideLoading(); // Ẩn loading nếu có lỗi
                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối khi tải avatar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            hideLoading();
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDobTextWatcher() {
        dobEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final String ddmmyyyy = "DDMMYYYY";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(current)) {
                    return;
                }

                String clean = s.toString().replaceAll("[^\\d]", "");
                String cleanC = current.replaceAll("[^\\d]", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                    dobEditText.setError("Định dạng: dd/MM/yyyy");
                } else {
                    dobEditText.setError(null); // Xóa lỗi nếu đủ 8 số
                    int day = Integer.parseInt(clean.substring(0, 2));
                    int mon = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    // Kiểm tra ngày tháng hợp lệ
                    if (mon < 1 || mon > 12 || day < 1 || year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                        dobEditText.setError("Ngày không hợp lệ");
                    } else {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, mon - 1);
                        if (day > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            dobEditText.setError("Ngày không hợp lệ cho tháng này");
                        } else {
                            dobEditText.setError(null); // Ngày hợp lệ
                        }
                    }
                }

                // Tự động thêm dấu gạch chéo
                if (clean.length() >= 4) {
                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, Math.min(8, clean.length())));
                } else if (clean.length() >= 2) {
                    clean = String.format("%s/%s", clean.substring(0, 2), clean.substring(2));
                }

                sel = Math.max(0, sel);
                current = clean;
                dobEditText.setText(current);
                dobEditText.setSelection(Math.min(sel, current.length()));
            }
        });
    }
}