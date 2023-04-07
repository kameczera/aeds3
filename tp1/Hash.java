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

class Hash {
    public static void start(){
        RandomAccessFile hash = new RandomAccessFile("./tmp/hash/hash.db", "rw"); 
    }
}