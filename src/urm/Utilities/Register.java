package urm.Utilities;

import java.util.Observable;

/**
 * Created by Дом on 23.06.2016.
 */
public class Register extends Observable {

    public int index;
    public int value;

    public static Register[] createEmptyRegisters( int number){

        Register[] registers = new Register[number];

        for (int i = 0 ; i < number ; i++){

            registers[i] = new Register();
            registers[i].index = i;
            registers[i].value = 0;

        }

        return registers;
    }

    public void setValue(int value){

        this.value = value;

        setChanged();
        notifyObservers();
    }
}
