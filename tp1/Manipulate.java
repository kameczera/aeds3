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

class Manipulate {

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

    // função para a escrita do csv para a memória primária e da memória primária para o data.db
    public static void write(Champion[] list){
        try {
            byte[] ba;
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");
            int len = list.length;

            // tamanho da lista de campeões na memória primária
            arq.writeInt(len);
            for(int i = 0; i < len; i++){
                ba = list[i].transformToByte();
                arq.writeInt(ba.length);
                arq.write(ba);
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void read(){
        try {
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");
            int len;
            byte[] ba;
            Champion c;
            
            // ler os n campeões do cabeçalho
            int nChamps = arq.readInt();
            for(int i = 0; i < nChamps; i++){
                c = new Champion();
                len = arq.readInt();
                ba = new byte[len];
                arq.read(ba);
                c.transformToArray(ba);
                
                // se campeão existir, imprimir ele
                if(c.getExist()) c.printChamp();
                else i--;
            }
        }catch(Exception e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static boolean delete(int id) {
        boolean isDeleted = false;
        try {
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");

            int len = 0;
            long pos = 0;
            byte[] ba;
            Champion c;
            
            // salvar posição inicial para ler o n campeões do arquivo e depois diminuir 1 
            pos = arq.getFilePointer();
            int nChamps = arq.readInt();
            arq.seek(pos);
            arq.writeInt(nChamps - 1);
            
            for(int i = 0; i < nChamps && !isDeleted; i++){
                //ler tamanho do registro do campeão
                len = arq.readInt();
                pos = arq.getFilePointer();
                
                //ler campeão para memória primária
                c = new Champion();
                ba = new byte[len];
                arq.read(ba);
                c.transformToArray(ba);

                if(c.getExist()){
                    // verificar se é igual ao do campeão
                    if(c.getId() == id){
                        //caso for, voltar para posição armazenada e reescrever o registro como não existente
                        arq.seek(pos);
                        c.setExist(false);
                        ba = c.transformToByte();
                        arq.write(ba);
                        System.out.println("ok");
                        isDeleted = true;
                    }
                }else i--;
            }

        }catch(Exception e){
            System.out.println(e);
        }
        return isDeleted;
    }

    public static void insert(Champion champ, int id){
        try {
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");

            int len;
            byte[] ba;
            Champion c = new Champion();
            long pos;

            // ler quantidade de ids e voltar para mudar para nIds + 1
            pos = arq.getFilePointer();
            int nChamps = arq.readInt();
            arq.seek(pos);
            arq.writeInt(nChamps + 1);

            // ler os n campeões do arquivo
            for(int i = 0; i < nChamps; i++){
                len = arq.readInt();
                ba = new byte[len];
                arq.read(ba);
                c.transformToArray(ba);
                //caso o campeão lido não exista i-- para compensar
                if(!c.getExist()) i--;
            }

            // se id recebido pela main do novo campeão -> id = quantidade de campeões no arquivo + 1
            if(id == 0) champ.setId(nChamps + 1);
            // se id recebido pelo update do campeão atualizado -> id = id do campeão antes da mudanã
            else champ.setId(id);
            
            ba = champ.transformToByte();
            arq.writeInt(ba.length);
            arq.write(ba);

        }catch(Exception e){}
    }

    public static void update(int id, int num){
        try {
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");
            Scanner sc = new Scanner(System.in);

            int len = 0;
            byte[] ba, baClone;
            Champion c = new Champion();
            long pos = 0;

            int nChamps = arq.readInt();
            boolean found = false;

            // ler todos campeões até achar o com id igual
            for(int i = 0; i < nChamps && !found; i++){
                c = new Champion();
                len = arq.readInt();
                pos = arq.getFilePointer();
                ba = new byte[len];
                arq.read(ba);
                c.transformToArray(ba);
                //caso o campeão lido não exista i-- para compensar
                if(c.getExist()){
                    //caso o campeão lido tenha o id igual ao id procurado: encontrado
                    if(c.getId() == id) found = true;
                }else i--;
            }
            if(found){
                arq.seek(pos);
                Champion clone = new Champion();
                clone.clone(c);
                if(num == 1) clone.setName(sc.nextLine());
                else if (num == 2) clone.setTitle(sc.nextLine());
                else if (num == 3) clone.setBlurb(sc.nextLine());
                else if (num == 4) clone.setTags0(sc.nextLine());
                else if (num == 5) clone.setTags1(sc.nextLine());
                else if (num == 6) clone.setPartype(sc.nextLine());
                else if (num == 7) clone.setAttack(Integer.parseInt(sc.nextLine()));
                else if (num == 8) clone.setDefense(Integer.parseInt(sc.nextLine()));
                else if (num == 9) clone.setMagic(Integer.parseInt(sc.nextLine()));
                else if (num == 10) clone.setDifficulty(Integer.parseInt(sc.nextLine()));
                else if (num == 11) clone.setDate(sc.nextLine());
                else if (num == 12) clone.setId(Integer.parseInt(sc.nextLine()));

                // pegar tamanho em bytes do registro antes da atualização e depois
                ba = c.transformToByte();
                baClone = clone.transformToByte();

                // se update for maior que o tamanho de bytes
                if(baClone.length > ba.length){
                    c.setExist(false);
                    ba = c.transformToByte();
                    arq.write(ba);

                    // voltar para o começo e tirar 1 champion do cabeçalho
                    arq.seek(0);
                    arq.writeInt(nChamps - 1);

                    // inserir campeão atualizado
                    insert(clone, c.getId());
                }else arq.write(baClone);
            } else System.out.println("Champion doesn't exist");
        }catch(Exception e){}
    }

    public static void sort(){
        try{
            //arquivo principal do banco de dados
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");
            //arquivo temporário para ajudar na ordenação externa
            RandomAccessFile arq1 = new RandomAccessFile("./tmp/arq1.db", "rw");
            RandomAccessFile arq2 = new RandomAccessFile("./tmp/arq2.db", "rw");
            RandomAccessFile arq3 = new RandomAccessFile("./tmp/arq3.db", "rw");
            RandomAccessFile arq4 = new RandomAccessFile("./tmp/arq4.db", "rw");

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
                        System.out.print(cont + " ");
                        System.out.println(c.getName() + " " + ps1[i]);
                    }else{
                        System.out.println("-1");
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
                        System.out.print(cont + " ");
                        System.out.println(c.getName() + " " + ps2[i]);
                    }else {
                        System.out.println("-1");
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
                        System.out.println(c1.getName());
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
                        System.out.println(c2.getName());
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

        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}