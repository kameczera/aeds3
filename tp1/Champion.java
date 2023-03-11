import java.util.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.text.DecimalFormat;

class Champion {
    private int id;
    private boolean exist;
    private String name;
    private String title;
    private String blurb;
    private String[] tags;
    private String partype;
    private int attack;
    private int defense;
    private int magic;
    private int difficulty;
    private Date dateRelease;

    public Champion() {
        this.id = 0;
        this.name = this.title = this.blurb = this.partype = "";
        this.tags = new String[2];
        this.attack = this.defense = this.magic = this.difficulty = 0;
        this.dateRelease = null;
        this.dateRelease = new Date();
        this.exist = true;
    }

    public Champion(boolean exist, String name, String title, String blurb, String tags0, String tags1, String partype, int attack, int defense, int magic, int difficulty, String dateRelease){
        this.exist = exist;
        this.name = name;
        this.title = title;
        this.blurb = blurb;
        this.tags = new String[2];
        this.tags[0] = tags0;
        if(!tags1.equals("")) this.tags[1] = tags1;
        else tags[1] = null;
        this.partype = partype;
        this.attack = attack;
        this.defense = defense;
        this.magic = magic;
        this.difficulty = difficulty;
        this.dateRelease = new Date();
        setDate(dateRelease);
    }

    public void clone(Champion c){
        this.id = c.getId();
        this.exist = c.getExist();
        this.name = c.getName();
        this.title = c.getTitle();
        this.blurb = c.getBlurb();
        this.tags[0] = c.getTags0();
        this.tags[1] = c.getTags1();
        this.partype = c.getPartype();
        this.attack = c.getAttack();
        this.defense = c.getDefense();
        this.magic = c.getMagic();
        this.difficulty = c.getDifficulty();
        this.dateRelease = c.getDateRelease();
    }

    public void printChamp(){
        System.out.print(id + ", " + name + ", " + title + ", " + blurb + ", " + tags[0] + ", ");
        System.out.print((tags[1] != null) ? tags[1] + ", " : "");
        System.out.println(partype + ", " + attack + ", " + defense + ", " + magic + ", " + difficulty + ", " + dateRelease.getDate());
    }

    public void setExist(boolean exist){
        this.exist = exist;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setBlurb(String blurb){
        this.blurb = blurb;
    }

    public void setTags0(String tags0){
        this.tags[0] = tags0;
    }

    public void setTags1(String tags1){
        this.tags[1] = tags1;
    }

    public void setPartype(String partype){
        this.partype = partype;
    }

    public void setDate(String date){
        int index = 0, stopIndex = 0;
        int day = 0, month = 0, year = 0;
        // find day
        while(true){
            index++;
            if(date.charAt(index) == '/'){
                day = Integer.parseInt(date.substring(stopIndex, index));
                stopIndex = ++index;
                break;
            }
        }

        //find month
        while(true){
            index++;
            if(date.charAt(index) == '/'){
                month = Integer.parseInt(date.substring(stopIndex, index));
                stopIndex = ++index;
                break;
            }
        }

        // find year 
        while(index < date.length()){
            index++;
        }
        year = Integer.parseInt(date.substring(stopIndex, index));
        dateRelease.setDate(day, month, year);
    }

    public void setAttack(int attack){
        this.attack = attack;
    }

    public void setDefense(int defense){
        this.defense = defense;
    }

    public void setMagic(int magic){
        this.magic = magic;
    }

    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    public int getId(){
        return id;
    }

    public boolean getExist(){
        return exist;
    }

    public String getName(){
        return name;
    }

    public String getTitle(){
        return title;
    }

    public String getBlurb(){
        return blurb;
    }

    public String getTags0(){
        return tags[0];
    }

    public String getTags1(){
        return tags[1];
    }

    public String getPartype(){
        return partype;
    }

    public int getAttack(){
        return attack;
    }

    public int getDefense(){
        return attack;
    }

    public int getMagic(){
        return magic;
    }

    public int getDifficulty(){
        return difficulty;
    }

    public Date getDateRelease(){
        return dateRelease;
    }

    public void read(String line, int id){
        int index = 0, stopIndex = 0;

        // id
        this.id = id;

        // --------------------------------- //

        // find name
        while(true) {
            index++;
            if(line.charAt(index) == ','){
                this.name = line.substring(stopIndex, index);
                getName();
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find title
        while(true){
            index++;
            if(line.charAt(index) == ','){
                this.title = line.substring(stopIndex, index);
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find blurb
        while(true){
            index++;
            if(line.charAt(index) == '"'){
                // ++index to remove ,
                this.blurb = line.substring(stopIndex, ++index);
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find tags

        // one tag
        if(line.charAt(index) == '['){
            index = stopIndex += 2;
            while(true){
                index++;
                if(line.charAt(index) == '\''){
                    this.tags[0] = line.substring(stopIndex, index);
                    // += 2 cause of '],
                    stopIndex = index += 3;
                    break;
                }
            }

        // two tags
        } else {
            // += 3 to remove "['
            stopIndex = index += 3;
            while(true){
                index++;
                if(line.charAt(index) == '\''){
                    this.tags[0] = line.substring(stopIndex, index);
                    // += to remove ', '
                    stopIndex = index += 4;
                    break;
                }
            }
            while(true){
                index++;
                if(line.charAt(index) == '\''){
                    this.tags[1] = line.substring(stopIndex, index);
                    // += to remove ']",
                    stopIndex = index += 4;
                    break;
                }
            }
        }
        // --------------------------------- //

        // find partype

        while(true){
            index++;
            if(line.charAt(index) == ','){
                this.partype = line.substring(stopIndex, index);
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find attack

        while(true){
            index++;
            if(line.charAt(index) == ','){
                this.attack = Integer.parseInt(line.substring(stopIndex, index));
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find defense

        while(true){
            index++;
            if(line.charAt(index) == ','){
                this.defense = Integer.parseInt(line.substring(stopIndex, index));
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find magic

        while(true){
            index++;
            if(line.charAt(index) == ','){
                this.magic = Integer.parseInt(line.substring(stopIndex, index));
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find difficulty

        while(true){
            index++;
            if(line.charAt(index) == ','){
                this.difficulty = Integer.parseInt(line.substring(stopIndex, index));
                stopIndex = ++index;
                break;
            }
        }
        // --------------------------------- //

        // find date release

        while(true){
            index++;
            if(index == line.length()){
                setDate(line.substring(stopIndex, index));
                break;
            }
        }
        // --------------------------------- //
    }

    public byte[] transformToByte() throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        if(exist) dos.writeUTF(" ");
        else dos.writeUTF("*");
        dos.writeUTF(name);
        dos.writeUTF(title);
        dos.writeUTF(blurb);
        if(tags[1] != null){
            dos.writeInt(2);
            dos.writeUTF(tags[0]);
            dos.writeUTF(tags[1]);
        }else {
            dos.writeInt(1);
            dos.writeUTF(tags[0]);
        }
        dos.writeUTF(partype);
        dos.writeInt(attack);
        dos.writeInt(defense);
        dos.writeInt(magic);
        dos.writeInt(difficulty);
        dos.writeUTF(dateRelease.getDate());

        return baos.toByteArray();
    }

    public void transformToArray(byte ba[]) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        boolean res = true;
        id = dis.readInt();
        if(dis.readUTF().equals(" ")) exist = true;
        else exist = false;
        name = dis.readUTF();
        title = dis.readUTF();
        blurb = dis.readUTF();
        int tag = dis.readInt();
        tags[0] = dis.readUTF();
        if(tag == 2) tags[1] = dis.readUTF();
        partype = dis.readUTF();
        attack = dis.readInt();
        defense = dis.readInt();
        magic = dis.readInt();
        difficulty = dis.readInt();
        setDate(dis.readUTF());
    }
}