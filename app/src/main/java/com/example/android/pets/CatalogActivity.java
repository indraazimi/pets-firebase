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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ArrayList<Pet> data = new ArrayList<>();
        data.add(new Pet("Toto", "Terrier"));
        data.add(new Pet("Binx", "Bombay"));
        data.add(new Pet("Lady", "Cocker Spaniel"));
        data.add(new Pet("Cat", "Tabby"));
        data.add(new Pet("Baxter", "Border Terrier"));
        data.add(new Pet("Toto", "Terrier"));
        data.add(new Pet("Binx", "Bombay"));
        data.add(new Pet("Lady", "Cocker Spaniel"));
        data.add(new Pet("Cat", "Tabby"));
        data.add(new Pet("Baxter", "Border Terrier"));
        data.add(new Pet("Toto", "Terrier"));
        data.add(new Pet("Binx", "Bombay"));
        data.add(new Pet("Lady", "Cocker Spaniel"));
        data.add(new Pet("Cat", "Tabby"));
        data.add(new Pet("Baxter", "Border Terrier"));

        RecyclerView recyclerView = findViewById(R.id.recycler_view_pet);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        CatalogAdapter adapter = new CatalogAdapter(this, data);
        recyclerView.setAdapter(adapter);
    }
}
