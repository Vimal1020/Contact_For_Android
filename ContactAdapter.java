package com.example.backendless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    Context context;
    List<Contact> contacts;

    public ContactAdapter(Context context, List<Contact> list){
        super(context,R.layout.row,list);
        this.context=context;
        this.contacts=list;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.row,parent,false);
        TextView tvchar=convertView.findViewById(R.id.firstlettertv);
        TextView tvname=convertView.findViewById(R.id.nametv);
        TextView tvmail=convertView.findViewById(R.id.emailtv);

        tvchar.setText(contacts.get(position).getName().toUpperCase().charAt(0)+"");
        tvname.setText(contacts.get(position).getName());
        tvmail.setText(contacts.get(position).getEmail());

        return convertView;
    }
}
