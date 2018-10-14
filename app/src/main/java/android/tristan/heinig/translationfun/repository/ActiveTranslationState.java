package android.tristan.heinig.translationfun.repository;

import android.tristan.heinig.translationfun.database.entity.TranslationItem;

public class ActiveTranslationState {

  private final TranslationItem mTranslationItem;
  private final STATE state;

  private ActiveTranslationState(TranslationItem pTranslationItem, STATE pState) {
    mTranslationItem = pTranslationItem;
    state = pState;
  }

  public static ActiveTranslationState saved(TranslationItem pTranslationItem) {
    return new ActiveTranslationState(pTranslationItem, STATE.SAVED);
  }

  public static ActiveTranslationState updated(TranslationItem pTranslationItem) {
    return new ActiveTranslationState(pTranslationItem, STATE.UPDATED);
  }

  public static ActiveTranslationState failed() {
    return new ActiveTranslationState(null, STATE.FAILED);
  }

  public TranslationItem getTranslationItem() {
    return mTranslationItem;
  }

  public STATE getState() {
    return state;
  }

  public enum STATE {
    SAVED, FAILED, UPDATED
  }
}
