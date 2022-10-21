package com.company;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxAppClientV2;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import sun.awt.image.ImageWatched;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class dropboxMethods {
    public static DbxClientV2 getClient() {
        String ACCESS_TOKEN = "a token i removed";
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        return new DbxClientV2(config, ACCESS_TOKEN);
    }

    public static String formatFilePath(String filePath) {
        String formattedFilePath = "";
        String subString = filePath.substring(Main.rootDirectory.length());
        for(int x = 0; x < subString.length(); x++) {
            char c = subString.charAt(x);
            if('\\' == c) {
                formattedFilePath += '/';
            }
            else {
                formattedFilePath += c;
            }
        }
        return formattedFilePath;
    }

    public static void uploadFile(String localFilePath) {
        try {
            FileInputStream fileIn = new FileInputStream(localFilePath);
            Main.client.files().uploadBuilder(formatFilePath(localFilePath)).uploadAndFinish(fileIn);
            fileIn.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(DbxException e) {
            e.printStackTrace();
        }


    }

    public static void overwriteFile(String localFilePath) {
        try {
            FileInputStream fileIn = new FileInputStream(localFilePath);
            Main.client.files().uploadBuilder(formatFilePath(localFilePath)).withMode(WriteMode.OVERWRITE).uploadAndFinish(fileIn);
            fileIn.close();
        } catch(DbxException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePath) {
        try {
            Main.client.files().delete(formatFilePath(filePath));
        } catch(DbxException e) {

        }
    }

    public static void downloadFile(String dropboxFilePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(Main.rootDirectory + dropboxFilePath);
            Main.client.files().downloadBuilder(dropboxFilePath).download(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(DbxException e) {
            e.printStackTrace();
        }


    }

    public static void readTargetDirectory(String parentDir, ArrayList<String> dropboxDirectoryList, LinkedHashMap<String, Long> dropboxFileMap) {
        try {
            ListFolderResult result = Main.client.files().listFolder(parentDir);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    if(metadata.getClass().getName().equalsIgnoreCase("com.dropbox.core.v2.files.FolderMetadata")) {
                        String filePath = metadata.getPathDisplay();
                        dropboxDirectoryList.add(filePath);
                        readTargetDirectory(filePath, dropboxDirectoryList, dropboxFileMap);
                    }else {
                        FileMetadata fileMetadata = (FileMetadata) metadata;
                        dropboxFileMap.put(metadata.getPathDisplay(), fileMetadata.getServerModified().getTime());
                    }
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = Main.client.files().listFolderContinue(result.getCursor());
            }
        } catch(DbxException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Object> returnFilesInDirectories() {
        ArrayList<Object> outputList = new ArrayList<>();
        ArrayList<String> dropboxDirectoryList = new ArrayList<>();
        LinkedHashMap<String, Long> dropboxFileMap= new LinkedHashMap<>();
        readTargetDirectory("", dropboxDirectoryList, dropboxFileMap);
        outputList.add(dropboxDirectoryList);
        outputList.add(dropboxFileMap);
        return outputList;
    }



    public static ArrayList<Object> readDropboxFileContents() {
        String pathToSaves = "D:\\Temporary\\TestDirectory\\saves\\";
        try {
            ArrayList<Object> outputList = new ArrayList<>();
            outputList.add(methods.readObject(pathToSaves + "dropboxDirectoryList.ser"));
            outputList.add(methods.readObject(pathToSaves + "dropboxFileMap.ser"));
            return outputList;
        } catch(IOException e) {
            System.out.println("files not found for dropbox stuff");
            ArrayList<Object> outputList = returnFilesInDirectories();
            ArrayList<String> dropboxDirectoryList = (ArrayList<String>)outputList.get(0);
            LinkedHashMap<String, Long> dropboxFileMap = (LinkedHashMap<String, Long>) outputList.get(1);
            for(String s : dropboxDirectoryList) {
                File directory = new File(Main.rootDirectory + s);
                if(!directory.exists()) {
                    directory.mkdirs();
                }
            }
            for(String s : dropboxFileMap.keySet()) {
                downloadFile(s);
            }

            methods.writeObject("dropboxDirectoryList.ser", dropboxDirectoryList);
            methods.writeObject("dropboxFileMap.ser", dropboxFileMap);

            return outputList;

        }


    }



}
