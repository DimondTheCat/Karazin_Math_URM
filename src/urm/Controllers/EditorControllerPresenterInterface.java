package urm.Controllers;

/**
 * Created by Дмитрий on 14.08.2016.
 */
public interface EditorControllerPresenterInterface {


    void setView(Controller view);

    //
    void viewInitializationCompeted();

    //tab bae

    //code editor

    //buttons bar
    void compileButtonPressed();

    void playButtonPressed();

    void stepButtonPressed();

    void stopButtonPressed();

    void resetButtonPressed();

    //registers
    void registersDidScroll();
}
