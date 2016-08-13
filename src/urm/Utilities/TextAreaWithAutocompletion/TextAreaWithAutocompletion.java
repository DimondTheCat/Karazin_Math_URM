package urm.Utilities.TextAreaWithAutocompletion;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;

/**
 * Created by Дом on 11.05.2016.
 */
public class TextAreaWithAutocompletion extends TextArea {

    public TextAreaWithAutocompletion(){
        super();

        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

            }
            
        });
    }
}
