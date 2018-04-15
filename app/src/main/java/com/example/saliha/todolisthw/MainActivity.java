package com.example.saliha.todolisthw;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    //Global variables in the list
    EditText input;
    Button button;
    ListView list;

    //Create an ArrayList<String> where you will store your values that are entered from the EditText.
    ArrayList<String> itemList;
    //Create an ArrayAdapter<String> that will serve to connect the ArrayList to the ListView.
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.item);
        button = (Button) findViewById(R.id.addBtn);
        list = (ListView) findViewById(R.id.listView);

        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,itemList);

        //In the onClick of the "ADD ITEM" button simply add the item from the EditText to the arrayList,
        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (input.getText().length() == 0) {

                    Toast.makeText(MainActivity.this, "Please enter an item", Toast.LENGTH_SHORT).show();
                }
                else {
                    itemList.add(input.getText().toString());
                    input.setText("");
                    // and notify the listView that it should be added be calling this:
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Item added!", Toast.LENGTH_SHORT).show();

                }
            }
        };

        button.setOnClickListener(addListener);

        list.setAdapter(adapter);
        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show input box
                showInputBox(itemList.get(position), position);
            }
        });

        //Storage items into the txt file,read and write the data to a file:
        try {
            Scanner sc = new Scanner (openFileInput("ToDoList.txt"));
            while (sc.hasNextLine()){
                String data = sc.nextLine();
                adapter.add(data);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Any activity will stop recording the items, so save it in onPause () and exit
    @Override
    public void onPause() {
        super.onPause();
        try {
            PrintWriter pw = new PrintWriter(openFileOutput("ToDoList.txt", Context.MODE_PRIVATE));
            for (String data : itemList) {
                pw.println(data);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finish();
    }

    //You added items to the ListView, and display an input dialog to update the selected  item of the ListView in the showInputBox().
    public void showInputBox(String oldItem, final int index) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Edit");
        dialog.setContentView(R.layout.input_box);

        final EditText editText = (EditText) dialog.findViewById(R.id.editTxt);
        editText.setText(oldItem);
        Button bt = (Button) dialog.findViewById(R.id.editBtn);
        //In the setOnClick of the "SAVE CHANGES" button simply update the item from the EditText to the arrayList,
        //You can editing any item that is shortly touching
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.set(index, editText.getText().toString());
                // and notify the listView that it should be updated be calling this:
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Item Updated.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Delete item from the listView
    public void onCreateContextMenu(ContextMenu menu,  View v, ContextMenu.ContextMenuInfo menuInfo){
        //in the context_menu_file (in menu),select the delete option
        menu.setHeaderTitle("Remove");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_file,menu);
    }

    //Delete item from the listView according to the context_menu_file
    //You can remove any item that is long touching
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo obj = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.delete:
                itemList.remove(obj.position);
                // and notify the listView that it should be removed be calling this:
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Item Deleted.", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
