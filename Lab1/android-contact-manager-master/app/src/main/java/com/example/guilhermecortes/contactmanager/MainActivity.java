package com.example.guilhermecortes.contactmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private EditText nameTxt, phoneTxt, emailTxt, addressTxt;
    ImageView contactImageImgView;
    static List<Contact> Contacts = new ArrayList<Contact>();
    ListView contactListView;
    Uri imageURI = null;
    private static final int INVALID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int verification = SignatureVerify.checkSignature(this);
        if (verification == INVALID){
            Toast.makeText(this,"The signature of this application cannot be verified." +
                    "Warning: The data in the application might have been tampered with.",Toast.LENGTH_LONG).show();
        }

        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        emailTxt = (EditText) findViewById(R.id.txtEmail);
        addressTxt = (EditText) findViewById(R.id.txtAddress);
        contactListView = (ListView) findViewById(R.id.listView);
        contactImageImgView = (ImageView) findViewById(R.id.imgViewContactImage);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        final Button addBtn = (Button) findViewById(R.id.btnAdd);

        Intent intent = getIntent();
        String type = intent.getType();
        String action = intent.getAction();

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.equals("text/plain")) {
                String[] data = intent.getStringExtra(Intent.EXTRA_TEXT).split("---");

                try
                {
                    data[0] = AppSecurity.Decrypt(data[0]);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                if(AppSecurity.generateMac("TEST", data[0]).equals(data[1]))
                {
                    try {
                        String[] dataArray = data[0].split(",");
                        nameTxt.setText(dataArray[1]);
                        phoneTxt.setText(dataArray[2]);
                        addressTxt.setText(dataArray[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                addBtn.setEnabled(true);
            }
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contacts.add(new Contact(nameTxt.getText().toString(), phoneTxt.getText().toString(), emailTxt.getText().toString(), addressTxt.getText().toString(), imageURI));
                populateList();
                Toast.makeText(getApplicationContext(), nameTxt.getText().toString() +  " has been added to your Contacts!", Toast.LENGTH_SHORT).show();
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            //habilitar o botao se o valor do campo for diferente de vazio
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addBtn.setEnabled(!nameTxt.getText().toString().trim().isEmpty()); //trim para "cortar os espaços em branco"
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contactImageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
            }
        });

    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if (resCode == RESULT_OK){
            if (reqCode == 1){
                imageURI = data.getData();
                contactImageImgView.setImageURI(data.getData());
            }
        }
    }

    private void populateList(){
        ArrayAdapter<Contact> adapter = new ContactListAdapter();
        contactListView.setAdapter(adapter);
    }

    //add contact
//    private void addContact(String name, String phone, String email, String address){
//        Contacts.add(new Contact(name, phone, email, address));
//    }

    private class ContactListAdapter extends ArrayAdapter<Contact>{
        public ContactListAdapter(){
            super(MainActivity.this, R.layout.listview_item, Contacts);
        }

        //criar função para retornar o emelento do array
        @Override
        public View getView(int position, View view, ViewGroup parent){
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.get_name());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentContact.get_phone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentContact.get_email());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentContact.get_address());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(currentContact.get_imageURI());

            Context context = getContext();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            if (sharedPrefs.contains("Phone Number"))
                if(sharedPrefs.getBoolean("Phone Number", false))
                    phone.setText(Html.fromHtml("<a href = ''>" + currentContact.get_phone() + "</a>"));

            if (sharedPrefs.contains("Email"))
                if(sharedPrefs.getBoolean("Email", false))
                    email.setText(Html.fromHtml("<a href = ''>" + currentContact.get_email() + "</a>"));

            if (sharedPrefs.contains("Address"))
                if(sharedPrefs.getBoolean("Address", false))
                    address.setText(Html.fromHtml("<a href = ''>" + currentContact.get_address() + "</a>"));
            populateList();
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final Context context = this;
            final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.settings, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptsView);

            final CheckBox check_phone = (CheckBox)promptsView.findViewById(R.id.check_phoneNumber);
            final CheckBox check_email = (CheckBox) promptsView.findViewById(R.id.check_Email);
            final CheckBox check_address = (CheckBox) promptsView.findViewById(R.id.check_Address);

            if (sharedPrefs.contains("Phone Number"))
                check_phone.setChecked(sharedPrefs.getBoolean("Phone Number", false));

            if (sharedPrefs.contains("Email"))
                check_email.setChecked(sharedPrefs.getBoolean("Email", false));

            if (sharedPrefs.contains("Address"))
                check_address.setChecked(sharedPrefs.getBoolean("Address", false));

            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Save",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor edit = sharedPrefs.edit();
                                    edit.putBoolean("Phone Number", check_phone.isChecked()).apply();
                                    edit.putBoolean("Email", check_email.isChecked()).apply();
                                    edit.putBoolean("Address", check_address.isChecked()).apply();
                                    populateList();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
