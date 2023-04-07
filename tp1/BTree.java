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

class BTree {
    public static void start(){
        try {
            long pos;
            long root;
            RandomAccessFile tree = new RandomAccessFile("./tmp/tree/tree.db", "rw");     
            tree.setLength(0);
            tree.writeLong(8L);
            root = tree.getFilePointer();
            pos = createNode(tree.length(), tree);
            tree.seek(0);
        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static long createNode(long pos,RandomAccessFile tree){
        try {
            tree.seek(pos);
            pos = tree.getFilePointer();
            // quantidade de registros na página
            tree.writeInt(0);

            // ponteiro esquerda
            tree.writeLong(-1L);
            // id
            tree.writeInt(-1);
            // localização do registro na data.db
            tree.writeLong(-1L);
            // ponteiro direita (ponteiro esquerda do próximo registro)
            tree.writeLong(-1L);

            tree.writeInt(-1);
            tree.writeLong(-1L);

            tree.writeLong(-1L);
            
            tree.writeInt(-1);
            tree.writeLong(-1L);

            tree.writeLong(-1L);

            tree.writeInt(-1);
            tree.writeLong(-1L);

            tree.writeLong(-1L);

            tree.writeInt(-1);
            tree.writeLong(-1L);

            tree.writeLong(-1L);

            tree.writeInt(-1);
            tree.writeLong(-1L);
            
            tree.writeLong(-1L);

            tree.writeInt(-1);
            tree.writeLong(-1L);
            // ultimo ponteiro (maiores ids)
            tree.writeLong(-1L);
            // árvore de ordem 8 (7 registros por página)
        }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return pos;
    }

    public static void insert(int id, long reg){
        try{
            RandomAccessFile tree = new RandomAccessFile("./tmp/tree/tree.db", "rw");
            long root = tree.readLong();
            insert(id, reg, root, false);

        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static boolean insert(int id, long reg, long startOfPage, boolean hasFather){
        boolean returnToFather = false;
        try{
            RandomAccessFile tree = new RandomAccessFile("./tmp/tree/tree.db", "rw");
            // posição da raíz
            System.out.println("Inserção do " + id);
            long tmp;
            int idTree;
            int q;
            boolean isInsert = false;
            boolean isFound = false;
            // procurar raíz
            tree.seek(startOfPage);
            // quantidade de registros na raíz
            q = tree.readInt();
            // verificar se tem ponteiro
            tmp = tree.readLong();
            // voltar para ler corretamente e pular quantidade de registros
            tree.seek(startOfPage);
            tree.readInt();
            // se não tiver ponteiro = folha
            if(tmp == -1L){
                // verificar se cabe na folha
                if(q < 7){
                    tree.seek(startOfPage);
                    tree.readInt();
                    // ordenação por inserção
                    Reg[] regs = new Reg[q + 1];
                    for(int i = 0; i < q + 1; i++) regs[i] = new Reg();
                    regs[0].id = id;
                    regs[0].reg = reg;
                    for(int i = 1; i < q + 1; i++){
                        tree.readLong();
                        regs[i].id = tree.readInt();
                        regs[i].reg = tree.readLong();
                    }
                    for (int i = 1; i < q + 1; i++) {
                        Reg key = regs[i];
                        int j = i - 1;
                        while (j >= 0 && regs[j].id > key.id) {
                            regs[j + 1] = regs[j];
                            j--;
                        }
                        regs[j + 1] = key;
                    }
                    tree.seek(startOfPage);
                    tree.writeInt(q + 1);
                    for(int i = 0; i < q + 1; i++){
                        tree.readLong();
                        tree.writeInt(regs[i].id);
                        tree.writeLong(regs[i].reg);
                    }
                // se não couber 
                }else{
                    // verificar se tem pai com espaço. senão tiver -> procedimento de split
                    if(!hasFather){
                       split(startOfPage, tree, id, reg);

                    // caso tenha. retorne para o pai
                    }else{ 
                        returnToFather = true;
                    }
                }
            // caso o nó tenha filhos
            }else{
                // se tiver espaço
                if(q < 7){
                    hasFather = true;
                    for(int i = 0; !isFound && i < q; i++){
                        tmp = tree.readLong();
                        int thisId = tree.readInt();
                        tree.readLong();
                        if(id < thisId){
                            // verificar se o nó precisa dar throw em algum filho
                            // erro aqui
                            if(insert(id, reg, tmp, hasFather)){
                                while(!isInsert){
                                    tree.readLong();
                                    tmp = tree.getFilePointer();
                                    idTree = tree.readInt();
                                    tree.readLong();
                                    // quando encontrar espaço vazio, inserir
                                    if(idTree == -1){
                                        tree.seek(tmp);
                                        tree.writeInt(id);
                                        tree.writeLong(reg);
                                        tree.seek(startOfPage);
                                        tree.writeInt(q + 1);
                                        isInsert = !isInsert;
                                    }
                                }
                            }
                            isFound = true; 
                        }
                    }
                    // if do final, para verificar o último ponteiro (ele não é verificado no while anterior)
                    if(!isFound){
                        long h = tree.getFilePointer();
                        tmp = tree.readLong();
                        if(insert(id, reg, tmp, hasFather)){
                            // esse caso contém um split diferente, pois é um split que possui um pai 
                            Reg throwReg = splitWithFather(tmp, tree, id, reg);
                            tree.seek(h);
                            tree.writeLong(throwReg.pointLef);
                            tree.writeInt(throwReg.id);
                            tree.writeLong(throwReg.reg);
                            tree.writeLong(throwReg.pointRig);
                            tree.seek(startOfPage);
                            tree.writeInt(q + 1);
                            isInsert = !isInsert;
                        }
                    }
                // se não couber no nó, porém tem pai
                }else {
                    split(startOfPage, tree, id, reg);
                }
            }
            readNode(startOfPage,tree);
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return returnToFather;
    }

    public static void split(long startOfPage, RandomAccessFile tree, int id, long reg){
        try{
        // criar mais 2 nós
        long posNodeSmall = createNode(tree.length(), tree);
        long posNodeBig = createNode(tree.length(), tree);
        long fatherNode = startOfPage;
        // criar array que guarda ids e regs ordenados
        Reg[] regs = new Reg[8];
        for(int i = 0; i < 8; i++) regs[i] = new Reg();
        tree.seek(startOfPage);
        tree.readInt();
        // primeira parte: guardar os 8 registros
        // TODO: guardar ponteiros também
        regs[0].id = id;
        regs[0].reg = reg;
        for(int i = 1; i < 8; i++){
            tree.readLong();
            regs[i].id = tree.readInt();
            regs[i].reg = tree.readLong();
        }
        // algoritmo de inserção
        for (int i = 1; i < 8; i++) {
            Reg key = regs[i];
            int j = i - 1;
            while (j >= 0 && regs[j].id > key.id) {
                regs[j + 1] = regs[j];
                j--;
            }
            regs[j + 1] = key;
        }
        // seek na posição do nó da esquerda
        tree.seek(posNodeSmall);
        tree.writeInt(4);
        tree.writeLong(-1L);
        // escrever 4 ids
        for(int i = 0; i < 4; i++){
            tree.writeInt(regs[i].id);
            tree.writeLong(regs[i].reg);
            if(i < 3) tree.writeLong(-1);
            else tree.writeLong(posNodeBig);
        }
        // seek na posição do nó da direita
        tree.seek(posNodeBig);
        tree.writeInt(4);
        tree.writeLong(-1L);
        // escrever 4 ids
        for(int i = 4; i < 8; i++){
            tree.writeInt(regs[i].id);
            tree.writeLong(regs[i].reg);
            tree.writeLong(-1);
        }
        // criar um novo nó para guardar o pai. ele ocupará o espaço do nó antigo pré-split
        createNode(startOfPage,tree);
        tree.seek(startOfPage);
        tree.writeInt(1);
        tree.writeLong(posNodeSmall);
        tree.writeInt(regs[4].id);
        tree.writeLong(regs[4].reg);
        tree.writeLong(posNodeBig);
        System.out.println("split:");
        System.out.println("nó esquerdo:");
        readNode(posNodeSmall, tree);
        System.out.println("nó pai:");
        readNode(startOfPage, tree);
        System.out.println("nó direito:");
        readNode(posNodeBig, tree);
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static Reg splitWithFather(long startOfPage, RandomAccessFile tree, int id, long reg){
        Reg throwReg = new Reg();
        try{
        // criar mais 2 nós
        long posMidChild = createNode(tree.length(), tree);
        long posLeftChild = startOfPage;
        // criar array que guarda ids e regs ordenados
        Reg[] regs = new Reg[8];
        for(int i = 0; i < 8; i++) regs[i] = new Reg();
        tree.seek(startOfPage);
        tree.readInt();
        // primeira parte: guardar os 8 registros
        // TODO: guardar ponteiros também
        regs[0].id = id;
        regs[0].reg = reg;
        for(int i = 1; i < 8; i++){
            tree.readLong();
            regs[i].id = tree.readInt();
            regs[i].reg = tree.readLong();
        }
        createNode(posLeftChild,tree);
        // algoritmo de inserção
        for (int i = 1; i < 8; i++) {
            Reg key = regs[i];
            int j = i - 1;
            while (j >= 0 && regs[j].id > key.id) {
                regs[j + 1] = regs[j];
                j--;
            }
            regs[j + 1] = key;
        }
        // seek na posição do nó da esquerda
        tree.seek(posMidChild);
        tree.writeInt(4);
        tree.writeLong(-1L);
        // escrever 4 ids
        for(int i = 0; i < 4; i++){
            tree.writeInt(regs[i].id);
            tree.writeLong(regs[i].reg);
            if(i < 3) tree.writeLong(-1);
            else tree.writeLong(posLeftChild);
        }
        // seek na posição do nó da direita
        tree.seek(posLeftChild);
        tree.writeInt(4);
        tree.writeLong(-1L);
        // escrever 4 ids
        for(int i = 4; i < 8; i++){
            tree.writeInt(regs[i].id);
            tree.writeLong(regs[i].reg);
            tree.writeLong(-1);
        }
        // criar um novo nó para guardar o pai. ele ocupará o espaço do nó antigo pré-split
        System.out.println("split:");
        System.out.println("nó filho do meio:");
        readNode(posMidChild, tree);
        System.out.println("nó filho da direita:");
        readNode(posLeftChild, tree);
        throwReg.id = regs[4].id;
        throwReg.reg = regs[4].reg;
        throwReg.pointLef = posMidChild;
        throwReg.pointRig = posLeftChild;
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return throwReg;
    }

    public static void readNode(long pos,RandomAccessFile tree){
        try{
            tree.seek(pos);
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readInt() + " ");
            System.out.print(tree.readLong() + " ");
            System.out.print(tree.readLong() + "\n");
        }catch(IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}