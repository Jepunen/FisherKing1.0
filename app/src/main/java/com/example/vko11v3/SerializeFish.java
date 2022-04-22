package com.example.vko11v3;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SerializeFish {

    //Fish f1 = new Fish("Särki", 2.2, 3.3, String.valueOf(61.011333), String.valueOf(25.614806));
    //Fish f2 = new Fish("Hauki", 5.5, 3.3, String.valueOf(61.011333), String.valueOf(25.614806));
    //Fish f3 = new Fish("Ahven", 4.4, 3.3, String.valueOf(61.011333), String.valueOf(25.614806));

    //ArrayList<Fish> fList = new ArrayList<Fish>();

    //public SerializeFish(ArrayList<Fish> fList) {
    public static final SerializeFish instance = new SerializeFish();

    public void serializeData(Context c, String fileName, Object data) {

        try {
            FileOutputStream fout = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oOut = new ObjectOutputStream(fout);
            oOut.writeObject(data);
            oOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T> T deSerializeData(Context c, String fileName) {

        try {
            FileInputStream fIn = c.openFileInput(fileName);
            ObjectInputStream oIn = new ObjectInputStream(fIn);
            T data = (T) oIn.readObject();
            oIn.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

 //       ArrayList<Fish> fisut = new ArrayList<>();
        //Fish f4 = new Fish("Vonkale", 9.9, 4.4, String.valueOf(61.011333), String.valueOf(25.614806));
        //fisut.add(f4);



/*
        for (Fish f : fisut) {
            System.out.println(f.title + ": " + f.weight);
        }
    }
*/

    /*private void addFish(ArrayList<Fish> fList) {

        //fList.add(f1);
        //fList.add(f2);
        //fList.add(f3);

        try {
            FileOutputStream fout = new FileOutputStream("Fisut");
            ObjectOutputStream oOut = new ObjectOutputStream(fout);
            oOut.writeObject(fList);
            oOut.close();
        } catch (Exception e) {
        }

        ArrayList<Fish> fisut = null;

        try {
            FileInputStream fIn = new FileInputStream("Fisut");
            ObjectInputStream oIn = new ObjectInputStream(fIn);
            fisut = (ArrayList<Fish>) oIn.readObject();
            oIn.close();
        } catch (Exception e) {
        }

        for (Fish f : fisut) {
            System.out.println(f.title + ": " + f.weight);
        }*/



/*
import java.io.Serializable;

public class Fish implements Serializable {
    String name;
    float weigh;

    public Fish(String name, float weight) {
        this.name = name;
        this.weigh = weight;
    }
}
*/