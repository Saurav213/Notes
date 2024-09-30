package com.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.notes.Model.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {

    EditText editText_title,editText_notes;
    ImageView imageView_save;
    Notes notes;
    boolean isOldNote=false;
    Button dialog_save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        editText_notes=findViewById(R.id.editText_notes);
        editText_title=findViewById(R.id.editText_tilte);
        imageView_save=findViewById(R.id.imageView_save);

        notes=new Notes();

        Dialog dialog=new Dialog(NotesTakerActivity.this);
        notes=(Notes) getIntent().getSerializableExtra("old_notes");

        if (notes!=null){
            editText_notes.setText(notes.getNotes());
            editText_title.setText(notes.getTitle());
            isOldNote=true;
        }

        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setContentView(R.layout.show_dialog_box);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                String title=editText_title.getText().toString();
                String description=editText_notes.getText().toString();

                if (description.isEmpty()){
                    Toast.makeText(NotesTakerActivity.this,"Pls Add Some Notes",Toast.LENGTH_LONG).show();
                    return;
                }
                else if (title.isEmpty()) {
                    Toast.makeText(NotesTakerActivity.this,"Pls Add some Titles",Toast.LENGTH_LONG).show();
                    return;

                }
                else {
                    dialog.show();
                }
                dialog_save_btn=dialog.findViewById(R.id.save_notes_dialog);

                dialog_save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        SimpleDateFormat fomatter=new SimpleDateFormat("EEEE,d MMM yyyy HH:mm:ss   a");
                        Date date=new Date();


                        if (!isOldNote){
                            notes=new Notes();
                        }


                        notes.setTitle(title);
                        notes.setNotes(description);
                        notes.setDate(fomatter.format(date));

                        Intent intent=new Intent();
                        intent.putExtra("note",notes);

                        setResult(Activity.RESULT_OK,intent);
                        Toast.makeText(NotesTakerActivity.this,"Added Successfully",Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });

            }
        });

    }
}