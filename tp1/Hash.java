import java.util.*;
import java.lang.Math;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.text.DecimalFormat;

class Reg {
    public int id;
    public long reg;
    public long pointRig;
    public long pointLef;

    public Reg(){
        id = 0;
        reg = 0;
        pointRig = 0L;
        pointLef = 0L;
    }

    public Reg(int id, long reg, long pointRig, long pointLef){
        this.id = id;
        this.reg = reg;
        this.pointRig = pointRig;
        this.pointLef = pointLef;
    }
}

class Hash {
    public static void start(){
        try{
            // arquivos separados para atualização fácil
            RandomAccessFile directory = new RandomAccessFile("./tmp/hash/directory.db", "rw");
            RandomAccessFile buckets = new RandomAccessFile("./tmp/hash/buckets.db", "rw");
            directory.setLength(0);
            buckets.setLength(0);
            // profundidade
            directory.writeInt(1);
            for(int i = 0; i < 2; i++){
                long pos = createBucket(buckets.length(), 1);
                newPointer(directory.length(), pos, i);
            } 
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static long createBucket(long pos, int depth){
        try{
            RandomAccessFile buckets = new RandomAccessFile("./tmp/hash/buckets.db", "rw");
            buckets.seek(pos);
            // profundidade
            buckets.writeInt(depth);
            // quantidade de registros no bucket
            buckets.writeInt(0);
            // chaves + registro
            for(int i = 0; i < 8; i++){
                buckets.writeInt(-1);
                buckets.writeLong(-1L);
            }
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        } 
        return pos;
    }

    public static void newPointer(long pos,long bucket, int mod){
        try{
            RandomAccessFile directory = new RandomAccessFile("./tmp/hash/directory.db", "rw");
            directory.seek(pos);
            // ponteiro para bucket respectivo
            directory.writeInt(mod);
            directory.writeLong(bucket);
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void insert(int id, long reg){
        try{
            RandomAccessFile directory = new RandomAccessFile("./tmp/hash/directory.db", "rw");
            RandomAccessFile buckets = new RandomAccessFile("./tmp/hash/buckets.db", "rw");

            int directoryDepth = directory.readInt();
            int bucketDepth;
            int mod;
            long pDirectory = (long)((id % Math.pow(2, directoryDepth)) * 12) + 4;
            long pBucket;
            int n;

            // escontrar o mod e o ponteiro para o bucket
            directory.seek(pDirectory);
            mod = directory.readInt();
            pBucket = directory.readLong();

            buckets.seek(pBucket);

            bucketDepth = buckets.readInt();
            n = buckets.readInt();

            // se não tiver registros no bucket
            if(n == 0){ 
                buckets.seek(pBucket + 4);
                // quantidade de registros no bucket
                buckets.writeInt(1);
                // registro
                buckets.writeInt(id);
                buckets.writeLong(reg);

            // se o número de registros no bucket for menor que a capacidade dele
            }else if(n < 8){
                buckets.seek(pBucket + 4);
                buckets.writeInt(n + 1);
                buckets.seek(pBucket + 8 + (n * 12));
                buckets.writeInt(id);
                buckets.writeLong(reg);

            // se não couber novos registros no bucket
            }else{
                Reg[] regs = new Reg[9];
                int b;
                // se profundidade do diretório for maior que profundidade do bucket
                if(directoryDepth > bucketDepth){
                    buckets.seek(pBucket);
                    buckets.readInt();
                    buckets.readInt();
                    // escrever os registros do bucket + registro a ser inserido
                    regs[0] = new Reg();
                    regs[0].id = id;
                    regs[0].reg = reg;
                    for(int i = 1; i < 9; i++){
                        regs[i] = new Reg();
                        regs[i].id = buckets.readInt();
                        regs[i].reg = buckets.readLong();
                    }
                    // criar bucket e atualizar o bucket antigo
                    Long newBucket = createBucket(buckets.length(), bucketDepth + 1);
                    createBucket(pBucket, bucketDepth + 1);
                    // atualizar diretório
                    newPointer(pDirectory, newBucket, mod);
                    // jogar registro nos buckets
                    int cont1 = 0;
                    int cont2 = 0;
                    b = (int)Math.pow(2, bucketDepth + 1);
                    buckets.seek(pBucket + 8);
                    for(int i = 0; i < 9; i++){
                        if(regs[i].id % b == mod){
                            buckets.writeInt(regs[i].id);
                            buckets.writeLong(regs[i].reg);
                            cont1++;
                        }
                    }
                    buckets.seek(pBucket + 4);
                    buckets.writeInt(cont1);

                    buckets.seek(newBucket + 8);
                    for(int i = 0; i < 9; i++){
                        if(regs[i].id % b != mod){
                            buckets.writeInt(regs[i].id);
                            buckets.writeLong(regs[i].reg);
                            cont2++;
                        }
                    }
                    buckets.seek(newBucket + 4);
                    buckets.writeInt(cont2);
                // se profundidade do diretório for menor ou igual à profundidade do bucket
                } else {
                    // atualizar o valor da profundidade do diretório
                    int nPDirectory = (int)Math.pow(2, directoryDepth);
                    directory.seek(0);
                    directory.writeInt(directoryDepth + 1);

                    // duplicar quantidade de ponteiros no diretório
                    long newBucket = createBucket(buckets.length(), bucketDepth + 1);
                    for(int i = 0; i < nPDirectory; i++){
                        directory.seek((long)(i * 12) + 4);
                        directory.readInt();
                        long tmp = directory.readLong();
                        // esse if else é simplesmente para mudar o ponteiro 
                        if(i % nPDirectory != mod) newPointer(directory.length(), tmp, nPDirectory + i);
                        else newPointer(directory.length(), newBucket, nPDirectory + i);
                    }

                    buckets.seek(pBucket + 8);
                    // escrever os registros do bucket + registro a ser inserido
                    regs[0] = new Reg();
                    regs[0].id = id;
                    regs[0].reg = reg;
                    for(int i = 1; i < 9; i++){
                        regs[i] = new Reg();
                        regs[i].id = buckets.readInt();
                        regs[i].reg = buckets.readLong();
                    }
                    createBucket(pBucket, bucketDepth + 1);
                    // jogar registros nos buckets
                    int cont1 = 0;
                    int cont2 = 0;
                    b = (int)Math.pow(2, directoryDepth + 1);
                    buckets.seek(pBucket + 8);
                    for(int i = 0; i < 9; i++){
                        if(regs[i].id % b == mod){
                            cont1++;
                            buckets.writeInt(regs[i].id);
                            buckets.writeLong(regs[i].reg);
                        }
                    }
                    buckets.seek(pBucket);
                    buckets.readInt();
                    buckets.writeInt(cont1);


                    buckets.seek(newBucket);
                    buckets.readInt();
                    buckets.readInt();
                    for(int i = 0; i < 9; i++){
                        if(regs[i].id % b != mod){
                            cont2++;
                            buckets.writeInt(regs[i].id);
                            buckets.writeLong(regs[i].reg);
                        }
                    }
                    buckets.seek(newBucket);
                    buckets.readInt();
                    buckets.writeInt(cont2);

                    
                }
            }

        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void readBucket(long pos){
        try{
            RandomAccessFile directory = new RandomAccessFile("./tmp/hash/directory.db", "rw");
            RandomAccessFile buckets = new RandomAccessFile("./tmp/hash/buckets.db", "rw");
            for(int j = 0; j < 100; j++){
                // quantidade de registros no bucket
                System.out.print(buckets.readInt() + " ");
                System.out.print(buckets.readInt() + " ");
                // chaves + registro
                for(int i = 0; i < 7; i++){
                    System.out.print(buckets.readInt() + " ");
                    System.out.print(buckets.readLong() + " ");
                }
                System.out.print(buckets.readInt() + " ");
                System.out.println(buckets.readLong() + " ");
            }
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}