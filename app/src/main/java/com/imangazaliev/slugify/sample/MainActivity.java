package com.imangazaliev.slugify.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.imangazaliev.slugify.Slugify;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Slugify slugify;
    private Slugify.Language[] languages;

    private EditText etPlainText;
    private TextView tvSlug;
    private Spinner spLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slugify = new Slugify();
        languages = Slugify.Language.values();

        etPlainText = (EditText) findViewById(R.id.etPlainText);
        tvSlug = (TextView) findViewById(R.id.tvSlug);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getLanguagesTitles());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLanguages = (Spinner) findViewById(R.id.spLanguages);
        spLanguages.setAdapter(adapter);
        spLanguages.setOnItemSelectedListener(this);
    }

    private String[] getLanguagesTitles() {
        String[] languagesTitles = new String[languages.length];
        for (int i = 0; i < languagesTitles.length; i++) {
            languagesTitles[i] = languages[i].getLangFileName();
        }
        return languagesTitles;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        slugify.setLanguageRuleSet(languages[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void slugify(View view) {
        tvSlug.setText(slugify.slugify(etPlainText.getText().toString()));
    }

}
