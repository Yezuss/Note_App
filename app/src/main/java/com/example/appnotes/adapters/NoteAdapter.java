package com.example.appnotes.adapters;

import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnotes.R;
import com.example.appnotes.entities.Note;
import com.example.appnotes.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> notesSourse;

    public NoteAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSourse = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_note,
                parent,
                false
        ));
    }
/*Đoạn code trên là một phương thức `onCreateViewHolder()` được ghi đè (`@override`) trong một lớp
 `RecyclerView.Adapter`.

- `@nonnull` cho biết các đối số không thể là NULL.
- Phương thức này trả về một đối tượng `noteviewholder` được khởi tạo bằng cách inflate (khởi tạo) 
layout `R.layout.item_container_note` trong một đối tượng `ViewGroup` có tên là `parent`.
- Phương thức này nhận hai đối số:
+ `parent` là một đối tượng `ViewGroup` chứa nhiều `View`(thường là `RecyclerView`).
+ `viewtype` là loại view. */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position),position);
            }
        });
    }
    /*Đoạn code trên là phương thức `onBindViewHolder` bên trong một Adapter dùng để hiển thị danh sách 
    các ghi chú (notes) lên một RecyclerView.

- `@override`: ghi đè phương thức cũ của lớp cha (nếu có).
- `public void onbindviewholder(@nonnull noteviewholder holder, int position)`: phương thức bind data vào
 ViewHolder được định nghĩa bởi lớp NoteViewHolder được truyền vào phương thức này và position là vị trí
  hiện tại của ViewHolder trong danh sách.
- `holder.setnote(notes.get(position))`: gán dữ liệu (notes) tại vị trí position vào ViewHolder.
- `holder.layoutnote.setonclicklistener(new view.onclicklistener() {...});`: thiết lập listener khi bấm vào
 View hiển thị ghi chú (layoutnote).
- `noteslistener.onnoteclicked(notes.get(position),position);`: gọi phương thức `onNoteClicked` được định nghĩa
 bên ngoài Adapter, truyền vào dữ liệu và vị trí của ViewHolder đang được click. */

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    /*Đoạn code trên là định nghĩa của hai phương thức `getItemCount()` và `getItemViewType(int position)`
     trong một custom `RecyclerView.Adapter`. Với ý nghĩa:

- `getItemCount()` : Phương thức trả về số lượng item trong danh sách được hiển thị trong RecyclerView.
- `getItemViewType(int position)` : Phương thức trả về loại view hiển thị cho item ở một vị trí được cung cấp. 
Trong trường hợp này, loại view sẽ được thiết đặt là vị trí của item. */

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textSubtitle, textDateTime;

        LinearLayout layoutNote;
        RoundedImageView imageNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note) {
            textTitle.setText(note.getTitle());
            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDatetime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (note.getColor() !=null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }
            else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if (note.getImagePath() != null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            }
            else {
                imageNote.setVisibility(View.GONE);
            }
        }

    }
    /*Đoạn code trên là một phương thức `setnote()` để thiết lập các giá trị của một ghi chú (note) lên giao diện người dùng.

Cụ thể, phương thức nhận đối tượng note và thiết lập các giá trị title, subtitle, datetime của note lên các thành phần textview trong giao diện. Nếu note không có giá trị subtitle, thì thành phần textsubtitle sẽ setvisibility view.gone.

Sau đó, phương thức thiết lập màu sắc (color) của ghi chú lên đối tượng gradientdrawable của layoutnote. Nếu ghi chú không có màu sắc được định nghĩa thì màu sắc sẽ được set là #333333.

Cuối cùng, phương thức check xem note có đường dẫn hình ảnh hay không, nếu có thì hình ảnh sẽ được đặt lên thành phần imagenote, nếu không có thì thành phần này sẽ setvisibility view.gone. */

    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    notes = notesSourse;
                }
                else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for(Note note : notesSourse){
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || note.getNotetext().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }
    public void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }

}
