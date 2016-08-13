package urm.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import urm.Utilities.Register;

/**
 * Created by Дом on 23.06.2016.
 */

public class RegisterListCellController {

    @FXML
    Label indexLabel;

    @FXML
    TextField valueTextField;

    public Register register;

    public void updateContent(){

        this.indexLabel.setText( Integer.toString(register.index) );
        this.valueTextField.setText( Integer.toString(register.value) );
    }

}
