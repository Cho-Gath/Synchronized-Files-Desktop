package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class localFileMethods {
    public static void readTargetDirectory(File parentDir, ArrayList<String> localDirectoryList, LinkedHashMap<String, Long> localFileMap) {
        File[] fileList = parentDir.listFiles();
        if (fileList.length > 0) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    localDirectoryList.add(file.getAbsolutePath());
                    readTargetDirectory(file, localDirectoryList, localFileMap);
                } else if (file.isFile()) {
                    localFileMap.put(file.getAbsolutePath(), file.lastModified());
                }
            }
        }
    }

    public static ArrayList<Object> returnFilesInDirectories(String filePath) {
        ArrayList<String> localDirectoryList = new ArrayList<>();
        LinkedHashMap<String, Long> localFileMap = new LinkedHashMap<>();
        readTargetDirectory(new File(filePath), localDirectoryList, localFileMap);
        ArrayList<Object> outputList = new ArrayList<>();
        outputList.add(localDirectoryList);
        outputList.add(localFileMap);
        return outputList;
    }

    public static ArrayList<Object> readLocalFileContents() {
        String pathToSaves = "D:\\Temporary\\TestDirectory\\saves\\";
        try{
            ArrayList<Object> outputList = new ArrayList<>();
            outputList.add(methods.readObject(pathToSaves + "localDirectoryList.ser"));
            outputList.add(methods.readObject(pathToSaves + "localFileMap.ser"));
            return outputList;
        } catch(IOException e) {
            System.out.println("no files found");
            ArrayList<Object> outputList = returnFilesInDirectories("D:\\Temporary\\TestDirectory\\files");
            methods.writeObject("localDirectoryList.ser", outputList.get(0));
            methods.writeObject( "localFileMap.ser", outputList.get(1));
            return outputList;
        }
    }






}
