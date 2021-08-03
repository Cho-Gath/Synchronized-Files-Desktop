package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class methods {
    public static Object readObject(String filePath) throws IOException {
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object o = objectIn.readObject();
            objectIn.close();
            fileIn.close();
            return o;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeObject(String fileName, Object o) {
        try {
            FileOutputStream fileOut = new FileOutputStream("D:\\Temporary\\TestDirectory\\saves\\" + fileName);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(o);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<String>> compareFileMaps(LinkedHashMap<String, Long> newMap, LinkedHashMap<String, Long> oldMap) {
        ArrayList<ArrayList<String>> outputList = new ArrayList<>();
        for(int x = 0; x < 3; x++) {
            outputList.add(new ArrayList<>());
        }
        for(String key : newMap.keySet()) {
            if(oldMap.containsKey(key)) {
                if(!oldMap.get(key).equals(newMap.get(key))) {
                    outputList.get(1).add(key);
                }
            }
            else {
                outputList.get(0).add(key);
            }
        }
        for(String key :oldMap.keySet()) {
            if(!newMap.containsKey(key)) {
                outputList.get(2).add(key);
            }
        }
        return outputList;
    }

    public static ArrayList<ArrayList<String>> compareList(ArrayList<String> newList, ArrayList<String> oldList) {
        ArrayList<ArrayList<String>> outputList = new ArrayList<>();
        outputList.add(new ArrayList<>());
        outputList.add(new ArrayList<>());

        for(String s : newList) {
            if(!oldList.contains(s)) {
                outputList.get(0).add(s);
            }
        }
        for(String s : oldList) {
            if(!newList.contains(s)) {
                outputList.get(1).add(s);
            }
        }


        return outputList;
    }
}