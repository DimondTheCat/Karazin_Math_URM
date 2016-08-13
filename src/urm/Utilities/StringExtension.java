package urm.Utilities;

import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * Created by Дом on 23.06.2016.
 */
public class StringExtension {

    public static String getTextRowWithCharAtIndex(String text ,int index){

        int nextNewLineIndex = text.indexOf('\n',index);
        int privNewLineIndex = text.substring(0,index).lastIndexOf('\n');

        //next line -1 - there are no new line signs after this character
        nextNewLineIndex = nextNewLineIndex == -1 ? text.length() : nextNewLineIndex;
        privNewLineIndex = privNewLineIndex == -1 ? 0 : privNewLineIndex + 1;

        return text.substring(privNewLineIndex , nextNewLineIndex);
    }

    public static int getCursorPositionAtRowWithText(String text, String row , int index){

        int nextNewLineIndex = text.indexOf('\n',index);
        nextNewLineIndex = nextNewLineIndex == -1 ? text.length() : nextNewLineIndex;

        return index - (nextNewLineIndex -row.length());
    }

//    public static Bool isTextR
}
