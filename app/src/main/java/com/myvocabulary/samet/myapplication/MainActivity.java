package com.myvocabulary.samet.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    static boolean editMode = false;
    ActionMode mActionMode;
    String word; // silinecek kelime
    ArrayAdapter adapterLw;
    static List<String> wordList = new ArrayList<>();
    private AdView adView;

    RelativeLayout rl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reklamiYukle();

        Database db = new Database(getApplicationContext());

        rl = (RelativeLayout) findViewById(R.id.relative);

        wordList = db.fetchAllWordName();
        adapterLw = new ArrayAdapter<String>(
                this,
                R.layout.textview_customtype,
                wordList
        );
        final ListView lw = (ListView) findViewById(R.id.listview);

        lw.setAdapter(adapterLw);
        registerForContextMenu(lw);
        lw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null)
                    return false;

                mActionMode = MainActivity.this.startActionMode(mActionModeCallBack);
                word = (lw.getItemAtPosition(position).toString());
                return true;

            }
        });

        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Database db = new Database(getApplicationContext());
                writeToRelative(db.retrieveData(parent.getItemAtPosition(position).toString()));
            }
        });

        //~~~~~~~~~~~~~~~~About quiz section~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        Button openExam = (Button) findViewById(R.id.openExam);

        openExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Do you want to exercise?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (wordList.size() < 4)
                                    Toast.makeText(MainActivity.this,"At least 4 words you must have",Toast.LENGTH_LONG).show();
                                else {
                                    startActivity(new Intent(MainActivity.this, quiz.class));


                                }
                                dialog.cancel();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });


    }

    private void reklamiYukle() {
        adView = new AdView(this);
        adView.setAdSize(new AdSize(250,50));
        adView.setAdUnitId(getString(R.string.reklam_kimligi));

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.reklam);
        layout.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
    }

    public void updateData(List<String> wordList){
        adapterLw = new ArrayAdapter<String>(
                this,
                R.layout.textview_customtype,
                wordList
        );

        ListView lw = (ListView) findViewById(R.id.listview);
        adapterLw.notifyDataSetChanged();
        lw.setAdapter(adapterLw);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adapterLw.getFilter().filter("");
                    updateData(wordList);

                } else {
                    adapterLw.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                AddWordCustomList.positions="";
                editMode = false;
                showDialog();
                return true;
            case R.id.totalWord:
                Toast.makeText(getApplicationContext(),"You have " + wordList.size() + " words",Toast.LENGTH_LONG).show();
                return true;
            case R.id.backup:
                backup();
                return true;
            case R.id.Import:
                Import();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                return true;
            case R.id.exit:
                this.finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Import() {

        try {
            OutputStream myOutput;

            String dbpath = "/data/" + getPackageName() + "/databases/DATABASE_WORD";
            String sdpath = Environment.getExternalStorageDirectory().getPath();

            myOutput = new FileOutputStream(Environment.getDataDirectory() + dbpath);

            File directory = new File(sdpath + "/BackupVocabulary");

            InputStream myInputs = new FileInputStream(directory.getPath() + "/DATABASE_WORDS");

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInputs.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close and clear the streams
            myOutput.flush();

            myOutput.close();

            myInputs.close();

            Toast.makeText(getApplicationContext(), "Import Done Succesfully!", Toast.LENGTH_LONG).show();
        }catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private void backup() {

        InputStream myInput;
        String dbpath = "/data/"+getPackageName()+"/databases/DATABASE_WORD";
        String sdpath = Environment.getExternalStorageDirectory().getPath();

        try {
            myInput = new FileInputStream(Environment.getDataDirectory()+dbpath);

            File directory = new File(sdpath,"/BackupVocabulary");
            if (!directory.exists())
                directory.mkdirs();

            OutputStream myOutput = new FileOutputStream(directory.getPath()+"/DATABASE_WORDS");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();

            myOutput.close();

            myInput.close();

            Toast.makeText(getApplicationContext(), "Backup Done Succesfully!", Toast.LENGTH_LONG)
                    .show();
        }catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e, Toast.LENGTH_LONG).show();

            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e, Toast.LENGTH_LONG).show();

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private ActionMode.Callback mActionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextmenu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.delete:
                    Database db = new Database(getApplicationContext());
                    db.deleteWord(word);
                    adapterLw.remove(word.toString());
                    adapterLw.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),word + " has been successfully deleted. ",Toast.LENGTH_SHORT).show();
                    mode.finish();
                    refleshTextViews();
                    return true;
                case R.id.edit:
                    editMode = true;
                    editDialog(word);
                    mode.finish();
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    public void editDialog(final String wordName){

        View view = getLayoutInflater().inflate(R.layout.activity_listele, null);
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Edit word");
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText equivalent = (EditText) view.findViewById(R.id.equivalent);
        final EditText example = (EditText) view.findViewById(R.id.example);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button update = (Button) view.findViewById(R.id.add);
        update.setText("Update");

        final AddWordCustomList cad = new AddWordCustomList(MainActivity.this);
        final Database db = new Database(getApplicationContext());

        Word word = db.retrieveData(wordName);

        name.setText(word.getName());
        equivalent.setText(word.getEquivalent());


        cad.positions = word.getType();


        example.setText(word.getExample());
        ListView listView = (ListView) view.findViewById(R.id.typeList);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word(
                        name.getText().toString(),
                        equivalent.getText().toString(),
                        AddWordCustomList.positions,
                        example.getText().toString()
                );

                if (isConvenient(word.getName(),word.getEquivalent(),word.getType())){
                    db.update(word,wordName);

                    for (int i = 0 ; i < wordList.size(); i++){
                        if (wordList.get(i).equals(wordName))
                            wordList.set(i,word.getName());
                    }
                    adapterLw.notifyDataSetChanged();
                    //
                    refleshTextViews();
                    dialog.dismiss();
                    writeToRelative(word);
                }
                else
                    Toast.makeText(getApplicationContext(),"You must be entered name, usage and type fields.",Toast.LENGTH_LONG).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        listView.setAdapter(cad);
        dialog.setContentView(view);
        dialog.show();

    }
    public void showDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Add word");
        final View view = getLayoutInflater().inflate(R.layout.activity_listele, null);
        final ListView listView = (ListView) view.findViewById(R.id.typeList);
        AddWordCustomList cad = new AddWordCustomList(MainActivity.this);
        listView.setAdapter(cad);

        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button add = (Button) view.findViewById(R.id.add);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) view.findViewById(R.id.name);
                EditText equivalent = (EditText) view.findViewById(R.id.equivalent);
                // positions dan çekiyorum onu
                EditText example = (EditText) view.findViewById(R.id.example);

                Word word = new Word(name.getText().toString(),
                        equivalent.getText().toString(),
                        AddWordCustomList.positions,
                        example.getText().toString());

                if (isConvenient(word.getName(),word.getEquivalent(),word.getType())) {
                    Database db = new Database(getApplicationContext());
                    db.addWord(word);
                    adapterLw.add(word.getName().toString());
                    adapterLw.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else
                    Toast.makeText(getApplicationContext(),"You must be entered name, usage and type fields.",Toast.LENGTH_LONG).show();
            }
        });

        dialog.setContentView(view);
        dialog.show();

    }
    public boolean isConvenient(String name, String usage, String type){
        if (name.equals("") || usage.equals("") || type.equals(""))
            return false;
        else return true;
    }
    public void refleshTextViews(){
        for (int i = 0; i < rl.getChildCount(); i++){
            if (rl.getChildAt(i).getId() != R.id.reklam) {
                TextView tv = (TextView) findViewById(rl.getChildAt(i).getId());
                tv.setText("");
            }
        }
    }
    public void writeToRelative(Word writeWord){
        TextView tvName = (TextView) findViewById(R.id.textName);
        TextView tvType = (TextView) findViewById(R.id.textType);
        TextView tvUsage = (TextView) findViewById(R.id.textUsage);
        TextView tvExample = (TextView) findViewById(R.id.textExample);

        TextView titleTypes = (TextView) findViewById(R.id.titleTypes);
        TextView titleUsages = (TextView) findViewById(R.id.titleUsage);
        TextView titleExamples = (TextView) findViewById(R.id.titleExample);

        refleshTextViews();

        tvName.setText(writeWord.getName());

        String type = "";
        for (int i = 0; i < writeWord.getType().length(); i++) {

            for (int j = 0; j < AddWordCustomList.types.length; j++) {
                if (Character.getNumericValue(writeWord.getType().charAt(i)) == j) {
                    type += (i < writeWord.getType().length() - 1) ?
                            (AddWordCustomList.types[j] + ", ") :
                            AddWordCustomList.types[j];
                }
            }

        }
        titleTypes.setText((writeWord.getType().length() > 1) ?
                "[ Types: ]" : "[ Type: ]");
        tvType.setText(type);

        titleUsages.setText("[ Usages: ]");
        tvUsage.setText(writeWord.getEquivalent());

        if (!writeWord.getExample().equals("")){
            titleExamples.setText("[ Example: ]");
            tvExample.setText(writeWord.getExample());
        }

    }

}
