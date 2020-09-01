package com.example.schedulednotification;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AndroidLocalFile {

    public static void writeFileOnExternalStorage(Context context, String sFileName, String sBody){
        writeFileOnLocalStorage(context.getExternalFilesDir(null), sFileName, sBody);
    }

    public static String readFileOnExternalStorage(Context context, String sFileName){
        return readFileOnLocalStorage(context.getExternalFilesDir(null), sFileName);
    }

    public static void writeFileOnInternalStorage(Context context, String sFileName, String sBody){
        writeFileOnLocalStorage(context.getFilesDir(), sFileName, sBody);
    }

    public static String readFileOnInternalStorage(Context context, String sFileName){
        return readFileOnLocalStorage(context.getFilesDir(), sFileName);
    }



    public static boolean isFileOnInternalStorageExist(Context context, String filename){
        return new File(context.getFilesDir(), filename).exists();
    }

    public static boolean isFileOnExternalStorageExist(Context context, String filename){
        return new File(context.getExternalFilesDir(null), filename).exists();
    }



    public void deleteFileOnInternalStorage(Context context, String filename){
        File file = new File(context.getFilesDir(),filename);
        file.delete();
    }

    public void deleteFileOnExternalStorage(Context context, String filename){
        File file = new File(context.getExternalFilesDir(null),filename);
        file.delete();
    }



    public static String[] getInternalListFilenames(Context context){
        return context.getFilesDir().list();
    }

    public static String[] getExternalListFilenames(Context context){
        return context.getExternalFilesDir(null).list();
    }




    private static void writeFileOnLocalStorage(File dir, String sFileName, String sBody){

        try {
            File gpxfile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String readFileOnLocalStorage(File dir, String fileName){
        StringBuilder sb = new StringBuilder();

        try {
            File file = new File(dir, fileName);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
