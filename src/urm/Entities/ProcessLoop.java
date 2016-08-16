package urm.Entities;

import urm.Utilities.operations.UrmOperation;

import java.util.ArrayList;

/**
 * Created by Дом on 15.08.2016.
 */
public class ProcessLoop {

    //MARK: PROPERTIES
    public ProcessLoopDatasource datasource;
    public boolean isNeedToRunInDebug = false;

    private volatile boolean processThreadIsRunning = false;
    private volatile boolean isNeedToHaltThread = false;

    //List of operations that will be used in loop
    private ArrayList<UrmOperation> operations = null;
    private int currentOperationIndex = 0;

    private Thread backgroundLoop = null;

    //MARK: CONSTRUCTORS
    public ProcessLoop(){

        super();
    }

    //MARK: METHODS
    public void startEventLoop(){

        this.configureStartData();

        if (isNeedToRunInDebug){

        }else{

            //check , is there any loop in  background
            if (!this.processThreadIsRunning){

                this.backgroundLoop = new Thread("URM_BACKGROUND_PROCESS_LOOP"){

                    @Override
                    public void run(){

                        System.out.println(backgroundLoop.getName() + " STARTED ");

                        while (processThreadIsRunning){

                            //check for stop signal
                            if(!isNeedToHaltThread){

                                //events

                            }else{
                                break;
                            }
                        }

                        System.out.println(backgroundLoop.getName() + " HALT ");
                    }

                };

                this.processThreadIsRunning = true;
                this.backgroundLoop.start();
            }else{
                //assert error
            }



        }
    }

    public void stopEventLoop(){

        if (this.backgroundLoop != null) {
            this.isNeedToHaltThread = true;
        }

    }

    public void performCurrentOperation(){

        if (this.operations.size() < this.currentOperationIndex){

            this.operations.get(this.currentOperationIndex).performOperation();
        }else {
            //assert error
        }

    }

    public void nextStep(){

        if (this.isNextStep()){

            this.currentOperationIndex++;

        }else{
            //assert error
        }

    }

    //private
    private void configureStartData(){

        if (this.datasource != null){
            this.operations = this.datasource.getOperationsForProcessLoop();
        }

        currentOperationIndex = 0;
    }

    private boolean isNextStep(){

        if (this.currentOperationIndex + 1 >= this.operations.size()){
            return false;
        }else {
            return true;
        }

    }


}
