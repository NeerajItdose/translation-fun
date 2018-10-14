package android.tristan.heinig.translationfun.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import android.tristan.heinig.translationfun.repository.ActiveTranslationState;
import android.tristan.heinig.translationfun.repository.TranslationRepository;
import java.util.List;

public class TranslationViewModel extends AndroidViewModel {

  private final TranslationRepository mRepository;
  private final LiveData<List<TranslationItem>> mMostRecentTranslations;
  private final LiveData<List<TranslationItem>> mMostViewedTranslations;
  private final LiveData<ActiveTranslationState> mActiveTranslation;

  public TranslationViewModel(@NonNull Application application) {
    super(application);
    mRepository = new TranslationRepository(application);
    mMostRecentTranslations = mRepository.getMostRecentTranslations();
    mMostViewedTranslations = mRepository.getMostViewedTranslations();
    mActiveTranslation = mRepository.getActiveTranslation();
  }

  public LiveData<List<TranslationItem>> getMostRecentTranslations() {
    return mMostRecentTranslations;
  }

  public LiveData<List<TranslationItem>> getMostViewedTranslations() {
    return mMostViewedTranslations;
  }

  public LiveData<ActiveTranslationState> getActiveTranslation() {
    return mActiveTranslation;
  }

  public void save(TranslationItem pTranslationItem) {
    mRepository.insert(pTranslationItem);
  }

  public void remove(TranslationItem pTranslationItem) {
    mRepository.delete(pTranslationItem);
  }

  public void restore(TranslationItem pTranslationItem) {
    mRepository.insert(pTranslationItem);
  }

  public void translate(String pText, String pSourceLngCode, String pTargetLngCode) {
    mRepository.translate(pText, pSourceLngCode, pTargetLngCode);
  }
}
