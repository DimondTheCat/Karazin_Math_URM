package urm.Utilities.operations;

import urm.Utilities.Register;
import urm.Utilities.RegistersManager;

/**
 * Created by Дом on 23.06.2016.
 */
public class Jumping implements UrmOperation {

    public RegistersManager collection;

    public int leftRegisterIndex;
    public int centerRegisterIndex;
    public int jumpRowIndex;

    @Override
    public void performOperation() {

//        this.collection.finishReached();
        Register register1 = this.collection.getRegisterAtIndex(this.leftRegisterIndex);
        Register register2 = this.collection.getRegisterAtIndex(this.centerRegisterIndex);

        if (register1.value == register2.value){

            this.collection.setCurrentOperation(jumpRowIndex-1);
        }


    }

}
