package com.example.uniguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.uniguide.R;
import com.example.uniguide.models.Book;
import java.util.List;

public class BookAdapter extends BaseAdapter {

    private Context        context;
    private List<Book>     list;
    private LayoutInflater inflater;

    public BookAdapter(Context context, List<Book> list) {
        this.context  = context;
        this.list     = list;
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
        view = inflater.inflate(R.layout.row_book, null);

        holder.imgBook   = view.findViewById(R.id.imgBook);
        holder.tvName    = view.findViewById(R.id.tvRowBookName);
        holder.tvAuthor  = view.findViewById(R.id.tvRowBookAuthor);
        holder.tvEdition = view.findViewById(R.id.tvRowBookEdition);

        Book b = list.get(i);

        holder.tvName.setText(b.getBookName());
        holder.tvAuthor.setText("✍️ " + b.getAuthor());
        holder.tvEdition.setText(b.getEdition());

        // ══════════════════════════════════════════
        // BOOK COVER PLACEHOLDER
        // After saving book_1.jpg etc. to res/drawable/:
        //
        // int bookRes = context.getResources().getIdentifier(
        //     "book_" + b.getId(),
        //     "drawable", context.getPackageName());
        // if (bookRes != 0)
        //     holder.imgBook.setImageResource(bookRes);
        // ══════════════════════════════════════════

        return view;
        
    }

    public class Holder {
        ImageView imgBook;
        TextView  tvName;
        TextView  tvAuthor;
        TextView  tvEdition;
    }
}
