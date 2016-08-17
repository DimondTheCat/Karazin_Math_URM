package urm.Utilities;

/**
 * Created by Дом on 23.06.2016.
 */
public interface RegistersManager {

    Register getRegisterAtIndex(int index);
    int getCurrentOperation();
    void setCurrentOperation(int currentOperation);
}
