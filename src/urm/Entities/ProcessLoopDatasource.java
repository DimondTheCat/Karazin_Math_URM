package urm.Entities;

import urm.Utilities.RegistersManager;
import urm.Utilities.operations.UrmOperation;

import java.util.ArrayList;

/**
 * Created by Дом on 16.08.2016.
 */
public interface ProcessLoopDatasource extends RegistersManager {

    ArrayList<UrmOperation> getOperationsForProcessLoop();

}
