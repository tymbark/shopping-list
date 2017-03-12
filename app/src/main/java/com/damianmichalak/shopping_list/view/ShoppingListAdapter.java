package com.damianmichalak.shopping_list.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.shopping_list.R;
import com.damianmichalak.shopping_list.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SHOPPING_ITEM = 1;

    private final LayoutInflater layoutInflater;
    private List<ShoppingItem> items = new ArrayList<>();

    @Inject
    ShoppingListAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    private class ShoppingItemView extends RecyclerView.ViewHolder {

        final TextView text;

        public ShoppingItemView(View itemView) {
            super(itemView);
            text = ((TextView) itemView.findViewById(R.id.shopping_item_text));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_SHOPPING_ITEM;
    }

    public void update(List<ShoppingItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.shopping_item, parent, false);
        return new ShoppingItemView(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ShoppingItemView) holder).text.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}