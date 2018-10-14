package android.tristan.heinig.translationfun.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.tristan.heinig.translationfun.R;
import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import android.tristan.heinig.translationfun.view.TranslationRecyclerViewAdapter.TranslationViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class TranslationRecyclerViewAdapter extends RecyclerView.Adapter<TranslationViewHolder> {

  private final LayoutInflater mInflater;
  private List<TranslationItem> mTranslationItems;

  public TranslationRecyclerViewAdapter(Context pContext) {
    mInflater = LayoutInflater.from(pContext);
  }

  @NonNull
  @Override
  public TranslationViewHolder onCreateViewHolder(ViewGroup pViewGroup, int pViewType) {
    View itemView = mInflater.inflate(R.layout.view_translation_card, pViewGroup, false);
    return new TranslationViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(TranslationViewHolder pViewHolder, int pPosition) {
    if (mTranslationItems != null) {
      TranslationItem current = mTranslationItems.get(pPosition);
      pViewHolder.mTextView.setText(current.getText());
      pViewHolder.mTranslationView.setText(current.getTranslation());
    }
    else {
      pViewHolder.mTextView.setText(R.string.no_translations);
    }
  }

  public List<TranslationItem> getTranslationItems() {
    return mTranslationItems;
  }

  public void setTranslationItems(List<TranslationItem> pTranslationItems) {
    mTranslationItems = pTranslationItems;
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    if (mTranslationItems != null) {
      return mTranslationItems.size();
    }
    else {
      return 0;
    }
  }

  class TranslationViewHolder extends RecyclerView.ViewHolder {

    private final TextView mTextView;
    private final TextView mTranslationView;

    private TranslationViewHolder(View pItemView) {
      super(pItemView);
      mTextView = pItemView.findViewById(R.id.tv_text);
      mTranslationView = pItemView.findViewById(R.id.tv_translation);
    }
  }
}