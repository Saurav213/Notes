package com.notes;

import static java.util.Locale.filter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.notes.Adapter.NotesListAdapter;
import com.notes.Database.RoomDB;
import com.notes.Model.Notes;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> noteslist=new ArrayList<>();
    RoomDB database;

    SearchView searchView_home;
    FloatingActionButton fab_add;

    Notes selectedNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView =findViewById(R.id.recycler_home);
        fab_add=findViewById(R.id.fab_add);
        searchView_home =findViewById(R.id.searchView_home);
        database = RoomDB.getInstance(this);
        noteslist = database.mainDAO().getAll();


        updateRecycler(noteslist);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,NotesTakerActivity.class);
                startActivityForResult(intent,101);

            }
        });
        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText){
        List<Notes> filteredList=new ArrayList<>();
        for(Notes singleNote:noteslist){
            if(singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())||
                    singleNote.getNotes().toLowerCase().contains(newText.toLowerCase()))
            {
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }


    private  void updateRecycler(List<Notes> notes){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter= new NotesListAdapter(MainActivity.this, notes,notesClickListener);
        recyclerView.setAdapter(notesListAdapter);

    }


    private final NotesClickListner notesClickListener=new NotesClickListner() {
        @Override
        public void onClick(Notes notes) {
            Intent intent =new Intent(MainActivity.this,NotesTakerActivity.class);
            intent.putExtra("old_notes",notes);
            startActivityForResult(intent,102);

        }
        @Override
        public void onLongClick(Notes notes, CardView cardView) {

            selectedNote =new Notes();
            selectedNote =notes;
            showPopup(cardView);

        }
    };

    private  void showPopup(CardView cardView){

        PopupMenu popupMenu=new PopupMenu(this,cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101){
            if(resultCode== Activity.RESULT_OK){
                Notes new_notes =(Notes)data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                noteslist.clear();
                noteslist.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();

            }
        } else if (requestCode==102) {
            if(resultCode== Activity.RESULT_OK){
                Notes new_notes=(Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(),new_notes.getTitle(),new_notes.getNotes());
                noteslist.clear();
                noteslist.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (R.id.pin==item.getItemId()){
            if(selectedNote.isPinned()){
                database.mainDAO().pin(selectedNote.getID(),false);
                Toast.makeText(MainActivity.this,"unpinned",Toast.LENGTH_LONG).show();
            }
            else{
                database.mainDAO().pin(selectedNote.getID(),true);
                Toast.makeText(MainActivity.this,"pinned",Toast.LENGTH_LONG).show();
            }
            noteslist.clear();
            noteslist.addAll(database.mainDAO().getAll());
            notesListAdapter.notifyDataSetChanged();
            return  true;
        }
        else if (R.id.delete==item.getItemId()) {
            database.mainDAO().delete(selectedNote);
            noteslist.remove(selectedNote);
            notesListAdapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this,"deleted successfully",Toast.LENGTH_LONG).show();
            return true;

        }
        else {
            return  false;
        }
    }

}