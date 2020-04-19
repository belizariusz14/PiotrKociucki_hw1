package com.example.PiotrKociuckihw1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.PiotrKociuckihw1.contacts.ContactListContent;
import com.example.PiotrKociuckihw1.contacts.DeleteDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity  implements ContactFragment.OnListFragmentInteractionListener, CallDialog.OnCallDialogInteractionListener, DeleteDialog.OnDeleteDialogInteractionListener {


    private int currentItemPosition = -1;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.fragment_contact_add);
            }
        });
        }

    @SuppressLint("ResourceType")
    public void addClick(View view) {
        EditText contactNameEditText = findViewById(R.id.Name);
        EditText contactSurnameEditText = findViewById(R.id.Surname);
        EditText contactPhoneEditText = findViewById(R.id.Phone);
        EditText contactBirthdayEditText = findViewById(R.id.Birthday);

        String ContactName = contactNameEditText.getText().toString();
        String ContactSurname = contactSurnameEditText.getText().toString();
        String ContactPhone= contactPhoneEditText.getText().toString();
        String ContactBirthday = contactBirthdayEditText.getText().toString();
        Random generator = new Random();
        int selectedImage = generator.nextInt(14);

        if(ContactName.isEmpty()){
            ContactName = getString(R.string.default_name);
            return;}
        if(ContactSurname.isEmpty()){
            ContactSurname = getString(R.string.default_surname);
            return;
        }
        if(!isOKDate(ContactBirthday)){
            contactBirthdayEditText.setError("The date is invalid (dd/MM/yyyy)");
            return;
        }
        if(!isOKPhone(ContactPhone)){
            contactPhoneEditText.setError("The number is invalid");
            return;
        }
        ContactListContent.addItem(new ContactListContent.Contact("Contact." + ContactListContent.ITEMS.size() +1,
                ContactName,
                ContactSurname,
                ContactBirthday,
                ContactPhone,
                selectedImage));
        contactNameEditText.setText("");
        contactSurnameEditText.setText("");
        contactBirthdayEditText.setText("");
        contactPhoneEditText.setText("");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onListFragmentClickInteraction(ContactListContent.Contact contact, int position) {
        Toast.makeText(this,getString(R.string.item_selected_msg) + position,Toast.LENGTH_SHORT).show();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            displayContactInFragment(contact);
        }else{
            startSecondActivity(contact,position);
        }
    }

    @Override
    public void onListFragmentLongClickInteraction(int position) {
        showCallDialog();
    }

    public static final String contactExtra = "contactExtra";

    @SuppressLint("ResourceType")
    public void onDeleteButtonClickInteraction(int position) {
       Toast.makeText(this,getString(R.string.long_click_msg) + position, Toast.LENGTH_SHORT).show();
        showDeleteDialog();
       currentItemPosition = position ;
    }

    private void startSecondActivity(ContactListContent.Contact contact, int position){
        Intent intent = new Intent(this,ContactInfoActivity.class);
        intent.putExtra(contactExtra,contact);
        startActivity(intent);
    }

    private void displayContactInFragment(ContactListContent.Contact contact){
        ContactInfoFragment contactInfoFragment = ((ContactInfoFragment) getSupportFragmentManager().findFragmentById(R.id.displayFragment));
        if(contactInfoFragment != null){
            contactInfoFragment.displayContact(contact);
        }
    }

    private void showCallDialog(){
        CallDialog.newInstance().show(getSupportFragmentManager(),getString(R.string.call_dialog_tag));
    }

    private void showDeleteDialog(){
        DeleteDialog.newInstance().show(getSupportFragmentManager(),getString(R.string.delete_dialog_tag));
    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog){

    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog){

    }

    @Override
    public void onDeleteDialogNegativeClick(DialogFragment dialog){
        View view = findViewById(R.id.floatingActionButton);
        if(view != null){
            Snackbar.make(view,getString(R.string.delete_cancel_msg), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry_msg), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDeleteDialog();
                        }
                    }).show();
        }
    }


    @Override
    public void onDeleteDialogPositiveClick(DialogFragment dialog) {
        if(currentItemPosition != -1 && currentItemPosition < ContactListContent.ITEMS.size()){
            ContactListContent.removeItem(currentItemPosition);
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isOKPhone(String phone) {
          boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone))
        {
            if(phone.length() == 9)
            {
                check = true;
            }
        }
        return check;

    }
    public boolean isOKDate(String date)
    {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        Date test = null;
        try
        {
            test = dateformat.parse(date);
        } catch(ParseException e)
        {
            return false;
        }
        if(!dateformat.format(test).equals(date))
        {
            return false;
        }

        return true;

    }
}
