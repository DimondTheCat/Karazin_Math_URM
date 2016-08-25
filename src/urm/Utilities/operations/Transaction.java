package urm.Utilities.operations;

import urm.Utilities.Register;
import urm.Utilities.RegistersManager;

/**
 * Created by Дом on 23.06.2016.
 */
public class Transaction implements UrmOperation {

    public int rightFromValue;
    public int leftToValue;

    public RegistersManager collection;

    @Override
    public void performOperation() {

        //get value of right register
        int value = this.collection.getRegisterAtIndex(leftToValue).value;

        //and fill it in left register
        Register destinationRegister = this.collection.getRegisterAtIndex(rightFromValue);
        destinationRegister.value = value;
//        destinationRegister.setValue(destinationRegister.value);
    }

    @Override
    public void performOperationInMain() {

        //get value of right register
        int value = this.collection.getRegisterAtIndex(leftToValue).value;

        //and fill it in left register
        Register destinationRegister = this.collection.getRegisterAtIndex(rightFromValue);
//        destinationRegister.value = value;
        destinationRegister.setValue(value);
    }

}
