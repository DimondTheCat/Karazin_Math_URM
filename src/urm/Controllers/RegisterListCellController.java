package urm.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import urm.Utilities.Register;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by Дом on 23.06.2016.
 */

public class RegisterListCellController implements Initializable , Observer {

    @FXML
    Label indexLabel;

    @FXML
    TextField valueTextField;

    public Register register;

    private Pattern pattern = Pattern.compile("^[0-9]*$");

    public void updateContent(){


        this.indexLabel.setText( Integer.toString(register.index) );
        this.valueTextField.setText( Integer.toString(register.value) );


    }

    public void setRegister(Register register) {

        try{
            register.deleteObserver(this);
        }catch (Exception ex){

        }

        this.register = register;
        register.addObserver(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        valueTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {


                if ( pattern.matcher(newValue).matches() ){

                    if (newValue.equalsIgnoreCase("")){
                        valueTextField.setText("");
                        register.value = 0;
                    }else {
                        valueTextField.setText(newValue);
                        register.value = Integer.parseInt(newValue);
                    }

                }else {

                    valueTextField.setText(oldValue);
                    register.value = Integer.parseInt(oldValue);

                }

            }
        });

    }

    @Override
    public void update(Observable o, Object arg) {

       Register register = o instanceof Register ? ((Register) o) : null;

        if (register != null){
            this.updateContent();
        }
    }
}
