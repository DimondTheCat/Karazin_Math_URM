package urm.Utilities;

import javafx.scene.control.IndexRange;
import urm.Utilities.CodeManager;

/**
 * Created by Дом on 23.06.2016.
 */
public interface CodeManagerDelegate extends RegistersManager {

    void codeManagerCurrentOperationChanged(CodeManager sender , int operationNumber);

    void errorWhileParsing(CodeManager sender , String errorDescription , IndexRange rangeOfProblemRow);
}
