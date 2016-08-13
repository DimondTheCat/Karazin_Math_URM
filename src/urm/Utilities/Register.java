package urm.Utilities;

/**
 * Created by Дом on 23.06.2016.
 */
public class Register {

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
}
