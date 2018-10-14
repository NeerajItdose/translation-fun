package android.tristan.heinig.translationfun.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.tristan.heinig.translationfun.database.DateConverter;
import java.util.Date;

@Entity(tableName = "translation_item")
@TypeConverters({DateConverter.class})
public class TranslationItem {

  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "text")
  private String mText;
  @ColumnInfo(name = "translation")
  private String mTranslation;
  @ColumnInfo(name = "source")
  private String mSource;
  @ColumnInfo(name = "target")
  private String mTarget;
  @ColumnInfo(name = "date")
  private Date mDate;
  @ColumnInfo(name = "views")
  private int mViews;

  public TranslationItem() {

  }

  public TranslationItem(@NonNull String pText, String pTranslation, String pSource, String pTarget) {
    mText = pText;
    mTranslation = pTranslation;
    mSource = pSource;
    mTarget = pTarget;
    setDate(new Date());
    setViews(1);
  }

  public TranslationItem(@NonNull String pText, String pTranslation, String pSource, String pTarget, Date pDate, int pViews) {
    mText = pText;
    mTranslation = pTranslation;
    mSource = pSource;
    mTarget = pTarget;
    mDate = pDate;
    mViews = pViews;
  }

  @NonNull
  public String getText() {
    return mText;
  }

  public void setText(@NonNull String pText) {
    mText = pText;
  }

  public String getTranslation() {
    return mTranslation;
  }

  public void setTranslation(String pTranslation) {
    mTranslation = pTranslation;
  }

  public String getSource() {
    return mSource;
  }

  public void setSource(String pSource) {
    mSource = pSource;
  }

  public String getTarget() {
    return mTarget;
  }

  public void setTarget(String pTarget) {
    mTarget = pTarget;
  }

  public Date getDate() {
    return mDate;
  }

  public void setDate(Date pDate) {
    mDate = pDate;
  }

  public int getViews() {
    return mViews;
  }

  public void setViews(int pViews) {
    mViews = pViews;
  }
}
