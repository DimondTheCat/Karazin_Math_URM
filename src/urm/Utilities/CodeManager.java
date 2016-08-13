package urm.Utilities;

import javafx.scene.control.IndexRange;
import urm.Utilities.operations.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Дом on 23.06.2016.
 */


public class CodeManager implements  RegistersManager{

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

            singleInstance.zeroPatter = Pattern.compile( "Z\\((\\d)\\)" );
            singleInstance.increasePatter = Pattern.compile( "S\\((\\d)\\)");
            singleInstance.transactionPatter = Pattern.compile("T\\((\\d),(\\d)\\)");
            singleInstance.jumpPatter = Pattern.compile("J\\((\\d),(\\d),(\\d)\\)");
        }

        return singleInstance;
    }

    public void setupManagerWithText(String text){

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

            start = end;
        }

        this.configureOperations();

        this.currentOperation = 0;

    }

    private void configureOperations(){

        for (int i = 0 ; i < this.rows.size() ; i++){

            String row = this.rows.get(i);

            //separate row by comment line and get uncommented line
            int commentSignIndex = row.indexOf("//");
            commentSignIndex = commentSignIndex == -1 ? row.length() : commentSignIndex;

            String uncommentedPart = row.substring(0 , commentSignIndex);

            //if line contains just one symbol provide auto-complete popup
            String code = uncommentedPart.replaceAll("\\s+","");

            if (code.length() <= 0) {
                //finish
                emptyOperation operation = new emptyOperation();
                operation.collection = this;

                this.operations.add(operation);

            }else if (this.jumpPatter.matcher( code ).matches()){

                Jumping operation = new Jumping();
                operation.collection = this;

                int left = Integer.parseInt(this.jumpPatter.matcher(code).group(0));
                int center = Integer.parseInt(this.jumpPatter.matcher(code).group(1));
                int right = Integer.parseInt(this.jumpPatter.matcher(code).group(2));

                operation.leftRegisterIndex = left;
                operation.centerRegisterIndex = center;
                operation.jumpRowIndex = right;

                this.operations.add(operation);


            }else if (this.transactionPatter.matcher(code).matches()){

                Transaction operation = new Transaction();
                operation.collection = this;

                int left = Integer.parseInt(this.jumpPatter.matcher(code).group(0));
                int right = Integer.parseInt(this.jumpPatter.matcher(code).group(1));

                operation.leftToValue = left;
                operation.rightFromValue = right;

                this.operations.add(operation);

            }else if (this.increasePatter.matcher(code).matches()){

                Increment operation = new Increment();
                operation.collection = this;

                int value = Integer.parseInt(this.jumpPatter.matcher(code).group(0));

                operation.registerIndex = value;

                this.operations.add(operation);

            }else if (this.zeroPatter.matcher(code).matches()){

                Zero operation = new Zero();
                operation.collection = this;

                int value = Integer.parseInt(this.jumpPatter.matcher(code).group(0));

                operation.registerIndex = value;

                this.operations.add(operation);

            }else {
                this.delegate.errorWhileParsing(this , "No Mached Commands" , this.rowsRanges.get(i));
            }


        }
    }

    @Override
    public Register getRegisterAtIndex(int index) {
        return null;
    }

    @Override
    public int getOperationNumber() {
        return 0;
    }

    @Override
    public void setOperationNumber(int number) {

    }

    @Override
    public void finishReached() {

    }
}
