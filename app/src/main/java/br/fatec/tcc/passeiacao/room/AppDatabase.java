package br.fatec.tcc.passeiacao.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import br.fatec.tcc.passeiacao.interfaces.UserModelDAO;
import br.fatec.tcc.passeiacao.model.UserModel;

@Database(entities = {UserModel.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "passeia_cao_sqlite_db")
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    /*TABELAS GERAIS*/
    public abstract UserModelDAO itemAndUserModel();

}
