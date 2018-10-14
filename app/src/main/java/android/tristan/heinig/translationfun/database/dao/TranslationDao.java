package android.tristan.heinig.translationfun.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import java.util.List;

@Dao
public interface TranslationDao {

  @Insert
  void insert(TranslationItem pTranslationItem);

  @Update
  void update(TranslationItem pTranslationItem);

  @Query("SELECT * from translation_item WHERE text = :pText")
  TranslationItem getByText(String pText);

  @Query("SELECT * from translation_item ORDER BY text")
  LiveData<List<TranslationItem>> getAll();

  @Query("SELECT * from translation_item ORDER BY date DESC")
  LiveData<List<TranslationItem>> getAllOrderedByDate();

  @Query("SELECT * from translation_item ORDER BY views DESC")
  LiveData<List<TranslationItem>> getAllOrderedByViews();

  @Query("SELECT * from translation_item ORDER BY date DESC LIMIT :limit")
  LiveData<List<TranslationItem>> getMostRecent(int limit);

  @Query("SELECT * from translation_item ORDER BY views DESC LIMIT :limit")
  LiveData<List<TranslationItem>> getMostViewed(int limit);

  @Query("DELETE FROM translation_item WHERE text=:pText")
  void delete(String pText);

  @Query("DELETE FROM translation_item")
  void deleteAll();
}
