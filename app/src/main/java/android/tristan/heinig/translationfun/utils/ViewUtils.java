package android.tristan.heinig.translationfun.utils;

import android.content.Context;
import android.tristan.heinig.translationfun.R;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUtils {

  public static View createTranslationView(LayoutInflater pLayoutInflater, ViewGroup parent, TranslationItem pTranslationItem) {
    View view = pLayoutInflater.inflate(R.layout.view_translation, parent, false);
    ((TextView) view.findViewById(R.id.tv_text)).setText(pTranslationItem.getText());
    ((TextView) view.findViewById(R.id.tv_translation)).setText(pTranslationItem.getTranslation());
    return view;
  }

  public static void showShortToast(Context pContext, int pStringResource) {
    Toast.makeText(pContext, pStringResource, Toast.LENGTH_SHORT).show();
  }

  public static void showLongToast(Context pContext, int pStringResource) {
    Toast.makeText(pContext, pStringResource, Toast.LENGTH_LONG).show();
  }
}
