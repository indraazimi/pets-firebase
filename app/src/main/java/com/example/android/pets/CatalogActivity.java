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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    private ArrayList<Pet> mData;
    private CatalogAdapter mAdapter;
    private ActionMode mActionMode;
    private DatabaseReference mDatabase;

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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("daftar");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_pet);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        View emptyView = findViewById(R.id.empty_view);
        mAdapter = new CatalogAdapter(this, mData, emptyView, new CatalogAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                if (mActionMode != null) {
                    mAdapter.toggleSelection(position);
                    if (mAdapter.selectionCount() == 0)
                        mActionMode.finish();
                    else
                        mActionMode.invalidate();
                    return;
                }

                String pet = mData.get(position).toString();
                Toast.makeText(CatalogActivity.this, pet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(int position) {
                if (mActionMode != null) return false;

                mAdapter.toggleSelection(position);
                mActionMode = CatalogActivity.this.startSupportActionMode(mActionModeCallback);
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

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.catalog_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(String.valueOf(mAdapter.selectionCount()));
            menu.findItem(R.id.action_edit).setVisible(mAdapter.selectionCount() == 1);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    editPet();
                    return true;

                case R.id.action_delete:
                    deletePet();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mAdapter.resetSelection();
        }
    };

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
                        String key = mDatabase.push().getKey();
                        mDatabase.child(key).setValue(new Pet(
                                nameTextView.getText().toString(),
                                breedTextView.getText().toString())
                        );
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

    private void editPet() {
        final Pet selectedPet = mData.get(mAdapter.getSelectedId().get(0));

        View view = getLayoutInflater().inflate(R.layout.dialog_editor, null);
        final TextView nameTextView = view.findViewById(R.id.name_edit_text);
        nameTextView.setText(selectedPet.getName());
        final TextView breedTextView = view.findViewById(R.id.breed_edit_text);
        breedTextView.setText(selectedPet.getBreed());

        AlertDialog.Builder builder = new AlertDialog.Builder(CatalogActivity.this);
        builder.setTitle(R.string.edit_pet)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPet.setName(nameTextView.getText().toString());
                        selectedPet.setBreed(breedTextView.getText().toString());
                        mAdapter.notifyItemChanged(mAdapter.getSelectedId().get(0));
                        mActionMode.finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mActionMode.finish();
                    }
                });
        builder.create().show();
    }

    private void deletePet() {
        String message;
        final ArrayList<Integer> selectedIds = mAdapter.getSelectedId();
        if (selectedIds.size() == 1)
            message = getString(R.string.delete_pet);
        else {
            message = getString(R.string.delete_pets);
            Collections.sort(selectedIds, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2.compareTo(o1);
                }
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int currentPetId : selectedIds) {
                            mData.remove(currentPetId);
                        }
                        mAdapter.notifyDataSetChanged();
                        mAdapter.updateEmptyView();
                        mActionMode.finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mActionMode.finish();
                    }
                });
        builder.create().show();
    }
}
