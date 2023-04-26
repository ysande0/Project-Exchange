package com.syncadapters.czar.exchange.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.syncadapters.czar.exchange.R;

import org.jetbrains.annotations.NotNull;

public class TosDialog extends DialogFragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_terms_of_service, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView exit_imageView = view.findViewById(R.id.tos_dialog_clear_image_imageView_id);


/*
        TextView terms_of_service_textView = view.findViewById(R.id.terms_of_service_text_view_id);
        String terms_of_service_document = getResources().getString(R.string.manaphest_terms_of_service);
        terms_of_service_textView.setText(Html.fromHtml(terms_of_service_document));
*/
        exit_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
