package com.example.uniguide.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.uniguide.R;
import com.example.uniguide.models.Dorm;
import java.util.List;

public class DormAdapter extends BaseAdapter {

    private Context        context;
    private List<Dorm>     list;
    private LayoutInflater inflater;
    private String         color;

    public DormAdapter(Context context, List<Dorm> list, String color) {
        this.context  = context;
        this.list     = list;
        this.color    = color;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int i) { return list.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        Holder holder = new Holder();
        view = inflater.inflate(R.layout.row_dorm, null);

        holder.viewAccent = view.findViewById(R.id.viewDormAccent);
        holder.imgDorm    = view.findViewById(R.id.imgDorm);
        holder.tvName     = view.findViewById(R.id.tvRowDormName);
        holder.tvLocation = view.findViewById(R.id.tvRowDormLocation);
        holder.tvPrice    = view.findViewById(R.id.tvRowDormPrice);
        holder.tvDesc     = view.findViewById(R.id.tvRowDormDescription);

        Dorm d = list.get(i);

        holder.viewAccent.setBackgroundColor(Color.parseColor(color));
        holder.tvName.setText(d.getDormName());
        holder.tvLocation.setText("📍 " + d.getLocation());
        holder.tvPrice.setText("💰 " + d.getPriceRange());
        holder.tvDesc.setText(d.getDescription());

        // ══════════════════════════════════════════
        // DORM PHOTO PLACEHOLDER
        // After saving dorm_1.jpg etc. to res/drawable/:
        //
        // int dormRes = context.getResources().getIdentifier(
        //     "dorm_" + d.getId(),
        //     "drawable", context.getPackageName());
        // if (dormRes != 0) {
        //     holder.imgDorm.setVisibility(View.VISIBLE);
        //     holder.imgDorm.setImageResource(dormRes);
        // }
        // ══════════════════════════════════════════

        return view;
    }

    public class Holder {
        View      viewAccent;
        ImageView imgDorm;
        TextView  tvName;
        TextView  tvLocation;
        TextView  tvPrice;
        TextView  tvDesc;
    }
}
