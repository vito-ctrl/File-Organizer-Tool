//import java.io.File;
//import java.io.FileWriter;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class Main {
    public static void main(String[] args){
        Path path = Paths.get("hi.txt");
        String data = "hello world!";
        try{
            Files.write(path, data.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e){
            System.out.println("File reading error.");
            e.printStackTrace();
        }
    }
}

//        try(FileInputStream input = new FileInputStream("test.txt")) {
//            int i;
//            while ((i = input.read()) != -1){
//                System.out.print((char) i);
//            }
//            System.out.print("\nreading file succesfully\n");



//            File myOb = new File("test.txt");
//            if(myOb.exists()){
//                System.out.println(myOb.getName() + " exists :)");
//                FileWriter o = new FileWriter("test.txt");
//                o.write("this is my first try ;)");
//                o.close();
//                System.out.println("file writen succefully :)");
//            }else{
//                System.out.println("File don't exist");
//            }
