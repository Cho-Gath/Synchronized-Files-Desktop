package com.company;


import java.io.*;
import java.util.*;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxAppClientV2;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;


public class Main {
    public static String rootDirectory = "D:/Temporary/TestDirectory/files";
    public static DbxClientV2 client = dropboxMethods.getClient();

    public static void main(String[] args) throws InterruptedException{
        Scanner scanner = new Scanner(System.in);

        ArrayList<Object> readLocalFileContents = localFileMethods.readLocalFileContents();
        ArrayList<String> oldLocalDirectoryList = (ArrayList<String>) readLocalFileContents.get(0);
        LinkedHashMap<String, Long> oldLocalFileMap = (LinkedHashMap<String, Long>) readLocalFileContents.get(1);

        ArrayList<Object> readDropboxFileContents = dropboxMethods.readDropboxFileContents();
        ArrayList<String> oldDropboxDirectoryList = (ArrayList<String>) readDropboxFileContents.get(0);
        LinkedHashMap<String, Long> oldDropboxFileMap = (LinkedHashMap<String, Long>) readDropboxFileContents.get(1);

        boolean localFileChange;
        boolean localDirectoryChange;

        boolean dropboxFileChange;
        boolean dropboxDirectoryChange;

        while (true) {
            dropboxFileChange = false;
            dropboxDirectoryChange = false;
            localFileChange = false;
            localDirectoryChange = false;


            //Local File process


            ArrayList<Object> newLocalFileContents = localFileMethods.returnFilesInDirectories("D:\\Temporary\\TestDirectory\\files");
            ArrayList<String> newLocalDirectoryList = (ArrayList<String>) newLocalFileContents.get(0);
            LinkedHashMap<String, Long> newLocalFileMap = (LinkedHashMap<String, Long>) newLocalFileContents.get(1);


            //LOCAL DIRECTORY STUFF
            System.out.println("Local Directory stuff");

            ArrayList<ArrayList<String>> localDirectoryListComparison = methods.compareList(newLocalDirectoryList, oldLocalDirectoryList);

            //added directories
            System.out.println(localDirectoryListComparison.get(0));
            for(String s : localDirectoryListComparison.get(0)) {
                try {
                    String formattedPath = dropboxMethods.formatFilePath(s);
                    client.files().createFolder(formattedPath);
                    oldDropboxDirectoryList.add(formattedPath);
                    dropboxDirectoryChange = true;
                } catch(DbxException e) {
                    e.printStackTrace();
                }
            }
            //deleted directories
            System.out.println(localDirectoryListComparison.get(1));
            for(String s : localDirectoryListComparison.get(1)) {
                try {
                    String formattedPath = dropboxMethods.formatFilePath(s);
                    client.files().delete(formattedPath);
                    oldDropboxDirectoryList.remove(formattedPath);
                    dropboxDirectoryChange = true;
                } catch (DbxException e) {
                }
            }


            //LOCAL FILE MAP STUFF
            System.out.println("Local File stuff:");
            ArrayList<ArrayList<String>> localFileMapComparison = methods.compareFileMaps(newLocalFileMap, oldLocalFileMap);

            //added local files
            System.out.println(localFileMapComparison.get(0));
            for(String s : localFileMapComparison.get(0)) {
                dropboxMethods.uploadFile(s);
                try {
                    FileMetadata fileMetadata = (FileMetadata) client.files().getMetadata(dropboxMethods.formatFilePath(s));
                    oldDropboxFileMap.put(fileMetadata.getPathDisplay(), fileMetadata.getServerModified().getTime());
                    dropboxFileChange = true;
                } catch (DbxException e) {
                    e.printStackTrace();
                }

            }

            //edited local files
            System.out.println(localFileMapComparison.get(1));
            for(String s : localFileMapComparison.get(1)) {
                try {
                    dropboxMethods.overwriteFile(s);
                    String dropboxFilePath = dropboxMethods.formatFilePath(s);
                    FileMetadata fileMetadata = (FileMetadata) client.files().getMetadata(dropboxFilePath);
                    oldDropboxFileMap.put(dropboxFilePath, fileMetadata.getServerModified().getTime());
                    dropboxFileChange = true;
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }

            //deleted local files
            System.out.println(localFileMapComparison.get(2));
            for(String s : localFileMapComparison.get(2)) {
                dropboxMethods.deleteFile(s);
                oldDropboxFileMap.remove(dropboxMethods.formatFilePath(s));
                dropboxFileChange = true;
            }

            //DROPBOX STUFF

            ArrayList<Object> newDropboxFileContents = dropboxMethods.returnFilesInDirectories();
            ArrayList<String> newDropboxDirectoryList =(ArrayList<String>) newDropboxFileContents.get(0);
            LinkedHashMap<String, Long> newDropboxFileMap = (LinkedHashMap<String, Long>) newDropboxFileContents.get(1);

            //Dropbox directory stuff
            ArrayList<ArrayList<String>> dropboxDirectoryListComparison = methods.compareList(newDropboxDirectoryList, oldDropboxDirectoryList);
            System.out.println("Dropbox Directory stuff");
            System.out.println(dropboxDirectoryListComparison.get(0));
            for(String s : dropboxDirectoryListComparison.get(0)) {
                File f = new File(rootDirectory + s);
                if(!f.exists()) {
                    f.mkdirs();
                }
                newLocalDirectoryList.add(f.getAbsolutePath());
                localDirectoryChange = true;
            }

            //deleted dropbox directories
            int x = 0;
            System.out.println(dropboxDirectoryListComparison.get(1));
            for(String s : dropboxDirectoryListComparison.get(1)) {
                File f = new File(rootDirectory + s);
                System.out.println("Directory to be deleted: " + f.getAbsolutePath());
                newLocalDirectoryList.remove(f.getAbsolutePath());
                f.delete();
                localDirectoryChange = true;
                x++;
            }
            System.out.println(x);
            //Dropbox file process
            System.out.println("Dropbox file stuff:");
            ArrayList<ArrayList<String>> dropboxFileMapComparison = methods.compareFileMaps(newDropboxFileMap, oldDropboxFileMap);

            //added files
            System.out.println(dropboxFileMapComparison.get(0));
            for(String s : dropboxFileMapComparison.get(0)) {
                dropboxMethods.downloadFile(s);
                File f = new File(rootDirectory + s);
                newLocalFileMap.put(f.getAbsolutePath(), f.lastModified());
                localFileChange = true;
            }

            //edited files
            System.out.println(dropboxFileMapComparison.get(1));
            for(String s : dropboxFileMapComparison.get(1)) {
                dropboxMethods.downloadFile(s);
                File f = new File(rootDirectory + s);
                newLocalFileMap.put(f.getAbsolutePath(), f.lastModified());
                localFileChange = true;
            }

            //deleted files
            System.out.println(dropboxFileMapComparison.get(2));
            ArrayList<String> poggers = dropboxFileMapComparison.get(2);
            Collections.reverse(poggers);
            for(String s : poggers) {
                File f = new File(rootDirectory + s);
                f.delete();
                newLocalFileMap.remove(f.getAbsolutePath());
                localFileChange = true;
            }

            //end process
            for(ArrayList<String> list : localDirectoryListComparison) {
                if(list.size() > 0) {
                    localDirectoryChange = true;
                    break;
                }
            }
            
            for(ArrayList<String> list : localFileMapComparison) {
                if(list.size() > 0) {
                    localFileChange = true;
                    break;
                }
            }
            
            for(ArrayList<String> list : dropboxDirectoryListComparison) {
                if(list.size() > 0) {
                    dropboxDirectoryChange = true;
                    break;
                }
            }

            for(ArrayList<String> list : dropboxFileMapComparison) {
                if(list.size() > 0) {
                    dropboxFileChange = true;
                    break;
                }

            }

            oldLocalFileMap = new LinkedHashMap<>(newLocalFileMap);
            oldLocalDirectoryList = new ArrayList<>(newLocalDirectoryList);

            oldDropboxFileMap = new LinkedHashMap<>(newDropboxFileMap);
            oldDropboxDirectoryList = new ArrayList<>(newDropboxDirectoryList);

            if(localFileChange) {
                methods.writeObject("localFileMap.ser", oldLocalFileMap);
            }

            if(localDirectoryChange) {
                methods.writeObject("localDirectoryList.ser", oldLocalDirectoryList);
            }

            if(dropboxDirectoryChange) {
                methods.writeObject("dropboxDirectoryList.ser", oldDropboxDirectoryList);
            }
            if(dropboxFileChange) {
                methods.writeObject("dropboxFileMap.ser", oldDropboxFileMap);
            }
            scanner.nextLine();
        }
    }
}