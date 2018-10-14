package android.tristan.heinig.translationfun.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.tristan.heinig.translationfun.AppExecutors;
import android.tristan.heinig.translationfun.R;
import android.tristan.heinig.translationfun.database.TranslationDatabase;
import android.tristan.heinig.translationfun.database.dao.TranslationDao;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import android.tristan.heinig.translationfun.webservice.TranslationWebservice;
import android.tristan.heinig.translationfun.webservice.TranslationWebservice.SimpleCallback;
import android.tristan.heinig.translationfun.webservice.TranslationWebservice.WebserviceInitialisationCallback;
import java.util.Date;
import java.util.List;

public class TranslationRepository {

  private final AppExecutors mAppExecutors;
  private final TranslationDao mTranslationDao;
  private final TranslationWebservice mTranslationWebservice;
  private final LiveData<List<TranslationItem>> mMostRecentTranslations;
  private final LiveData<List<TranslationItem>> mMostViewedTranslations;
  private final MutableLiveData<ActiveTranslationState> mActiveTranslation = new MutableLiveData<>();
  private final MutableLiveData<Boolean> mInitialised = new MutableLiveData<>();

  public TranslationRepository(Application pApplication) {
    mInitialised.setValue(false);
    mAppExecutors = AppExecutors.getInstance();
    mTranslationDao = TranslationDatabase.getInstance(pApplication).mTranslationDao();
    mMostRecentTranslations = mTranslationDao.getAllOrderedByDate();
    mMostViewedTranslations = mTranslationDao.getAllOrderedByViews();
    mTranslationWebservice = TranslationWebservice
      .getInstance(pApplication.getString(R.string.api_key), new WebserviceInitialisationCallback() {
        @Override
        public void onInitialised() {
          mInitialised.setValue(true);
        }

        @Override
        public void onError() {
          mInitialised.setValue(false);
        }
      });
  }

  public LiveData<List<TranslationItem>> getMostViewedTranslations() {
    return mMostViewedTranslations;
  }

  public LiveData<List<TranslationItem>> getMostRecentTranslations() {
    return mMostRecentTranslations;
  }

  public LiveData<ActiveTranslationState> getActiveTranslation() {
    return mActiveTranslation;
  }

  public void getTranslationByText(final String pText, final SimpleCallback<TranslationItem> pCallback) {
    mAppExecutors.diskIO().execute(new Runnable() {
      @Override
      public void run() {
        final TranslationItem translation = mTranslationDao.getByText(pText);
        mAppExecutors.mainThread().execute(new Runnable() {
          @Override
          public void run() {
            if (translation == null) {
              pCallback.onNoResult();
            }
            else {
              pCallback.onResult(translation);
            }
          }
        });
      }
    });
  }

  public void insert(final TranslationItem pTranslationItem) {
    mAppExecutors.diskIO().execute(new Runnable() {
      @Override
      public void run() {
        mTranslationDao.insert(pTranslationItem);
      }
    });
  }

  public void delete(final TranslationItem pTranslationItem) {
    mAppExecutors.diskIO().execute(new Runnable() {
      @Override
      public void run() {
        mTranslationDao.delete(pTranslationItem.getText());
      }
    });
  }

  public void translate(final String pText, final String pSourceLngCode, final String pTargetLngCode) {
    getTranslationByText(pText, new SimpleCallback<TranslationItem>() {
      @Override
      public void onResult(TranslationItem translationItem) {
        if (null != translationItem) {
          translationItem.setViews(translationItem.getViews() + 1);
          translationItem.setDate(new Date());
          update(translationItem);
          mActiveTranslation.setValue(ActiveTranslationState.updated(translationItem));
          return;
        }
        translateByGoogle(pText, pSourceLngCode, pTargetLngCode);
      }

      @Override
      public void onNoResult() {
        translateByGoogle(pText, pSourceLngCode, pTargetLngCode);
      }
    });
  }

  private void update(final TranslationItem pTranslationItem) {
    mAppExecutors.diskIO().execute(new Runnable() {
      @Override
      public void run() {
        mTranslationDao.update(pTranslationItem);
      }
    });
  }

  private void translateByGoogle(final String pText, final String pSourceLngCode, final String pTargetLngCode) {
    if (mInitialised.getValue() != null && mInitialised.getValue()) {
      mTranslationWebservice.translate(pText, pSourceLngCode, pTargetLngCode, new SimpleCallback<String>() {
        @Override
        public void onResult(String translation) {
          if (translation != null) {
            TranslationItem translationItem = new TranslationItem(pText, translation, pSourceLngCode, pTargetLngCode);
            mActiveTranslation.setValue(ActiveTranslationState.saved(translationItem));
          }
        }

        @Override
        public void onNoResult() {
          mActiveTranslation.setValue(ActiveTranslationState.failed());
        }
      });
    }
  }
}
