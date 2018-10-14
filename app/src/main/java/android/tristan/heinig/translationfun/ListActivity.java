package android.tristan.heinig.translationfun;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import android.tristan.heinig.translationfun.view.SwipeToDeleteCallback;
import android.tristan.heinig.translationfun.view.TranslationRecyclerViewAdapter;
import android.tristan.heinig.translationfun.viewmodel.TranslationViewModel;
import android.view.View;
import java.util.List;

public class ListActivity extends AppCompatActivity {

  public static final String EXTRA_SORT_TYPE = "extra.order";
  public static final int SORT_TYPE_DATE = 0;
  public static final int SORT_TYPE_VIEWS = 1;
  private RecyclerView mRecyclerView;
  private TranslationRecyclerViewAdapter mAdapter;
  private TranslationViewModel mTranslationViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);

    mAdapter = new TranslationRecyclerViewAdapter(this);
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    int backgroundColor = ContextCompat.getColor(this, R.color.colorAccent);
    Drawable deleteDrawable = ContextCompat.getDrawable(this, R.drawable.ic_delete);
    Callback swipeToDeleteCallback = new SwipeToDeleteCallback(deleteDrawable, backgroundColor) {
      @Override
      public void onSwiped(@NonNull ViewHolder pViewHolder, int direction) {
        final int position = pViewHolder.getAdapterPosition();
        TranslationItem item = mAdapter.getTranslationItems().get(position);
        mTranslationViewModel.remove(item);
        showUndoSnackbar(pViewHolder, position, item);
      }
    };
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
    itemTouchHelper.attachToRecyclerView(mRecyclerView);

    mTranslationViewModel = ViewModelProviders.of(this).get(TranslationViewModel.class);
    subscribeToModel(mTranslationViewModel, getIntent().getIntExtra(EXTRA_SORT_TYPE, SORT_TYPE_DATE));
  }

  private void showUndoSnackbar(@NonNull ViewHolder pViewHolder, final int pPosition, final TranslationItem pItem) {
    Snackbar snackbar = Snackbar.make(pViewHolder.itemView, getString(R.string.snack_translation_item_removed), Snackbar.LENGTH_LONG);
    snackbar.setAction(R.string.snack_undo, new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mTranslationViewModel.restore(pItem);
        mRecyclerView.scrollToPosition(pPosition);
      }
    });

    snackbar.setActionTextColor(Color.YELLOW);
    snackbar.show();
  }

  private void subscribeToModel(TranslationViewModel pTranslationViewModel, int pSortTyoe) {
    Observer<List<TranslationItem>> observer = new Observer<List<TranslationItem>>() {
      @Override
      public void onChanged(@Nullable List<TranslationItem> pTranslationItems) {
        mAdapter.setTranslationItems(pTranslationItems);
      }
    };
    if (pSortTyoe == SORT_TYPE_DATE) {
      pTranslationViewModel.getMostRecentTranslations().observe(this, observer);
      setTitle(R.string.label_most_recent);
    }
    else {
      pTranslationViewModel.getMostViewedTranslations().observe(this, observer);
      setTitle(R.string.label_most_viewed);
    }
  }

}
