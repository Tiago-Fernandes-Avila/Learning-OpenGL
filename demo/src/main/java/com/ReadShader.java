package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadShader {

    public static String read(String shaderPath){
        String data  = "";
    

        try (BufferedReader br = new BufferedReader(new FileReader(shaderPath));) {

            String line = br.readLine();
            while(line != null){
                data += line +"\n";
                line = br.readLine(); 
            }
            
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }

        return data;
    }
    
}
