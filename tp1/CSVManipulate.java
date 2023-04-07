import java.util.*;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.text.DecimalFormat;

class CSVManipulate {

    public static void readCsv(Champion[] list){
        try{
            // abrir arquivo csv
            File file = new File("./tmp/table.csv");
            Scanner scFile = new Scanner(file);

            // ignorar cabeçalho do csv
            scFile.nextLine();

            // ler todos os campeões e salvar em memória primária
            int cont = 0;
            while(scFile.hasNextLine()) {
                list[cont] = new Champion();
                list[cont].read(scFile.nextLine(), cont + 1);
                cont++;
            }

        }catch (FileNotFoundException ex){
            System.out.println(ex);
        }
    } 
}