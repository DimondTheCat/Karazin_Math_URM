package urm.Utilities.operations;

import urm.Utilities.Register;
import urm.Utilities.RegistersManager;

/**
 * Created by Дом on 23.06.2016.
 */
public class Zero implements UrmOperation {

    public int registerIndex;

    public RegistersManager collection;

    @Override
    public void performOperation() {

        Register register = this.collection.getRegisterAtIndex(registerIndex);
        register.value = 0;

    }
}
