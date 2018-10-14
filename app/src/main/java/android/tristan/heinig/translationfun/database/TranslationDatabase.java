package android.tristan.heinig.translationfun.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.tristan.heinig.translationfun.AppExecutors;
import android.tristan.heinig.translationfun.database.dao.TranslationDao;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import java.util.Date;

@Database(entities = {TranslationItem.class}, version = 1, exportSchema = false)
public abstract class TranslationDatabase extends RoomDatabase {

  private static final String DATABASE_NAME = "translation_database";
  private static TranslationDatabase INSTANCE;

  public static TranslationDatabase getInstance(final Context context) {
    if (INSTANCE == null) {
      synchronized (TranslationDatabase.class) {
        if (INSTANCE == null) {
          Callback callback = new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
              super.onOpen(db);
              AppExecutors.getInstance().diskIO().execute(new DataInitialisation());
            }
          };
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TranslationDatabase.class, DATABASE_NAME)
            .fallbackToDestructiveMigration().addCallback(callback).build();
        }
      }
    }
    return INSTANCE;
  }

  public abstract TranslationDao mTranslationDao();

  private static class DataInitialisation implements Runnable {

    @Override
    public void run() {
      Runnable databaseActions = new Runnable() {
        @Override
        public void run() {
          TranslationDao translationDao = INSTANCE.mTranslationDao();
          long now = System.currentTimeMillis();
          translationDao.insert(new TranslationItem("How are you?", "Wie geht es dir?", "en", "de", new Date(now - (1000 * 60 * 60)), 10));
          translationDao.insert(new TranslationItem("I am well.", "Mit geht es gut.", "en", "de", new Date(now), 1));
        }
      };
      INSTANCE.runInTransaction(databaseActions);
    }
  }
}