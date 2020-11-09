package com.github.warren_bank.locale_changer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Locale[] all_avail = Locale.getAvailableLocales();
    Locale   def_value = Locale.getDefault();
    Locale[] new_array = new Locale[all_avail.length + 1];
    new_array[0] = def_value;
    System.arraycopy(all_avail, 0, new_array, 1, all_avail.length);
    all_avail = null;
    def_value = null;

    ArrayAdapter adapter = new ArrayAdapter<Locale>(this, R.layout.locale_listitem, new_array);

    ListView listView = (ListView) findViewById(R.id.locale_list);
    listView.setAdapter(adapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
        Locale locale = (Locale) adapter.getItem(position);
        changeLocale(locale);
      }
    });
  }

  private void changeLocale(Locale locale) {
    if (locale == null) return;

    try {
      setLocale(locale);
    }
    catch (Exception e) {
      Log.e(getPackageName(), "Error changing locale to " + locale.toString(), e);
    }
  }

  private static void setLocale(Locale paramLocale) throws Exception {
    Class localClass = Class.forName("android.app.ActivityManagerNative");
    Object localObject = localClass.getMethod("getDefault", null).invoke(localClass, null);
    Configuration localConfiguration = (Configuration)localObject.getClass().getMethod("getConfiguration", null).invoke(localObject, null);
    localConfiguration.locale = paramLocale;
    setUserLocale(localConfiguration, true);
    localObject.getClass().getMethod("updateConfiguration", new Class[] { Configuration.class }).invoke(localObject, new Object[] { localConfiguration });
  }

  private static void setUserLocale(Configuration paramConfiguration, boolean paramBoolean) throws Exception {
    Class localClass = paramConfiguration.getClass();
    localClass.getField("userSetLocale").set(paramConfiguration, new Boolean(paramBoolean));
  }

}
