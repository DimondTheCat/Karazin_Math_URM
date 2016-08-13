package urm.Utilities;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by Дом on 23.06.2016.
 */
public class CompletionDatasource {

    private static CompletionDatasource completionInstance;

    public Hashtable<String,String> source = new Hashtable<>();
    public static CompletionDatasource sharedCompletion(){

        if (completionInstance == null){

            completionInstance = new CompletionDatasource();
        }

        return completionInstance;
    }

    private CompletionDatasource(){

        this.source.put("j","J( , , )");
        this.source.put("J","J( , , )");
        this.source.put("z","Z( )");
        this.source.put("Z","Z( )");
        this.source.put("t","T( , )");
        this.source.put("T","T( , )");
        this.source.put("s","S( )");
        this.source.put("S","S( )");

    }

    public boolean isHaveValueForCompletion(String s){

        return this.source.containsKey(s);
    }

    public String completionValue(String s){

        return this.source.get(s);
    }

}
