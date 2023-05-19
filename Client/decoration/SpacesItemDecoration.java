package com.syncadapters.czar.exchange.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    @SuppressWarnings("CanBeFinal")
    private int space;
    public SpacesItemDecoration(int space){

        this.space = space;

    }

    @Override
    public void getItemOffsets(@NonNull Rect out_rect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(out_rect, view, parent, state);

        out_rect.left = space;
        out_rect.right = space;
        out_rect.bottom = space;

        if(parent.getChildLayoutPosition(view) == 0){
            out_rect.top = space;
        }else{
            out_rect.top = 0;
        }

    }
}
