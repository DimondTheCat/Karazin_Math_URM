package urm.Utilities.operations;

import urm.Utilities.RegistersManager;

/**
 * Created by Дом on 24.06.2016.
 */
public class emptyOperation implements UrmOperation {

    public RegistersManager collection;

    @Override
    public void performOperation() {

//        this.collection.finishReached();
    }

    @Override
    public void performOperationInMain() {

    }
}
