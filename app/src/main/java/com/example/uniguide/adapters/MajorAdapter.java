package com.example.uniguide.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.uniguide.R;
import com.example.uniguide.models.Major;
import java.util.List;

public class MajorAdapter extends BaseAdapter {

    private Context        context;
    private List<Major>    list;
    private LayoutInflater inflater;
    private String         color;

    public MajorAdapter(Context context, List<Major> list, String color) {
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

    private String getIcon(String name) {
        String n = name.toLowerCase();
        if (n.contains("computer") || n.contains("software")) return "💻";
        if (n.contains("medicine") || n.contains("medical"))  return "🏥";
        if (n.contains("business") || n.contains("management")) return "📊";
        if (n.contains("law"))                                 return "⚖️";
        if (n.contains("engineer"))                            return "⚙️";
        if (n.contains("architect"))                           return "🏛️";
        if (n.contains("pharmacy"))                            return "💊";
        if (n.contains("design") || n.contains("graphic"))    return "🎨";
        if (n.contains("biology") || n.contains("science"))   return "🔬";
        if (n.contains("political"))                           return "🏛️";
        return "📚";
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        Holder holder = new Holder();
        view = inflater.inflate(R.layout.row_major, null);

        holder.tvIcon     = view.findViewById(R.id.tvRowMajorIcon);
        holder.tvName     = view.findViewById(R.id.tvRowMajorName);
        holder.tvFaculty  = view.findViewById(R.id.tvRowMajorFaculty);
        holder.tvDuration = view.findViewById(R.id.tvRowMajorDuration);

        Major m = list.get(i);

        holder.tvIcon.setText(getIcon(m.getMajorName()));
        holder.tvIcon.setBackgroundColor(Color.parseColor(color));
        holder.tvName.setText(m.getMajorName());
        holder.tvFaculty.setText(m.getFaculty());
        holder.tvDuration.setText("⏱  " + m.getDuration() + " Year Program");
        holder.tvDuration.setBackgroundColor(Color.parseColor(color));

        return view;
    }

    public class Holder {
        TextView tvIcon;
        TextView tvName;
        TextView tvFaculty;
        TextView tvDuration;
    }
}
