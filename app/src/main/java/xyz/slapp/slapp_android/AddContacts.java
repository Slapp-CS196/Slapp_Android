package xyz.slapp.slapp_android;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AddContacts extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_contacts, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void addContact(String name,String number, String email){
        try{
            ContentResolver contentResolver = this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,name);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER,number);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            contentValues.put(ContactsContract.CommonDataKinds.Email.ADDRESS,email);
            contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);

        }
        catch(Exception e){

        }
    }
}
