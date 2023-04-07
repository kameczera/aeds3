import java.util.*;

//dependencies read csv file
import java.io.File;
import java.io.FileNotFoundException;

import java.io.RandomAccessFile;

class lol {
    public static void main(String[] args){
        try{
            // comandos para iniciar o arquivo
            Champion[] list = new Champion[148];
            BTree.start();
            CSVManipulate.readCsv(list);
            Crud.write(list);
            RandomAccessFile tree = new RandomAccessFile("./tmp/tree/tree.db", "rw");
            
            Scanner sc = new Scanner(System.in);
            int op;
            long j = 0L;
            tree.seek(0);
            tree.readLong();
            
            do{
            System.out.println("Sistema de Banco de Dados de Campeões de League of Legends\n");
            System.out.println("0 - Sair");
            System.out.println("1 - Ler todos os Campeões cadastrados");
            System.out.println("2 - Cadastrar um Campeão");
            System.out.println("3 - Atualizar um Campeão");
            System.out.println("4 - Deletar um Campeão\n");

                op = Integer.parseInt(sc.nextLine());   
                int id;
                switch (op) {
                    case 0:
                        System.out.println("Tchau");
                        break;
                    case 1:
                        Crud.read();
                        break;
                    case 2:
                        System.out.print("Digite o nome: ");
                        String name = sc.nextLine();
                        
                        System.out.print("Digite o título: ");
                        String title = sc.nextLine();
                        
                        System.out.print("Digite a história: ");
                        String blurb = sc.nextLine();
                        
                        System.out.print("Digite a tag:");
                        String tag1 = sc.nextLine();
                        
                        System.out.print("Digite a tag 2 (caso não exista, não digite nada): ");
                        String tag2 = sc.nextLine();
                        
                        System.out.print("Digite o Partype (ex: mana, fúria etc..): ");
                        String partype = sc.nextLine();
                        
                        int attack, defense, magic, difficulty;
                        System.out.print("Digite o ataque: ");
                        attack = Integer.parseInt(sc.nextLine());

                        System.out.print("Digite a defesa: ");
                        defense = Integer.parseInt(sc.nextLine());

                        System.out.print("Digite a magia: ");
                        magic = Integer.parseInt(sc.nextLine());

                        System.out.print("Digite a dificuldade: ");
                        difficulty = Integer.parseInt(sc.nextLine());
                        
                        System.out.print("Digite a data de lançamento (formato: dd/mm/aaaa): ");
                        String dateRelease = sc.nextLine();

                        Champion c = new Champion(true, name, title, blurb, tag1, tag2, partype, attack, defense, magic, difficulty, dateRelease);

                        Crud.insert(c, 0);
                        break;
                    case 3:
                        System.out.println("Id do campeão a ser alterado: ");
                        id = Integer.parseInt(sc.nextLine());

                        System.out.println("\n1 - Nome");
                        System.out.println("2 - Título");
                        System.out.println("3 - História");
                        System.out.println("4 - Tag 1");
                        System.out.println("5 - Tag 2");
                        System.out.println("6 - Partype");
                        System.out.println("7 - Ataque");
                        System.out.println("8 - Defesa");
                        System.out.println("9 - Mágica");
                        System.out.println("10 - Dificuldade");
                        System.out.println("11 - Data de Lançamento\n");

                        int num = Integer.parseInt(sc.nextLine());

                        System.out.println("\nDigite o valor da mudança:");
                        Crud.update(id, num);
                        System.out.println("ok");
                        break;
                    case 4:
                        System.out.println("Id do campeão a ser removido: ");
                        id = Integer.parseInt(sc.nextLine());
                        
                        if(Crud.delete(id)) System.out.println("ok");
                        break;
                    case 5:
                        System.out.println("Ordenação: ");
                        Sort.sort();
                }
            }while(op != 0);
        }catch(Exception e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}