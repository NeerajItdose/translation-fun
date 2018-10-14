package android.tristan.heinig.translationfun;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import android.tristan.heinig.translationfun.repository.ActiveTranslationState;
import android.tristan.heinig.translationfun.repository.ActiveTranslationState.STATE;
import android.tristan.heinig.translationfun.utils.NetworkUtils;
import android.tristan.heinig.translationfun.utils.ViewUtils;
import android.tristan.heinig.translationfun.view.TranslationFilterListAdapter;
import android.tristan.heinig.translationfun.viewmodel.TranslationViewModel;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private ViewGroup mMostRecentContainer;
  private ViewGroup mMostViewedContainer;
  private AutoCompleteTextView mSearchInput;
  private TranslationViewModel mTranslationViewModel;
  private TranslationFilterListAdapter mTranslationFilterListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mTranslationViewModel = ViewModelProviders.of(this).get(TranslationViewModel.class);
    mMostRecentContainer = (ViewGroup) findViewById(R.id.container_most_recent);
    mMostViewedContainer = (ViewGroup) findViewById(R.id.container_most_viewed);
    initSearchInput();
    subscribeToModel();
  }

  private void initSearchInput() {
    final String sourceLngCode = getString(R.string.source_lng_code);
    final String targetLngCode = getString(R.string.target_lng_code);
    mSearchInput = (AutoCompleteTextView) findViewById(R.id.input_search);
    mSearchInput.setOnEditorActionListener(new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView pTextView, int pActionId, KeyEvent pKeyEvent) {
        boolean handled = false;
        if (pActionId == EditorInfo.IME_ACTION_SEARCH) {
          String text = pTextView.getText().toString();
          if (text.isEmpty()) {
            ViewUtils.showShortToast(MainActivity.this, R.string.input_empty);
            return false;
          }
          if (!NetworkUtils.isConnected(MainActivity.this)) {
            ViewUtils.showShortToast(MainActivity.this, R.string.system_no_network);
            return false;
          }
          mTranslationViewModel.translate(text, sourceLngCode, targetLngCode);
          pTextView.setText(getString(R.string.search_input_translate));
          pTextView.setEnabled(false);
          handled = true;
        }
        return handled;
      }
    });
    mTranslationFilterListAdapter = new TranslationFilterListAdapter();
    mSearchInput.setAdapter(mTranslationFilterListAdapter);
  }

  private void subscribeToModel() {
    final int itemCount = getResources().getInteger(R.integer.card_item_count);
    subscribeToMostViewedTranslations(itemCount);
    subscribeToMostRecentTranslations(itemCount);
    subscribeToActiveTranslation();
  }

  private void subscribeToActiveTranslation() {
    mTranslationViewModel.getActiveTranslation().observe(this, new Observer<ActiveTranslationState>() {
      @Override
      public void onChanged(@Nullable final ActiveTranslationState pActiveTranslationState) {
        if (pActiveTranslationState != null && pActiveTranslationState.getState() != STATE.FAILED) {
          Builder myAlertBuilder = buildTranslationDialog(pActiveTranslationState);
          myAlertBuilder.show();
        }
        else {
          Snackbar snackbar = Snackbar.make(mSearchInput, getString(R.string.snack_translation_failed), Snackbar.LENGTH_LONG);
          snackbar.show();
        }
        mSearchInput.setEnabled(true);
        mSearchInput.setText("");
      }
    });
  }

  @NonNull
  private Builder buildTranslationDialog(@NonNull final ActiveTranslationState pPTranslationItemState) {
    Builder myAlertBuilder = new Builder(MainActivity.this);
    myAlertBuilder.setTitle(R.string.dialog_title);
    TranslationItem translationItem = pPTranslationItemState.getTranslationItem();
    String translation = translationItem.getTranslation();
    String text = translationItem.getText();
    String source = translationItem.getSource();
    String target = translationItem.getTarget();
    myAlertBuilder.setMessage(source + ": " + text + "\n\n" + target + ": " + translation);
    if (pPTranslationItemState.getState() == STATE.SAVED) {
      myAlertBuilder.setPositiveButton(R.string.dialog_btn_save, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          mTranslationViewModel.save(pPTranslationItemState.getTranslationItem());
        }
      });
    }
    myAlertBuilder.setNegativeButton(R.string.dialog_btn_back, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    return myAlertBuilder;
  }

  private void subscribeToMostRecentTranslations(final int pItemCount) {
    mTranslationViewModel.getMostRecentTranslations().observe(this, new Observer<List<TranslationItem>>() {
      @Override
      public void onChanged(@Nullable List<TranslationItem> pTranslationItems) {
        updateTranslationContainer(pTranslationItems, mMostRecentContainer, pItemCount);
        // calling for most recent translation returns the complete list of translation, which we can use for auto completion in out search input
        mTranslationFilterListAdapter.setTranslations(pTranslationItems);
      }
    });
  }

  private void subscribeToMostViewedTranslations(final int pItemCount) {
    mTranslationViewModel.getMostViewedTranslations().observe(this, new Observer<List<TranslationItem>>() {
      @Override
      public void onChanged(@Nullable List<TranslationItem> pTranslationItems) {
        updateTranslationContainer(pTranslationItems, mMostViewedContainer, pItemCount);
      }
    });
  }

  private void updateTranslationContainer(@Nullable List<TranslationItem> pTranslationItems, ViewGroup pViewGroup, int pItemCount) {
    if (pTranslationItems == null) {
      return;
    }
    pViewGroup.removeAllViews();
    int index = 0;
    while (index < pItemCount && index < pTranslationItems.size()) {
      pViewGroup.addView(ViewUtils.createTranslationView(getLayoutInflater(), pViewGroup, pTranslationItems.get(index)));
      index++;
    }
  }

  public void showRecent(View view) {
    Intent intent = new Intent(this, ListActivity.class);
    intent.putExtra(ListActivity.EXTRA_SORT_TYPE, ListActivity.SORT_TYPE_DATE);
    startActivity(intent);
  }

  public void showMostViewed(View view) {
    Intent intent = new Intent(this, ListActivity.class);
    intent.putExtra(ListActivity.EXTRA_SORT_TYPE, ListActivity.SORT_TYPE_VIEWS);
    startActivity(intent);
  }

}
