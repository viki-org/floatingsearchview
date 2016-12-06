package com.arlib.floatingsearchview.suggestions;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.R;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SearchSuggestionsAdapter";

    private List<SearchSuggestion> mSearchSuggestions = new ArrayList<>();

    private Listener mListener;

    private Context mContext;

    private Drawable mRightIconDrawable;
    private boolean mShowRightIcon = false;
    private int mBodyTextSizePx;
    private int mTextColor = -1;
    private int mDividerColor = -1;

    public interface OnBindSuggestionCallback {

        void onBindSuggestion(SearchSuggestionViewHolder holder, SearchSuggestion item, int itemPosition);
    }

    private OnBindSuggestionCallback mOnBindSuggestionCallback;

    public interface Listener {

        void onItemSelected(SearchSuggestion item);

        void onRightIconClicked(SearchSuggestion item);
    }

    public static class SearchSuggestionViewHolder extends RecyclerView.ViewHolder {

        public TextView body;
        public ImageView leftIcon;
        public ImageView rightIcon;
        public View divider;

        private Listener mListener;

        public interface Listener {

            void onItemClicked(int adapterPosition);

            void onRightIconClicked(int adapterPosition);
        }

        public SearchSuggestionViewHolder(View v, Listener listener) {
            super(v);

            mListener = listener;
            body = (TextView) v.findViewById(R.id.body);
            leftIcon = (ImageView) v.findViewById(R.id.left_icon);
            rightIcon = (ImageView) v.findViewById(R.id.right_icon);
            divider = v.findViewById(R.id.divider);

            rightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int adapterPosition = getAdapterPosition();
                    if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        mListener.onRightIconClicked(getAdapterPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int adapterPosition = getAdapterPosition();
                    if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        mListener.onItemClicked(adapterPosition);
                    }
                }
            });
        }
    }

    public SearchSuggestionsAdapter(Context context, int suggestionTextSize, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mBodyTextSizePx = suggestionTextSize;
    }

    public void swapData(List<? extends SearchSuggestion> searchSuggestions) {
        mSearchSuggestions.clear();
        mSearchSuggestions.addAll(searchSuggestions);
        notifyDataSetChanged();
    }

    public List<? extends SearchSuggestion> getDataSet() {
        return mSearchSuggestions;
    }

    public void setOnBindSuggestionCallback(OnBindSuggestionCallback callback) {
        this.mOnBindSuggestionCallback = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_suggestion_item, viewGroup, false);
        SearchSuggestionViewHolder viewHolder = new SearchSuggestionViewHolder(view,
                new SearchSuggestionViewHolder.Listener() {

                    @Override
                    public void onItemClicked(int adapterPosition) {

                        if (mListener != null) {
                            mListener.onItemSelected(mSearchSuggestions.get(adapterPosition));
                        }
                    }

                    @Override
                    public void onRightIconClicked(int adapterPosition) {

                        if (mListener != null) {
                            mListener.onRightIconClicked(mSearchSuggestions
                                    .get(adapterPosition));
                        }
                    }

                });

        viewHolder.rightIcon.setImageDrawable(mRightIconDrawable);
        viewHolder.body.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBodyTextSizePx);

        if (mDividerColor != -1)
            viewHolder.divider.setBackgroundColor(mDividerColor);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

        SearchSuggestionViewHolder viewHolder = (SearchSuggestionViewHolder) vh;

        if (!mShowRightIcon) {
            viewHolder.rightIcon.setEnabled(false);
            viewHolder.rightIcon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.rightIcon.setEnabled(true);
            viewHolder.rightIcon.setVisibility(View.VISIBLE);
        }

        SearchSuggestion suggestionItem = mSearchSuggestions.get(position);
        viewHolder.body.setText(suggestionItem.getBody());

        if (mOnBindSuggestionCallback != null) {
            mOnBindSuggestionCallback.onBindSuggestion(viewHolder, suggestionItem, position);
        }
    }

    @Override
    public int getItemCount() {
        return mSearchSuggestions != null ? mSearchSuggestions.size() : 0;
    }

    public void setTextColor(int color) {

        boolean notify = false;
        if (this.mTextColor != color) {
            notify = true;
        }
        this.mTextColor = color;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void setRightIcon(int color, @DrawableRes int drawableRes) {
        mRightIconDrawable = Util.getWrappedDrawable(mContext, drawableRes);
        DrawableCompat.setTint(mRightIconDrawable, color);
        notifyDataSetChanged();
    }

    public void setDividerColor(@ColorInt int color) {

        boolean notify = false;
        if (this.mDividerColor != color) {
            notify = true;
        }
        this.mDividerColor = color;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void setShowRightIcon(boolean show) {

        boolean notify = false;
        if (this.mShowRightIcon != show) {
            notify = true;
        }
        this.mShowRightIcon = show;
        if (notify) {
            notifyDataSetChanged();
        }
    }
}
