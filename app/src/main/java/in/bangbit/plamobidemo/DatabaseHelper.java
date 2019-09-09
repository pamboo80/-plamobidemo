package in.bangbit.plamobidemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import com.android.volley.Response;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database
    private static final String DATABASE_NAME = "plamobi.db";
    private static final int DATABASE_VERSION = 1;
    //Table
    private static final String MESSAGES_TABLE_NAME = "messages";
    //COLUMNS
    private static final String ID_COLUMN = "_id";
    private static final String USERNAME_COLUMN = "username";
    private static final String PHOTOURL_COLUMN = "photourl";
    private static final String LATITUDE_COLUMN = "latitude";
    private static final String LONGITUDE_COLUMN = "longitude";
    private static final String EMAIL_COLUMN = "email";
    private static final String PHONE_COLUMN = "phone";
    private static final String CHAT_COLUMN = "chat";
    private static final String MESSAGE_COLUMN = "message";
    private static final String MESSAGE_TYPE_COLUMN = "message_type";
    private static final String TIMESTAMP_COLUMN = "message_time";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ".concat(MESSAGES_TABLE_NAME).concat(" (")
                .concat(ID_COLUMN).concat(" INTEGER primary key autoincrement, ")
                .concat(USERNAME_COLUMN).concat(" TEXT, ")
                .concat(PHOTOURL_COLUMN).concat(" TEXT, ")
                .concat(LATITUDE_COLUMN).concat(" REAL, ")
                .concat(LONGITUDE_COLUMN).concat(" REAL, ")
                .concat(EMAIL_COLUMN).concat(" TEXT, ")
                .concat(PHONE_COLUMN).concat(" TEXT, ")
                .concat(CHAT_COLUMN).concat(" INTEGER, ")
                .concat(MESSAGE_COLUMN).concat(" TEXT, ")
                .concat(MESSAGE_TYPE_COLUMN).concat(" INTEGER, ")
                .concat(TIMESTAMP_COLUMN).concat(" INTEGER);"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS " + MESSAGES_TABLE_NAME);
        onCreate(db);
    }

    public void putMessages(final ArrayList<Message> messages) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = getWritableDatabase();
                for (int i = 0; i < messages.size(); i++) {
                    Message message = messages.get(i);
                    ContentValues newRow = new ContentValues();
                    newRow.put(USERNAME_COLUMN, message.getUsername());
                    newRow.put(PHOTOURL_COLUMN, message.getImgUrl());
                    newRow.put(LATITUDE_COLUMN, message.getLatLng().latitude);
                    newRow.put(LONGITUDE_COLUMN, message.getLatLng().longitude);
                    newRow.put(EMAIL_COLUMN, message.getEmail());
                    newRow.put(PHONE_COLUMN, message.getPhone());
                    newRow.put(CHAT_COLUMN, message.getChat());
                    newRow.put(MESSAGE_COLUMN, message.getText());
                    newRow.put(MESSAGE_TYPE_COLUMN, message.getMessageType());
                    newRow.put(TIMESTAMP_COLUMN, message.getMessageTimeStamp());
                    database.delete(DatabaseHelper.MESSAGES_TABLE_NAME,
                            DatabaseHelper.USERNAME_COLUMN.concat(" = ? AND ").concat(MESSAGE_COLUMN).concat(" = ?"), //TODO: change to time in production!
                            new String[]{
                                    newRow.getAsString(DatabaseHelper.USERNAME_COLUMN),
                                    newRow.getAsString(MESSAGE_COLUMN) //TODO: change to time in production!
                            }
                    );
                    database.insert(MESSAGES_TABLE_NAME, null, newRow);
                }
                database.close();
            }
        });
    }

    public void getMessages(final Response.Listener<ArrayList<Message>> listener) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<Message> messages = new ArrayList<>();
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.query(
                        MESSAGES_TABLE_NAME, new String[]{
                                USERNAME_COLUMN, PHOTOURL_COLUMN, LATITUDE_COLUMN, LONGITUDE_COLUMN, EMAIL_COLUMN, PHONE_COLUMN,
                                CHAT_COLUMN, MESSAGE_COLUMN, MESSAGE_TYPE_COLUMN, TIMESTAMP_COLUMN},
                        null, null, null, null, null
                );
                while (cursor.moveToNext()) {
                    messages.add(new Message(
                            cursor.getString(cursor.getColumnIndex(USERNAME_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(PHOTOURL_COLUMN)),
                            cursor.getDouble(cursor.getColumnIndex(LATITUDE_COLUMN)),
                            cursor.getDouble(cursor.getColumnIndex(LONGITUDE_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(EMAIL_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(PHONE_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex(CHAT_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(MESSAGE_COLUMN)),
                            cursor.getInt(cursor.getColumnIndex(MESSAGE_TYPE_COLUMN)),
                            cursor.getLong(cursor.getColumnIndex(TIMESTAMP_COLUMN))
                    ));
                }
                cursor.close();
                database.close();
                listener.onResponse(messages);
            }
        });
    }
}
