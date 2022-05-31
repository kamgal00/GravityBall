package com.example.gravityball.utils;

import android.content.res.Resources;

import com.example.gravityball.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ResourcesUtils {
    private ResourcesUtils(){}


    public static String getStringFromId(Resources resources, int id) {
        InputStream is = resources.openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

    public static ArrayList<String> getLevels(){
        ArrayList<String> out = new ArrayList<>();
        Pattern levelPattern = Pattern.compile("level[0-9]*");

        Field[] fields= R.raw.class.getFields();
        for (Field field : fields) {
            String file = field.getName();
            if (levelPattern.matcher(file).matches()) {
                out.add(file);
            }
        }

        return out;
    }
}
