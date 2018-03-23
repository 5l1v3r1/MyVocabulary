package com.myvocabulary.samet.myapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class AddWordCustomList extends BaseAdapter {

    static String[] types = {"Noun","Verb","Adjective","Pronoun","Adverb","Connective"}; //!
    LayoutInflater layoutInflater;
    static String positions;

    public AddWordCustomList(Context context){
        layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return types.length;
    }

    @Override
    public String getItem(int position) {
        return types[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position,View row, ViewGroup parent) {
        final ViewHolder holder;

        final View v = row;

        if (row == null){

            row = layoutInflater.inflate(R.layout.single_item_listview,null);
            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.typeText);
            holder.checkBox = (CheckBox) row.findViewById(R.id.check);

            row.setTag(holder);

        }else {
            holder = (ViewHolder) row.getTag();
        }

        holder.textView.setText(types[position]);

        if (MainActivity.editMode == true && checkControlForEdit(position))
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                    positions += position;
                }
                else  {
                    holder.checkBox.setChecked(false);
                    positions = positions.replaceAll(String.valueOf(position),"");
                }
            }
        });

        return row;

    }


    public boolean checkControlForEdit(int sira){

        for (int i=0; i<positions.length(); i++){

            if (sira == Character.getNumericValue(positions.charAt(i))){
                return true;
            }

        }
        return false;

    }




    public static class ViewHolder {
        private TextView textView;
        private CheckBox checkBox;

        public TextView getTextView() {
            return textView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }

}
