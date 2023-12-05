package ru.yoricya.mvnd.phpfuncs;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PHPFuncs {
    public static int rand(int a, int b) {
        return a + (int) (Math.random() * b);
    }

    public static boolean rand() {
        int a = rand(0, 1);
        return a == 1;
    }

    public static String file_get_contents(String filep) {
        StringBuilder str = new StringBuilder();
        try {
            File file = new File(filep);
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            while (line != null) {
                str.append(line + "\n");
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            System.out.println("FILE: "+filep);
            e.printStackTrace();
        }
        return str.toString();
    }
    public static String file_get_contents(URL urlObject) {
        try {
            URLConnection conn = urlObject.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine, output = "";
            while ((inputLine = in.readLine()) != null) {
                output += inputLine;
            }
            in.close();
            return output;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean file_put_contents(String filename, String data) {
        try {
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(data);
            out.close();
        } catch (FileNotFoundException f) {
            try {
                File myObj = new File(filename);
                myObj.setReadable(true, false);
                myObj.setWritable(true, false);
                myObj.setExecutable(true, false);
                myObj.createNewFile();
                return file_put_contents(filename, data);
            } catch (IOException e) {
                System.out.println("FILE: "+filename);
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean mkdir(String pathp) {
        Path path = Paths.get(pathp);
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static boolean is_dir(String path){
        try {
            File f = new File(path);
            return f.isDirectory();
        }catch (Exception e){e.printStackTrace(); return false;}
    }
}