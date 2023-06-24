package com.example.appnotes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnotes.R;
import com.example.appnotes.database.NotesDatabase;
import com.example.appnotes.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDatetime;
    private ImageView imageNote;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    private View viewSubtitleIndicator;

    private String selectedNoteColor;
    private String selectImagePath;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;

    private Note alreadyAvailabeNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /* */

        inputNoteSubtitle = findViewById(R.id.inputNoteSubTitle);
        inputNoteText     = findViewById(R.id.inputNote);
        inputNoteTitle    = findViewById(R.id.inputNoteTitile);
        textDatetime      = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        textDatetime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );
/
        //default Color
        selectedNoteColor = "#333333";

        selectImagePath = "";

        if (getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailabeNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdate();
        }
/*Đoạn code này kiểm tra xem có giá trị nào được truyền dưới dạng boolean và Serializable với 
các key tương ứng là "isvieworupdate" và "note" không. Nếu đúng thì nó gán giá trị của lớp "note" đã truyền 
đến biến alreadyavailabenote và gọi phương thức setvieworupdate(). Giả sử nếu giá trị 'isvieworupdate' 
trong intent là true thì hiển thị giao diện xem hoặc cập nhật ghi chú lên màn hình, ngược lại nếu giá trị là false thì
 không thực hiện hành động nào cả. */
        findViewById(R.id.imageRemoveWebURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWebURL.setText(null);
                layoutWebURL.setVisibility(View.GONE);
            }
        });
        /*Khi người dùng nhấp vào View này, mã sẽ xóa nội dung trong
         EditText có ID "textweburl" và ẩn Layout có ID "layoutweburl". */
        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectImagePath = "";
            }
        });
/*. Đặt `Bitmap` của `imagenote` thành `null`.
5. Đặt tính năng `visibility` của `imagenote` thành `view.gone`.
6. Đặt tính năng `visibility` của `imageremoveimage` thành `view.gone`.
7. Đặt `selectimagepath` thành chuỗi rỗng.

Nói cách khác, đoạn code này giúp xóa bỏ hình ảnh đang được chọn và ẩn nó khi
 người dùng nhấp vào nút "Xóa ảnh" trên giao diện. */
        if (getIntent().getBooleanExtra("isFromQuickActions",false)){
            String type = getIntent().getStringExtra("quickActionType");
            if (type != null){
                if (type.equals("image")){
                    selectImagePath = getIntent().getStringExtra("imagePath");
                    imageNote.setImageBitmap(BitmapFactory.decodeFile(selectImagePath));
                    imageNote.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                }
                else if (type.equals("URL")){
                    textWebURL.setText(getIntent().getStringExtra("URL"));
                    layoutWebURL.setVisibility(View.VISIBLE);
                }
            }
        }
        initMiscellaneous();
        setSubtitleIndicatorColor();
    }
/*- Kiểm tra nếu intent có extra "isfromquickactions" là true thì thực hiện các xử lý tiếp theo.
- Lấy giá trị của extra "quickactiontype".
- Nếu "quickactiontype" có giá trị và bằng "image", lấy đường dẫn của hình ảnh từ extra "imagepath" 
và gán vào đối tượng imagenote để hiển thị hình ảnh. Hiển thị các control liên quan đến hình ảnh.
- Nếu "quickactiontype" có giá trị và bằng "url", lấy đường dẫn web từ extra "url" gán vào đối tượng
 textweburl để hiển thị đường dẫn. Hiển thị các control liên quan đến đường dẫn web.
- Tiếp theo là thực hiện các thủ tục initmiscellaneous và setsubtitleindicatorcolor. */
    private void setViewOrUpdate(){
        inputNoteTitle.setText(alreadyAvailabeNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailabeNote.getSubtitle());
        inputNoteText.setText(alreadyAvailabeNote.getNotetext());
        textDatetime.setText(alreadyAvailabeNote.getDatetime());

        if (alreadyAvailabeNote.getImagePath() != null && !alreadyAvailabeNote.getImagePath().trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailabeNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectImagePath = alreadyAvailabeNote.getImagePath();
        }
        if (alreadyAvailabeNote.getWebLink() != null && !alreadyAvailabeNote.getWebLink().trim().isEmpty()){
            textWebURL.setText(alreadyAvailabeNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
    }
    /*setvieworupdate()` có chức năng lấy các giá trị từ một đối tượng `alreadyavailable`
     (đã có sẵn) của một ghi chú (note) và gán vào các thành phần của giao diện (view) để hiển thị 
     hoặc cập nhật thông tin của ghi chú đó.

Các lệnh trong phương thức này thực hiện các nhiệm vụ như sau:

- Lấy tiêu đề (title), phụ đề (subtitle), nội dung (notetext) và thời gian (datetime) của ghi chú 
`alreadyavailable` và gán lần lượt vào các ô nhập liệu (edit text) trong giao diện
 `inputnotetitle`, `inputnotesubtitle`, `inputnotetext` và `textdatetime`.

- Kiểm tra xem đường dẫn của hình ảnh (imagepath) và liên kết web (weblink) có được định nghĩa trong 
đối tượng `alreadyavailable` không. Nếu tồn tại đường dẫn hình ảnh, phương thức sẽ tải ảnh đó lên và 
hiển thị nó trong một ImageView (`imagenote`), cũng như hiển thị một nút để xóa ảnh đó (`imageremoveimage`). 
Nếu tồn tại liên kết web, phương thức sẽ hiển thị nó trong một TextView (`textweburl`) và hiển thị cả layout
 chứa liên kết đó (`layoutweburl`).

- Cuối cùng, phương thức cập nhật một biến `selectimagepath` để lưu đường dẫn của ảnh (nếu có). */
    private void saveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note title can't be empty !",Toast.LENGTH_LONG).show();
            return;
        }
        else if(inputNoteSubtitle.getText().toString().trim().isEmpty() &&
                inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note can't be empty !",Toast.LENGTH_LONG).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNotetext(inputNoteText.getText().toString());
        note.setDatetime(textDatetime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectImagePath);

        if (layoutWebURL.getVisibility() == View.VISIBLE){
            note.setWebLink(textWebURL.getText().toString());
        }
/*Đoạn code trên có chức năng lưu thông tin ghi chú (note) vào cơ sở dữ liệu.
 Nó sẽ kiểm tra trường tiêu đề ghi chú (inputnotetitle) và nếu trống nó sẽ hiển thị thông báo lỗi
  "note title can't be empty !" và trả về. Nếu trường phụ đề và nội dung ghi chú (inputnotesubtitle và inputnotetext)
   đều trống, nó sẽ hiển thị thông báo lỗi "note can't be empty !" và trả về.

Nếu không có lỗi xảy ra trong việc kiểm tra thông tin, đoạn code tiếp tục bằng cách tạo một đối tượng ghi chú 
(note) mới và thiết lập các thuộc tính của nó (tiêu đề, phụ đề, nội dung, thời gian, màu sắc, hình ảnh và 
liên kết web). Nếu trường URL web (textweburl) đã được nhập vào (vì chúng ta đã kiểm tra trước đó), đối tượng
 ghi chú mới sẽ được cập nhật với liên kết web (weblink) mới đó. */
        if (alreadyAvailabeNote != null){
            note.setId(alreadyAvailabeNote.getId());
        }
/*Đoạn code trên kiểm tra xem một ghi chú đã có sẵn trong danh sách hay chưa thông qua biến alreadyavailabenote.
 Nếu ghi chú đã có sẵn, nó sẽ được lấy định danh (id) từ ghi chú đó và gán cho ghi chú trong đoạn code trên 
 (biến note). Điều này giúp đảm bảo rằng ghi chú được lưu trữ đúng với định danh của nó trong danh sách. Nếu ghi chú chưa có sẵn, biến alreadyavailabenote sẽ có giá trị null và không có gì được thực hiện. */
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }
/*Đoạn code trên là một lớp "savenotetask" được viết dưới dạng lớp chồng là một lớp con của lớp AsyncTask.
 Lớp này có mục đích lưu trữ các ghi chú vào cơ sở dữ liệu.

- @suppresslint("staticfieldleak"): chú thích này giúp tắt các cảnh báo (warning) lỗi kết nối mà trình biên dịch
 sẽ hiển thị khi thoát khỏi việc sử dụng các trường tĩnh của lớp (static) mà chưa được khởi tạo.
- Asynctask, Void, Void>: là một lớp trừu tượng trong Java, cung cấp một cơ chế sử dụng đơn giản để thực hiện 
các nhiệm vụ đa tiến trình (multi-thread), mà không cần phải tạo ra các luồng mới.
- @Override: chú thích này tiếp tục được sử dụng để đảm bảo rằng phương thức được định nghĩa (doInBackground) 
được ghi đè từ lớp cha của nó.
- doInBackground(Void... voids): phương thức này được gọi sau khi AsyncTask được thực hiện, và là nơi thực hiện 
các tác vụ nền (background tasks) trong luồng khác. Trong trường hợp này, nó được sử dụng để chèn một ghi chú mới vào CSDL và trả về null. */
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
            /*Đoạn mã này là phương thức `onPostExecute()` của một AsyncTask trong Android. Khi tiến trình thực 
            hiện xong, phương thức `onPostExecute()` này sẽ được gọi để thực hiện các hành động kết thúc.

- `@Override`: là annotation để xác định phương thức hiện tại đang Override một phương thức trong lớp cha.
- `protected void onpostexecute(void avoid)`: định nghĩa phương thức `onPostExecute()` với tham số truyền vào 
là một Void object và trả về void.
- `super.onpostexecute(avoid);`: gọi phương thức `onPostExecute()` của lớp cha.
- `intent intent = new intent();`: tạo một Intent mới.
- `setresult(result_ok,intent);`: đặt kết quả trả về cho Activity với mã kết quả là `RESULT_OK` và Intent trên.
- `finish();`: kết thúc Activity hiện tại. */
        }
        new SaveNoteTask().execute();
    }

    private void initMiscellaneous(){
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            });
/*Đoạn code trên định nghĩa một biến Linear Layout với ID "layoutmiscellaneous" và đặt giá trị bằng phương thức 
findViewById(). Sau đó nó khởi tạo một BottomSheetBehavior với biến Linear Layout trên. Nó đặt một sự kiện lắng 
nghe cho TextView có ID "textmiscellaneous" để xử lý khi được nhấp vào. Sự kiện onClick sử dụng BottomSheetBehavior 
để mở và đóng các mục bổ sung bên dưới. Khi được nhấp vào, nó kiểm tra trạng thái của BottomSheetBehavior để quyết 
định xem nó có nên mở hoặc đóng tập tin bổ sung. Nếu nó không được mở, onclick sẽ mở tập tin và nếu nó đang mở nó 
sẽ đóng nó. */
        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        //Gọi phương thức `setsubtitleindicatorcolor()` để cập nhật màu sắc cho một số view khác.
        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#3A52FC";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });

        if (alreadyAvailabeNote != null && alreadyAvailabeNote.getColor() != null &&
                !alreadyAvailabeNote.getColor().trim().isEmpty()){

            switch (alreadyAvailabeNote.getColor()){
                case "#FDBE3B":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52FC":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }
                else {
                    selectImage();
                }
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
            }
        });

        if (alreadyAvailabeNote != null){
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }
    }


    private void showDeleteNoteDialog(){
        if (dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            /*Đoạn code này kiểm tra xem biến `dialogdeletenote` có giá trị null hay không. 
            Nếu biến này null thì một hộp thoại xóa ghi chú sẽ được tạo ra và được hiển thị 
            trên màn hình bằng cách sử dụng builder và layoutinflater. Sau khi xây dựng hộp thoại xóa ghi chú, 
            nó được gán cho biến `dialogdeletenote`. Nếu cửa sổ hiện tại của hộp thoại không phải null, nó sẽ
             được đặt là đối tượng màu không đổ bóng.

*/
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailabeNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    /*Đoạn code trên là một hàm xử lý sự kiện khi người dùng bấm vào một View trong giao diện ứng
                     dụng. Cụ thể, hàm này được gọi khi người dùng bấm vào View có id là "textdeletenote".
                      Khi xảy ra sự kiện này, một đối tượng "deletenotetask" được tạo ra, đối tượng này sẽ thực hiện 
                      xóa một ghi chú từ cơ sở dữ liệu và sau đó gửi kết quả về cho Activity hiện tại thông qua một 
                      intent. Nếu hành động xóa ghi chú thành công, intent sẽ có một extra có tên là "isnotedeleted"
                       với giá trị true. Nếu xóa không thành công, kết quả trả về sẽ là RESULT_CANCELED.
*/
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
    }
    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
/*Đoạn code này có tác dụng thiết lập màu sắc cho chỉ báo phụ đề trong một ứng dụng.
 Cụ thể, nó sử dụng một đối tượng GradientDrawable để lấy và thiết lập màu sắc cho chỉ báo phụ đề.
  Để làm điều này, đoạn mã sử dụng phương thức `getColor()` để chuyển đổi một giá trị màu sắc
   (trong biến `selectednotecolor`) thành một đối tượng màu sắc và sử dụng phương thức `setcolor()`
    của GradientDrawable để thiết lập màu sắc được chuyển đổi.

*/
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }
    /*Đoạn code này là một phương thức trong Java với tên là selectimage(), có chức năng để chọn hình ảnh từ
     bộ nhớ điện thoại hoặc thư viện ảnh của máy tính.

- Dòng 2: Tạo ra một đối tượng Intent với action là "intent.action_pick" (yêu cầu hệ thống để chọn một 
thành phần nhất định) và Uri là "mediastore.images.media.external_content_uri" (trả về tất cả các hình ảnh có
 trong bộ nhớ).

- Dòng 3: Kiểm tra xem có ứng dụng nào có khả năng xử lý Intent này hay không bằng cách gọi 
resolveActivity() và truyền vào đối tượng PackageManager.

- Dòng 4: Nếu có ứng dụng hỗ trợ xử lý Intent trên thì gọi startActivityForResult() để mở Activity hoặc
 ứng dụng tương ứng để chọn hình ảnh. Kết quả trả về sẽ được xử lý trong phương thức onActivityResult().

- request_code_select_image là một mã yêu cầu (request code) dùng để phân biệt kết quả trả về từ các Activity
 khác nhau trong ứng dụng. */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else {
                Toast.makeText(this, "Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*Đoạn code trên là một phương thức `onRequestPermissionsResult` được ghi đè (@override) để xử lý 
    kết quả của việc yêu cầu cấp quyền sử dụng bộ nhớ trong thiết bị.

- `requestcode` là mã yêu cầu cấp quyền được gửi đi trước đó.
- `permissions` là mảng chứa danh sách các quyền được yêu cầu.
- `grantresults` là mảng chứa kết quả của quá trình cấp quyền tương ứng với các quyền được yêu cầu ở `permissions`.

Nếu `requestcode == request_code_storage_permission` và `grantresults` chứa ít nhất một phần tử, thì code kiểm 
tra xem quyền sử dụng bộ nhớ đã được cấp hay chưa.
 Nếu quyền đã được cấp (tức `grantresults[0] == packagemanager.permission_granted`) thì phương thức `selectimage()`
  sẽ được gọi để chọn ảnh, ngược lại thì sẽ hiển thị một thông báo dạng toast thông báo "permission denied" với độ dài ngắn. */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE || requestCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);

                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

                        selectImagePath = getPathFromUri(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
/*Đoạn code trên là một phương thức (method) trong lớp (class) được bảo vệ (protected) và có tên 
là onactivityresult(). Nó được sử dụng để xử lý kết quả trả về từ một hoạt động (activity) trước đó
 và thực hiện các hành động liên quan đến việc chọn ảnh.

Thông tin về ý nghĩa của các tham số đầu vào như sau:

- requestcode: mã yêu cầu (request code) đã được gửi đến hoạt động trước đó.
- resultcode: mã kết quả (result code) trả về từ hoạt động trước đó.
- data: đối tượng Intent chứa dữ liệu trả về từ hoạt động trước đó.

Nếu request code là request_code_select_image hoặc result code là RESULT_OK, phương thức sẽ lấy 
đường dẫn của ảnh được chọn và hiển thị ảnh đó trên màn hình. Để làm điều này, phương thức sử dụng các lớp
 như InputStream, BitmapFactory và Bitmap để đọc và xử lý dữ liệu hình ảnh từ URI được chọn. Cuối cùng, đường 
 dẫn của ảnh được lưu trữ trong biến selectimagepath để được sử dụng cho các mục đích khác sau này. */
    private String getPathFromUri(Uri contentUri){

        String filePath;

        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null,null);
        if (cursor == null){
            filePath = contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
    /*Đoạn code trên là một method (phương thức) trong lớp (class) và có mục đích là lấy đường dẫn 
    tệp tin (file path) từ một URI. Hàm này nhận đầu vào là một đối tượng URI, và sau đó thực hiện truy vấn 
    (query) với đối tượng Cursor thông qua ContentResolver để truy xuất thông tin đường dẫn của tập tin.
     Nếu cursor trả về null, nghĩa là không có thông tin đường dẫn trong URI đó, nó sẽ lấy đường dẫn trực tiếp từ
      URI. Ngược lại, nếu cursor trả về một bảng dữ liệu thông tin tập tin, phương thức này sẽ di chuyển đến hàng 
      đầu tiên của cursor và lấy giá trị tương ứng của cột "_data", sau đó đóng cursor lại trước khi trả về giá trị
       đường dẫn. */

    private void showAddURLDialog(){
        if (dialogAddURL == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                R.layout.layout_add_url,
                        (ViewGroup) findViewById(R.id.layoutAddUrlContainer));
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
/*Đoạn code này kiểm tra biến `dialogaddurl` nếu nó không có giá trị thì tạo mới một đối tượng AlertDialog.Buider 
để tạo ra một hộp thoại dialog. Sau đó, tạo một view từ layout được định nghĩa trong file `layout_add_url.xml` và 
thiết lập view trong dialog bằng phương thức `builder.setView(view)`. Tiếp theo, tạo đối tượng `dialogaddurl` và 
thiết lập nó bằng phương thức `builder.create()`. Sau đó, kiểm tra xem `dialogaddurl` có window không và nếu có sẽ
 đặt `background drawable` cho window đó với một ColorDrawable trong suốt. */
            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoteActivity.this,"Eenter URL",Toast.LENGTH_SHORT).show();
                    }
                    else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                        Toast.makeText(CreateNoteActivity.this,"Enter valid URL",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();

                    }
                }
            });
            /*- Xác định view có id là "textadd" bằng phương thức findViewById() và thiết lập onClickListener()
             cho nó bằng anonymous inner class.
- Trong phương thức onClick(), kiểm tra liệu giá trị nhập vào ở inputurl có rỗng không. Nếu có, hiển thị một Toast
 thông báo lỗi và không làm gì tiếp theo.
- Nếu không rỗng, kiểm tra xem giá trị nhập vào có phù hợp với mẫu đường dẫn web hay không bằng cách so sánh với 
regular expression `patterns.web_url`. Nếu không, hiển thị một Toast thông báo lỗi và không làm gì tiếp theo.
- Nếu giá trị nhập vào hợp lệ, hiển thị giá trị đó trên textweburl và thiết lập visibility của layoutweburl 
là VISIBLE (hiển thị layout lên màn hình). Sau đó, đóng dialogaddurl điều khiển hiển thị cửa sổ dialog hiện tại. */

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }
}