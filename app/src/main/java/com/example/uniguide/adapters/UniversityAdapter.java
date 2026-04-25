package com.example.uniguide.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uniguide.Config;
import com.example.uniguide.R;
import com.example.uniguide.models.University;

import java.util.List;

public class UniversityAdapter extends BaseAdapter {

    private Context context;
    private List<University> list;
    private LayoutInflater inflater;

    public UniversityAdapter(Context context, List<University> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateList(List<University> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        Holder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.row_university, parent, false);

            holder = new Holder();
            holder.viewStrip = view.findViewById(R.id.viewColorStrip);
            holder.imgLogo = view.findViewById(R.id.imgUniversityLogo);
            holder.tvName = view.findViewById(R.id.tvRowUniName);
            holder.tvCity = view.findViewById(R.id.tvRowUniCity);
            holder.tvType = view.findViewById(R.id.tvRowUniType);
            holder.tvLanguage = view.findViewById(R.id.tvRowUniLanguage);
            holder.tvHint = view.findViewById(R.id.tvRowUniHint);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        University u = list.get(i);
        String color = Config.getColor(u.getId());

        holder.tvName.setText(u.getName());

        String city = u.getCity();
        if (city != null && city.contains(",")) {
            city = city.split(",")[0];
        }
        holder.tvCity.setText("📍 " + city);

        holder.tvType.setText(
                (u.getType() == null || u.getType().trim().isEmpty())
                        ? "University"
                        : u.getType()
        );

        holder.tvLanguage.setText(
                (u.getLanguage() == null || u.getLanguage().trim().isEmpty())
                        ? "N/A"
                        : u.getLanguage()
        );

        // Keep university identity mainly in the top strip
        holder.viewStrip.setBackgroundColor(Color.parseColor(color));

        // Keep only the type chip dynamic
        holder.tvType.setBackgroundColor(Color.parseColor(color));
        holder.tvType.setTextColor(Color.WHITE);

        // Keep hint bar consistent with the dark theme
        holder.tvHint.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        holder.tvHint.setTextColor(context.getResources().getColor(R.color.colorTextSecondary));

        // Keep language chip consistent with theme
        holder.tvLanguage.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        holder.tvLanguage.setTextColor(Color.WHITE);

        // Logo handling
        int logoRes = context.getResources().getIdentifier(
                "uni_" + u.getId() + "_logo",
                "drawable",
                context.getPackageName()
        );

        if (logoRes != 0) {
            holder.imgLogo.setImageResource(logoRes);
            holder.imgLogo.setColorFilter(null);
        } else {
            holder.imgLogo.setImageResource(android.R.drawable.ic_menu_gallery);
            holder.imgLogo.setColorFilter(Color.parseColor(color));
        }

        return view;
    }

    static class Holder {
        View viewStrip;
        ImageView imgLogo;
        TextView tvName;
        TextView tvCity;
        TextView tvType;
        TextView tvLanguage;
        TextView tvHint;
    }
}