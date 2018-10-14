package android.tristan.heinig.translationfun.view;

import android.tristan.heinig.translationfun.database.entity.TranslationItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TranslationFilterListAdapter extends BaseAdapter implements Filterable {

  private List<TranslationItem> mFilteredTranslations;
  private List<TranslationItem> mAllTranslations;
  private Filter mTranslationFilter = new TranslationFilter();

  @Override
  public int getCount() {
    return mFilteredTranslations.size();
  }

  public List<TranslationItem> getFilteredTranslations() {
    return mFilteredTranslations;
  }

  public List<TranslationItem> getAllTranslations() {
    return mAllTranslations;
  }

  public void setTranslations(List<TranslationItem> pTranslationItems) {
    mAllTranslations = pTranslationItems;
    mFilteredTranslations = new ArrayList<>(mAllTranslations);
    notifyDataSetChanged();
  }

  @Override
  public TranslationItem getItem(int pPosition) {
    return mFilteredTranslations.get(pPosition);
  }

  @Override
  public long getItemId(int pPosition) {
    return pPosition;
  }

  @Override
  public View getView(int pPosition, View pView, ViewGroup pViewGroup) {
    if (pView == null) {
      pView = LayoutInflater.from(pViewGroup.getContext()).inflate(android.R.layout.simple_dropdown_item_1line, pViewGroup, false);
    }
    ((TextView) pView).setText(getItem(pPosition).getText());
    return pView;
  }

  @Override
  public Filter getFilter() {
    return mTranslationFilter;
  }

  private class TranslationFilter extends Filter {

    @Override
    public CharSequence convertResultToString(Object pResultValue) {
      return ((TranslationItem) pResultValue).getText();
    }

    @Override
    protected FilterResults performFiltering(CharSequence pCharSequence) {
      FilterResults results = new FilterResults();

      ArrayList<TranslationItem> suggestions = new ArrayList<>();
      if (pCharSequence != null) {
        for (TranslationItem customer : mAllTranslations) {
          // Note: change the "contains" to "startsWith" if you only want starting matches
          if (customer.getText().toLowerCase().contains(pCharSequence.toString().toLowerCase())) {
            suggestions.add(customer);
          }
        }
      }
      results.values = suggestions;
      results.count = suggestions.size();

      return results;
    }

    @Override
    protected void publishResults(CharSequence pCharSequence, FilterResults pFilterResults) {
      mFilteredTranslations.clear();
      if (pFilterResults != null) {
        mFilteredTranslations.addAll((ArrayList<TranslationItem>) pFilterResults.values);
      }
      notifyDataSetChanged();
    }

  }
}
