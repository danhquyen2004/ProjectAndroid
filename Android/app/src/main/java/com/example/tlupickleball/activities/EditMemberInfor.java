package com.example.tlupickleball.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.base.BaseActivity;
import com.example.tlupickleball.activities.base.BaseMember;
import com.example.tlupickleball.model.User;

import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import retrofit2.Call;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class EditMemberInfor extends BaseMember {
    EditText edtName, edtDob;
    AutoCompleteTextView edtGender;
    ImageButton ivAvatar;
    Button btn_Accept;
    ImageButton btnBack;
    boolean isDialogShowing;
    String uid;
    Dialog dialogForm;
    private Uri selectedImageUri = null;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_member_infor);

        edtName = findViewById(R.id.edtPlayerNameInfor);
        edtGender = findViewById(R.id.edtGenderInfor);
        edtDob = findViewById(R.id.edtDobInfor);
        ivAvatar = findViewById(R.id.imgAvatar);

        btn_Accept = findViewById(R.id.btn_accept);
        btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        uid = getIntent().getStringExtra("uid");


//        edtName.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                edtName.setTextColor(Color.BLACK);
//                edtName.setTypeface(null, Typeface.BOLD);
//            } else {
//                edtName.setTextColor(ContextCompat.getColor(this, R.color.));
//                edtName.setTypeface(null, Typeface.NORMAL);
//            }
//        });
//
//        edtDob.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                edtDob.setTextColor(Color.BLACK);
//                edtDob.setTypeface(null, Typeface.BOLD);
//            } else {
//                edtDob.setTextColor(ContextCompat.getColor(this, R.color.your_default_color));
//                edtDob.setTypeface(null, Typeface.NORMAL);
//            }
//        });

        ivAvatar.setOnClickListener(v -> {
            Intent intentAvt = new Intent(Intent.ACTION_PICK);
            intentAvt.setType("image/*");
            startActivityForResult(intentAvt, REQUEST_CODE_PICK_IMAGE);
        });

        // Gender dropdown
        AutoCompleteTextView genderView = findViewById(R.id.edtGenderInfor);
        String[] genderOptions = {"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderView.setAdapter(adapter);
        genderView.setFocusable(false);
        genderView.setOnClickListener(v -> genderView.showDropDown());

        EditText dobEditText = findViewById(R.id.edtDobInfor);

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

//        Intent intent = getIntent();
//        tvName.setText(intent.getStringExtra("name"));
//        tvEmail.setText(intent.getStringExtra("email"));
//        ivAvatar.setImageResource(intent.getIntExtra("avatar", 0));

        btnBack.setOnClickListener(v -> onBackPressed());

        btn_Accept.setOnClickListener(v -> {
           showDialogForm();
        });

        LoadUserProfile(this, uid);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void LoadUserProfile(Context context, String uid) {
        showLoading();
        userService.getUserProfileById(uid).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    edtName.setText(user.getFullName());
                    edtGender.setText(user.getGender(), false);
                    // Sửa cách gọi hàm convert date
                    edtDob.setText(BaseActivity.convertToVietnameseDate(user.getBirthDate()));

                    Glide.with(context)
                            .load(user.getAvatarUrl())
                            .placeholder(R.drawable.default_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // Bỏ qua cache trên đĩa
                            .skipMemoryCache(true) // Bỏ qua cache trong bộ nhớ
                            .circleCrop()
                            .into(ivAvatar);
                } else {
                    Toast.makeText(context, "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                hideLoading();
                Toast.makeText(context, "Không thể truy vấn hồ sơ người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMember(String uid) {
        String name = edtName.getText().toString();
        String genderDisplay = edtGender.getText().toString();
        String dobDisplay = edtDob.getText().toString();

        String genderApi = "male";
        if (genderDisplay.equals("Nữ")) {
            genderApi = "female";
        }

        // Sử dụng hàm có sẵn từ BaseActivity
        String dobApi = BaseActivity.convertDateFormat(dobDisplay);
        if (dobApi == null) {
            Toast.makeText(this, "Lỗi định dạng ngày tháng.", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser = new User();
        updatedUser.setFullName(name);
        updatedUser.setGender(genderApi);
        updatedUser.setBirthDate(dobApi);

        showLoading();
        userService.updateUser(uid, updatedUser).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(EditMemberInfor.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditMemberInfor.this, "Cập nhật thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading();
                Toast.makeText(EditMemberInfor.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatarAndUpdateProfile(String uid, Uri imageUri) {
        showLoading();
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File tempFile = File.createTempFile("avatar", ".png", getCacheDir());
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.close();
            inputStream.close();

            // Sửa lỗi: Xử lý trường hợp MIME type là null
            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType == null) {
                mimeType = "image/png"; // Cung cấp giá trị mặc định an toàn
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", tempFile.getName(), requestFile);

            userService.updateAvatar(uid, body).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Sau khi tải avatar thành công, tiếp tục cập nhật các thông tin còn lại
                        Toast.makeText(EditMemberInfor.this, "Cập nhật ảnh thành công", Toast.LENGTH_SHORT).show();
                        updateMember(uid);
                    } else {
                        hideLoading();
                        Toast.makeText(EditMemberInfor.this, "Cập nhật avatar thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    hideLoading();
                    Toast.makeText(EditMemberInfor.this, "Lỗi kết nối khi tải avatar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            hideLoading();
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Lưu Uri của ảnh đã chọn
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(ivAvatar);
        }
    }

    private void showDialogForm() {
        if (isDialogShowing) {
            return; // Nếu dialog đã hiển thị, không làm gì cả
        }

        dialogForm = new Dialog(EditMemberInfor.this);
        dialogForm.setContentView(R.layout.view_dialog_form);
        dialogForm.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogForm.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_box));
        dialogForm.setCancelable(false);

        isDialogShowing = true;

        // Tìm các view trong dialog
        ImageView iconDialog = dialogForm.findViewById(R.id.icon_dialog);
        TextView titleDialog = dialogForm.findViewById(R.id.tv_confirm);
        TextView messageDialog = dialogForm.findViewById(R.id.tv_confirm_infor);
        Button btnDiaLogOK = dialogForm.findViewById(R.id.btn_approve);
        Button btnDiaLogCancel = dialogForm.findViewById(R.id.btn_reject);


        iconDialog.setImageResource(R.drawable.info_warning_fill);
        titleDialog.setText("Bạn chắc chắn muốn thay đổi thông tin");
        messageDialog.setText("Thông tin sẽ được cập nhật ngay lập tức, bạn có chắc chắn muốn tiếp tục?");
        btnDiaLogOK.setText("Xác nhận");
        btnDiaLogCancel.setText("Quay lại");

        btnDiaLogOK.setOnClickListener(v -> {
            // Kiểm tra xem người dùng có chọn ảnh mới không
            if (selectedImageUri != null) {
                // Nếu có, tải ảnh lên trước, sau đó cập nhật thông tin
                uploadAvatarAndUpdateProfile(uid, selectedImageUri);
            } else {
                // Nếu không, chỉ cập nhật thông tin văn bản
                updateMember(uid);
            }
        });

        btnDiaLogCancel.setOnClickListener(v -> {
            isDialogShowing = false; // Reset trạng thái
            dialogForm.dismiss();
        });

        // Thêm listener khi dialog bị dismiss
        dialogForm.setOnDismissListener(dialog -> {
            isDialogShowing = false;
        });

        dialogForm.show();
    }
}