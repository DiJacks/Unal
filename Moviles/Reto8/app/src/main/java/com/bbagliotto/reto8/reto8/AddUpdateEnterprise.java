package com.bbagliotto.reto8.reto8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddUpdateEnterprise extends AppCompatActivity {

    private static final String EXTRA_EMP_ID = "id";
    private static final String EXTRA_ADD_UPDATE = "add_update";
    private EditText nameEditText;
    private EditText urlEditText;
    private EditText phoneEditText;
    private EditText mailEditText;
    private EditText productsEditText;
    private EditText classificationEditText;
    private Button addUpdateButton;
    private CheckBox consultoriaCheckBox, desarolloCheckBox, fabricacionCheckBox;
    private Enterprise newEnterprise;
    private Enterprise oldEnterprise;
    private String mode;
    private long entId;
    private EnterpriseOperations entrepriseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_enterprise);
        newEnterprise = new Enterprise();
        oldEnterprise = new Enterprise();
        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        urlEditText = (EditText) findViewById(R.id.edit_text_url);
        phoneEditText = (EditText) findViewById(R.id.edit_text_phone);
        mailEditText = (EditText) findViewById(R.id.edit_text_mail);
        productsEditText = (EditText) findViewById(R.id.edit_text_products);
        consultoriaCheckBox = (CheckBox) findViewById(R.id.checkbox_consultoria);
        desarolloCheckBox = (CheckBox) findViewById(R.id.checkbox_desarollo);
        fabricacionCheckBox = (CheckBox) findViewById(R.id.checkbox_fabricacion);
        addUpdateButton = (Button) findViewById(R.id.button_add_update_enterprise);
        entrepriseData = new EnterpriseOperations(this);
        entrepriseData.open();


        mode = getIntent().getStringExtra(EXTRA_ADD_UPDATE);
        if (mode.equals("Update")) {

            addUpdateButton.setText("Update Enterprise");
            entId = getIntent().getLongExtra(EXTRA_EMP_ID, 0);

            initializeEnterprise(entId);

        }


        addUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mode.equals("Add")) {
                    newEnterprise.setName(nameEditText.getText().toString());
                    newEnterprise.setUrl(urlEditText.getText().toString());
                    newEnterprise.setPhone(phoneEditText.getText().toString());
                    newEnterprise.setEmail(mailEditText.getText().toString());
                    newEnterprise.setProducts(productsEditText.getText().toString());
                    newEnterprise.setClassification(getClassification());
                    entrepriseData.addEnterprise(newEnterprise);
                    Toast t = Toast.makeText(AddUpdateEnterprise.this, "Enterprise " + newEnterprise.getName() + " has been added successfully !", Toast.LENGTH_SHORT);
                    t.show();
                    Intent i = new Intent(AddUpdateEnterprise.this, MainActivity.class);
                    startActivity(i);
                } else {
                    oldEnterprise.setName(nameEditText.getText().toString());
                    oldEnterprise.setUrl(urlEditText.getText().toString());
                    oldEnterprise.setPhone(phoneEditText.getText().toString());
                    oldEnterprise.setEmail(mailEditText.getText().toString());
                    oldEnterprise.setProducts(productsEditText.getText().toString());
                    oldEnterprise.setClassification(getClassification());
                    entrepriseData.updateEnterprise(oldEnterprise);
                    Toast t = Toast.makeText(AddUpdateEnterprise.this, "Enterprise " + oldEnterprise.getName() + " has been updated successfully !", Toast.LENGTH_SHORT);
                    t.show();
                    Intent i = new Intent(AddUpdateEnterprise.this, MainActivity.class);
                    startActivity(i);

                }


            }
        });


    }

    public void onCheckboxClicked(View view) {}

    public String getClassification(){
        String classification="";
        if(consultoriaCheckBox.isChecked())
            classification+="consultoria;";
        if(desarolloCheckBox.isChecked())
            classification+="desarollo;";
        if(fabricacionCheckBox.isChecked())
            classification+="fabricacion;";
        return classification;
    }


    private void initializeEnterprise(long entId) {
        oldEnterprise = entrepriseData.getEnterprise(entId);
        nameEditText.setText(oldEnterprise.getName());
        urlEditText.setText(oldEnterprise.getUrl());
        phoneEditText.setText(oldEnterprise.getPhone());
        mailEditText.setText(oldEnterprise.getEmail());
        productsEditText.setText(oldEnterprise.getProducts());
        initializeCheckbox(oldEnterprise);
    }

    private void initializeCheckbox(Enterprise ent){
        String classif = ent.getClassification();
        if(classif.contains("consultoria"))
            consultoriaCheckBox.setChecked(true);
        if(classif.contains("desarollo"))
            desarolloCheckBox.setChecked(true);
        if(classif.contains("fabricacion"))
            fabricacionCheckBox.setChecked(true);
    }
}
