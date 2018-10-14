package android.tristan.heinig.translationfun.webservice;

import android.tristan.heinig.translationfun.AppExecutors;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateException;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class TranslationWebservice {

  private static TranslationWebservice INSTANCE;
  private final AppExecutors mAppExecutors;
  private Translate mPTranslateClient;
  private boolean mInitialised = false;

  private TranslationWebservice(final String pApiKey, final WebserviceInitialisationCallback pCallback) {
    mAppExecutors = AppExecutors.getInstance();
    runOnNetworkThread(new Runnable() {
      @Override
      public void run() {
        final Translate translateClient = TranslateOptions.getDefaultInstance().toBuilder().setApiKey(pApiKey).build().getService();
        runOnMainThread(new Runnable() {
          @Override
          public void run() {
            mPTranslateClient = translateClient;
            mInitialised = true;
            pCallback.onInitialised();
          }
        });
      }
    });
  }

  public static TranslationWebservice getInstance(String pApiKey, WebserviceInitialisationCallback pCallback) {
    if (INSTANCE == null) {
      INSTANCE = new TranslationWebservice(pApiKey, pCallback);
    }
    return INSTANCE;
  }

  public boolean isInitialised() {
    return mInitialised;
  }

  public void translate(final String pText, final String pSourceLngCode, final String pTargetLngCode, final SimpleCallback pCallback) {
    if (!mInitialised) {
      pCallback.onNoResult();
      return;
    }
    runOnNetworkThread(new Runnable() {
      @Override
      public void run() {
        try {
          // format has to be text, otherwise we get special chars issues
          final Translation translation = mPTranslateClient
            .translate(pText, TranslateOption.sourceLanguage(pSourceLngCode), TranslateOption.targetLanguage(pTargetLngCode),
              TranslateOption.format("text"));
          runOnMainThread(new Runnable() {
            @Override
            public void run() {
              pCallback.onResult(translation.getTranslatedText());
            }
          });
        } catch (TranslateException pException) {
          pException.printStackTrace();
          runOnMainThread(new Runnable() {
            @Override
            public void run() {
              pCallback.onNoResult();
            }
          });
        }
      }
    });
  }

  private void runOnMainThread(final Runnable pRunnable) {
    mAppExecutors.mainThread().execute(pRunnable);
  }

  private void runOnNetworkThread(final Runnable pRunnable) {
    mAppExecutors.networkIO().execute(pRunnable);
  }

  public interface SimpleCallback<T> {

    void onResult(T translation);

    void onNoResult();
  }

  public interface WebserviceInitialisationCallback {

    void onInitialised();

    void onError();
  }
}
