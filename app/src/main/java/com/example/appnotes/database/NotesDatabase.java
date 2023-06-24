package com.example.appnotes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.appnotes.dao.NoteDao;
import com.example.appnotes.entities.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase notesDatabase;

    public static synchronized NotesDatabase getDatabase(Context context){
        if (notesDatabase == null){
             notesDatabase = Room.databaseBuilder(
                    context,
                    NotesDatabase.class,
                    "notes_db"
            ).build();
        }
        return notesDatabase;
    }
    public abstract NoteDao noteDao();
}
/*Đoạn code trên định nghĩa một lớp notesdatabase được gắn với cơ sở dữ liệu Room của hệ thống android. Class này dùng để quản lý các phiên bản của cơ sở dữ liệu và cung cấp một phương thức để lấy đối tượng notesdatabase.

Lớp notesdatabase có các thuộc tính và phương thức sau:

- entities: Tham số này chỉ định các đối tượng entity có trong cơ sở dữ liệu.
- version: Tham số này chỉ định số phiên bản của cơ sở dữ liệu.
- exportschema: Tham số này chỉ ra xem có xuất khẩu cấu trúc cơ sở dữ liệu ra file schema như thế nào.

notesdatabase là một lớp trừu tượng được đánh dấu bằng @RoomDatabase. Nó có một vài phương thức trừu tượng.

- getdatabase() là một phương thức tĩnh để lấy một đối tượng notesdatabase theo context, nếu notesdatabase chưa được khởi tạo thì sẽ tạo mới nó.
- notedao(): phương thức trừu tượng trả về một đối tượng notedao.

Nội dung được tạo bởi https://GPTGO.ai
#GPTGO #chatgpt #freechatgpt #chatgptfree */