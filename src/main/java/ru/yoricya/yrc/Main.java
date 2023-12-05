package ru.yoricya.yrc;

import static ru.yoricya.mvnd.phpfuncs.PHPFuncs.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        String scr = file_get_contents("Main.ysc");
        assert scr != null;
        Object jb = new YrC().Parse(scr).object;
        if(jb instanceof YrC.YrCException){
            ((YrC.YrCException)jb).printStackTrace();
        }
    }
}