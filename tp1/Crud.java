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

class Crud {
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

    // função para a escrita do csv para a memória primária e da memória primária para o data.db
    public static void write(Champion[] list){
        try {
            byte[] ba;
            RandomAccessFile arq = new RandomAccessFile("./tmp/data.db", "rw");
            int len = list.length;
            long pos;
            // tamanho da lista de campeões na memória primária
            arq.writeInt(len);
            for(int i = 0; i < len; i++){
                ba = list[i].transformToByte();
                pos = arq.getFilePointer();
                BTree.insert(list[i].getId(), pos);
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
}