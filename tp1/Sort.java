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

class Sort {
    public static void sort(){
        try{
            //arquivo principal do banco de dados
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");
            //arquivo temporário para ajudar na ordenação externa
            RandomAccessFile arq1 = new RandomAccessFile("./tmp/sort/arq1.db", "rw");
            RandomAccessFile arq2 = new RandomAccessFile("./tmp/sort/arq2.db", "rw");
            RandomAccessFile arq3 = new RandomAccessFile("./tmp/sort/arq3.db", "rw");
            RandomAccessFile arq4 = new RandomAccessFile("./tmp/sort/arq4.db", "rw");

            arq1.setLength(0);
            arq2.setLength(0);
            arq3.setLength(0);
            arq4.setLength(0);

            int nChamps = divideFile(4, arq, arq1, arq2);
            intercalation(nChamps,4,arq,arq1,arq2,arq3,arq4); 
        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static int divideFile(int block,RandomAccessFile arq, RandomAccessFile arq1, RandomAccessFile arq2){
        int len;
        byte[] ba;
        Champion[] c1 = new Champion[block];
        Champion[] c2 = new Champion[block];
        Champion c;
        int contChamp = 0;
        int cont2 = 0;
        int cont1 = 0;

        int nChamps = 0;
        int champs1 = 0;
        int champs2 = 0;
        try{
            arq1.seek(0);
            arq2.seek(0);
            nChamps = arq.readInt();
            
            // descobrir quantidade final de campeões em cada arquivo
            while(true){
                if(champs1 + champs2 < nChamps){
                    champs1 += block;
                    champs2 = champs1;
                }else if(champs1 + champs2 > nChamps){
                    champs2--;
                }else break;
            }

            // escrever quantidade final
            arq1.writeInt(champs1);
            arq2.writeInt(champs2);

            while(contChamp < nChamps){

                // criar campeões em memória primária

                for(int i = 0; i < block; i++){
                    if(contChamp < nChamps){
                        c1[i] = new Champion();
                        len = arq.readInt();
                        ba = new byte[len];
                        arq.read(ba);
                        c1[i].transformToArray(ba);
                        contChamp++;
                        if(!c1[i].getExist()){
                            i--;
                            cont1--;
                        }
                    }
                }
                for(int i = 0; i < block; i++){
                    if(contChamp < nChamps){
                        c2[i] = new Champion();
                        len = arq.readInt();
                        ba = new byte[len];
                        arq.read(ba);
                        c2[i].transformToArray(ba);
                        contChamp++;
                        cont2++;
                        if(!c2[i].getExist()) {
                            cont2--;
                            i--;
                        }
                    }
                }

                // ordenar c1
                for (int i = 1; i < block; i++) {
                        c = new Champion();
                        c.clone(c1[i]);
                        int position = i;

                        while (position > 0 && c1[position - 1].getId() > c.getId()) {
                            c1[position] = c1[position - 1];
                            position--;
                        }
                        c1[position] = c;
                }
                for (int i = 0; i < block; i++) {
                        ba = c1[i].transformToByte();
                        arq1.writeInt(ba.length);
                        arq1.write(ba);
                }

                // ordenar c2
                for (int i = 1; i < cont2; i++) {
                        c = new Champion();
                        c.clone(c2[i]);
                        int position = i;

                            while (position > 0 && c2[position - 1].getId() > c.getId()) {
                                c2[position] = c2[position - 1];
                                position--;
                            }

                        c2[position] = c;
                }
                for (int i = 0; i < cont2; i++) {
                        ba = c2[i].transformToByte();
                        arq2.writeInt(ba.length);
                        arq2.write(ba);
                }
                cont2 = 0;
            }
        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return nChamps;
    }

    public static void intercalation(int nChamps, int block, RandomAccessFile arq, RandomAccessFile arq1, RandomAccessFile arq2, RandomAccessFile arq3, RandomAccessFile arq4){
        try{
            arq1.seek(0);
            arq2.seek(0);
            //  limpar arquivos
            arq3.setLength(0);
            arq4.setLength(0);

            long[] ps1;
            long[] ps2;

            int len;
            byte[] ba;
            long pos;

            boolean wfile = true;
            Champion c;

            int nChamps1 = 0;
            int nChamps2 = 0;

            // descobrir quantidade final de campeões em cada arquivo
            while(true){
                if(nChamps1 + nChamps2 < nChamps){
                    nChamps1 += block;
                    nChamps2 = nChamps1;
                }else if(nChamps1 + nChamps2 > nChamps){
                    nChamps2--;
                }else break;
            }
            if(nChamps1 > nChamps) nChamps1 = nChamps;
            // registrar numero de campeões em cada arquivo
            arq3.writeInt(nChamps1);
            arq4.writeInt(nChamps2);

            nChamps1 = arq1.readInt();
            nChamps2 = arq2.readInt();

            // ponteiros dos campeões para memória primária
            int cont = nChamps;
            while(cont > 0){
                ps1 = new long[block];
                for(int i = 0; i < block; i++){
                    if(cont > 0){
                        c = new Champion();
                        pos = arq1.getFilePointer();
                        len = arq1.readInt();
                        ba = new byte[len];
                        arq1.read(ba);
                        c.transformToArray(ba);
                        ps1[i] = pos;
                        cont--;
                    }else{
                        ps1[i] = -1;
                    }
                }
                ps2 = new long[block];
                for(int i = 0; i < block; i++){
                    if(cont > 0){
                        c = new Champion();
                        pos = arq2.getFilePointer();
                        len = arq2.readInt();
                        ba = new byte[len];
                        arq2.read(ba);
                        c.transformToArray(ba);
                        ps2[i] = pos;
                        cont--;
                    }else {
                        ps2[i] = -1;
                    }
                }

                int cont1 = 0;
                int cont2 = 0;
                for(int i = 0; i < block * 2; i++){
                    Champion c1 = new Champion();
                    Champion c2 = new Champion();

                    // se não ultrapassou o tamanho do bloco e o número de ponteiros armazenados na memória primária, registrar o que tem maior id
                    if(cont1 < block && cont2 < block && ps1[cont1] != -1 && ps2[cont2] != -1){
                        arq1.seek(ps1[cont1]);
                        len = arq1.readInt();
                        ba = new byte[len];
                        arq1.read(ba);
                        c1.transformToArray(ba);

                        arq2.seek(ps2[cont2]);
                        len = arq2.readInt();
                        ba = new byte[len];
                        arq2.read(ba);
                        c2.transformToArray(ba);
                        // conferir qual que tem o maior id
                        if(c1.getId() > c2.getId()){
                            // troca de arquivo
                            if(wfile){
                                ba = c2.transformToByte();
                                arq3.writeInt(ba.length);
                                arq3.write(ba);
                            }else {
                                ba = c2.transformToByte();
                                arq4.writeInt(ba.length);
                                arq4.write(ba);
                            }
                            cont2++;
                        }else{
                            // troca de arquivo
                            if(wfile){
                                ba = c1.transformToByte();
                                arq3.writeInt(ba.length);
                                arq3.write(ba);
                            }else {
                                ba = c1.transformToByte();
                                arq4.writeInt(ba.length);
                                arq4.write(ba);
                            }
                            cont1++;
                        }
                    // se acabou registros do ps2, registrar todos os do ps1
                    }else if(cont1 < block && ps1[cont1] != -1){
                        arq1.seek(ps1[cont1]);
                        len = arq1.readInt();
                        ba = new byte[len];
                        arq1.read(ba);
                        c1.transformToArray(ba);
                        if(wfile){
                            ba = c1.transformToByte();
                            arq3.writeInt(ba.length);
                            arq3.write(ba);
                        }else{
                            ba = c1.transformToByte();
                            arq4.writeInt(ba.length);
                            arq4.write(ba);
                        }cont1++;
                    // se acabou registros do ps2, registrar todos os do ps1
                    }else if(cont2 < block && ps2[cont2] != -1){
                        arq2.seek(ps2[cont2]);
                        len = arq2.readInt();
                        ba = new byte[len];
                        arq2.read(ba);
                        c2.transformToArray(ba);
                        if(wfile){
                            ba = c2.transformToByte();
                            arq3.writeInt(ba.length);
                            arq3.write(ba);
                        }else {
                            ba = c2.transformToByte();
                            arq4.writeInt(ba.length);
                            arq4.write(ba);
                        }cont2++;
                    }
                }
                cont1 = 0;
                cont2 = 0;
                // troca de escrita de arquivo
                wfile = !wfile;
            }

            // recursão com arquivos mudados de posição
            if(block < nChamps) intercalation(nChamps, block * 2, arq, arq3, arq4, arq1, arq2);
            // se já ordenou, ler eles ordenados
            else {
                arq3.seek(0);
                arq3.readInt();
                for(int i = 0; i < nChamps; i++){
                    c = new Champion();
                    len = arq3.readInt();
                    ba = new byte[len];
                    arq3.read(ba);
                    c.transformToArray(ba);
                    
                    c.printChamp();
                }
            }
        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}