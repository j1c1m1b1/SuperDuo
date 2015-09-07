package barqsoft.footballscores.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @see <a href="http://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview">Stack overflow answer</a>
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration
{

    private static final int VERTICAL_ITEM_SPACE = 48;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
        {
            outRect.bottom = VERTICAL_ITEM_SPACE;
        }
    }
}
