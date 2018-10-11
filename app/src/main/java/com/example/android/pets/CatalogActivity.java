/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    private ArrayList<Pet> mData;
    private CatalogAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mData = new ArrayList<>();
        mData.add(new Pet("Toto", "Terrier"));
        mData.add(new Pet("Binx", "Bombay"));
        mData.add(new Pet("Lady", "Cocker Spaniel"));
        mData.add(new Pet("Cat", "Tabby"));
        mData.add(new Pet("Baxter", "Border Terrier"));

        RecyclerView recyclerView = findViewById(R.id.recycler_view_pet);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        mAdapter = new CatalogAdapter(this, mData, new CatalogAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                String pet = mData.get(position).toString();
                Toast.makeText(CatalogActivity.this, pet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(int position) {
                String pet = mData.get(position).getName() + " long clicked!";
                Toast.makeText(CatalogActivity.this, pet, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPet();
            }
        });
    }

    private void addPet() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_editor, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CatalogActivity.this);
        builder.setTitle(R.string.add_pet)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView nameTextView = view.findViewById(R.id.name_edit_text);
                        TextView breedTextView = view.findViewById(R.id.breed_edit_text);
                        mData.add(new Pet(
                                nameTextView.getText().toString(),
                                breedTextView.getText().toString()
                        ));
                        mAdapter.notifyItemInserted(mData.size()-1);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
