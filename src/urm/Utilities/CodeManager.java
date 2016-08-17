package urm.Utilities;

import javafx.scene.control.IndexRange;
import urm.Utilities.operations.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Дом on 23.06.2016.
 */


public class CodeManager {

    private static CodeManager singleInstance;

    public ArrayList<String> rows;
    public ArrayList<IndexRange> rowsRanges;
    public ArrayList<UrmOperation> operations;

    public CodeManagerDelegate delegate;

    public int currentOperation;

    private Pattern jumpPatter;
    private Pattern zeroPatter;
    private Pattern increasePatter;
    private Pattern transactionPatter;

    public static CodeManager sharedManager(){

        if (singleInstance == null){

            singleInstance = new CodeManager();

            singleInstance.zeroPatter = Pattern.compile( "Z\\s*[(]\\s*([0-9]+)\\s*[)]\\s*" );
            singleInstance.increasePatter = Pattern.compile( "S\\s*[(]\\s*([0-9]+)\\s*[)]\\s*");
            singleInstance.transactionPatter = Pattern.compile("T\\s*[(]\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*[)]\\s*");
            singleInstance.jumpPatter = Pattern.compile("J\\s*[(]\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*[)]\\s*");
        }

        return singleInstance;
    }

    public boolean setupManagerWithText(String text){

        int start = 0;
        int end = 0;

        this.rows = new ArrayList<String>();
        this.rowsRanges = new ArrayList<IndexRange>();
        this.operations = new ArrayList<UrmOperation>();

        while (end < text.length()-1){

            String row = StringExtension.getTextRowWithCharAtIndex(text , start);
            end = start + row.length();


            this.rows.add(row);
            this.rowsRanges.add(new IndexRange(start,end));

            start = end + 1;
        }

        this.currentOperation = 0;

        return this.configureOperations();

    }

    private boolean configureOperations(){

        for (int i = 0 ; i < this.rows.size() ; i++){

            String row = this.rows.get(i);

            //separate row by comment line and get uncommented line
            int commentSignIndex = row.indexOf("//");
            commentSignIndex = commentSignIndex == -1 ? row.length() : commentSignIndex;

            String uncommentedPart = row.substring(0 , commentSignIndex);

            //if line contains just one symbol provide auto-complete popup
            String code = uncommentedPart;

            if (code.length() <= 0) {
                //finish
                emptyOperation operation = new emptyOperation();
                operation.collection = delegate;

                this.operations.add(operation);

            }else if (this.jumpPatter.matcher( code ).matches()){

                Matcher matcher = this.jumpPatter.matcher(code);
                matcher.matches();

                Jumping operation = new Jumping();
                operation.collection = delegate;

                int left = Integer.parseInt(matcher.group(1));
                int center = Integer.parseInt(matcher.group(2));
                int right = Integer.parseInt(matcher.group(3));

                operation.leftRegisterIndex = left;
                operation.centerRegisterIndex = center;
                operation.jumpRowIndex = right;

                this.operations.add(operation);


            }else if (this.transactionPatter.matcher(code).matches()){

                Matcher matcher = this.transactionPatter.matcher(code);
                matcher.matches();

                Transaction operation = new Transaction();
                operation.collection = delegate;

                int left = Integer.parseInt(matcher.group(1));
                int right = Integer.parseInt(matcher.group(2));

                operation.leftToValue = left;
                operation.rightFromValue = right;

                this.operations.add(operation);

            }else if (this.increasePatter.matcher(code).matches()){

                Matcher matcher = this.increasePatter.matcher(code);
                matcher.matches();

                Increment operation = new Increment();
                operation.collection = delegate;

                int value = Integer.parseInt(matcher.group(1));

                operation.registerIndex = value;

                this.operations.add(operation);

            }else if (this.zeroPatter.matcher(code).matches()){

                Matcher matcher = this.zeroPatter.matcher(code);
                matcher.matches();

                Zero operation = new Zero();
                operation.collection = delegate;

                int value = Integer.parseInt(matcher.group(1));

                operation.registerIndex = value;

                this.operations.add(operation);

            }else {
                this.delegate.errorWhileParsing(this , "No Mached Commands" , this.rowsRanges.get(i));
                return false;
            }


        }

        return true;
    }


}
