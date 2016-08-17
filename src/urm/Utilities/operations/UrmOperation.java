package urm.Utilities.operations;

import urm.Utilities.RegistersManager;

/**
 * Created by Дом on 23.06.2016.
 */
public interface UrmOperation {

    RegistersManager collection = null;

    void performOperation();
    void performOperationInMain();

}
