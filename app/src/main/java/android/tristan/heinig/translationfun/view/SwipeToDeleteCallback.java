package android.tristan.heinig.translationfun.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

abstract public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

  private Paint mClearPaint;
  private ColorDrawable mBackground;
  private int mBackgroundColor;
  private Drawable mDeleteDrawable;
  private int mIntrinsicWidth;
  private int mIntrinsicHeight;

  public SwipeToDeleteCallback(Drawable pDeleteDrawable, int pBackgroundColor) {
    super(0, ItemTouchHelper.LEFT);
    mBackgroundColor = pBackgroundColor;
    mDeleteDrawable = pDeleteDrawable;
    mIntrinsicWidth = mDeleteDrawable.getIntrinsicWidth();
    mIntrinsicHeight = mDeleteDrawable.getIntrinsicHeight();
    mBackground = new ColorDrawable();
    mClearPaint = new Paint();
    mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
  }

  @Override
  public boolean onMove(@NonNull RecyclerView pRecyclerView, @NonNull RecyclerView.ViewHolder pViewHolder, @NonNull RecyclerView.ViewHolder pViewHolder1) {
    return false;
  }

  @Override
  public void onChildDraw(@NonNull Canvas pCanvas, @NonNull RecyclerView pRecyclerView, @NonNull RecyclerView.ViewHolder pViewHolder, float pDX, float pDY, int pActionState, boolean pIsCurrentlyActive) {
    super.onChildDraw(pCanvas, pRecyclerView, pViewHolder, pDX, pDY, pActionState, pIsCurrentlyActive);

    View itemView = pViewHolder.itemView;
    int itemHeight = itemView.getHeight();

    boolean isCancelled = pDX == 0 && !pIsCurrentlyActive;

    if (isCancelled) {
      clearCanvas(pCanvas, itemView.getRight() + pDX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
      super.onChildDraw(pCanvas, pRecyclerView, pViewHolder, pDX, pDY, pActionState, pIsCurrentlyActive);
      return;
    }

    mBackground.setColor(mBackgroundColor);
    mBackground.setBounds(itemView.getRight() + (int) pDX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
    mBackground.draw(pCanvas);

    int deleteIconTop = itemView.getTop() + (itemHeight - mIntrinsicHeight) / 2;
    int deleteIconMargin = (itemHeight - mIntrinsicHeight) / 2;
    int deleteIconLeft = itemView.getRight() - deleteIconMargin - mIntrinsicWidth;
    int deleteIconRight = itemView.getRight() - deleteIconMargin;
    int deleteIconBottom = deleteIconTop + mIntrinsicHeight;

    mDeleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
    mDeleteDrawable.draw(pCanvas);

    super.onChildDraw(pCanvas, pRecyclerView, pViewHolder, pDX, pDY, pActionState, pIsCurrentlyActive);

  }

  private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
    c.drawRect(left, top, right, bottom, mClearPaint);
  }

  @Override
  public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder pViewHolder) {
    return 0.7f;
  }
}

