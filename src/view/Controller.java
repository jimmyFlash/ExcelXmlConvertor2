package view;


import androidtemplate.ExcelToXML;
import androidtemplate.XMLToExcel;
import config.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller{

    private Desktop desktop = Desktop.getDesktop();

    @FXML
    Button enxmlbtn, frxmlbtn, arxmlbtn, excelbtn, cnvrtxmltoecxel, cnvrtexceltoxml, outbtn;
    @FXML
    TextField entv, frtv, artv, exceltv;
    @FXML
    RadioButton xmlrb, excelrb;
    @FXML
    Label outputtv;


    String enFile, frFile, arFile, xlFile;

    String outputType = ".xslx";

    String outputLoc = "";

    public void initialize() {
        System.out.println("initialize");

        File file =  new File(System.getProperty("user.home"));
        outputLoc = file.getAbsolutePath();
        outputtv.setText(outputLoc);
    }


    /**
     * handler for the radio buttons click
     * @param ae
     */
    public void pressRB(ActionEvent ae){


        String btnID = ((RadioButton) ae.getTarget()).getId();

        switch (btnID){

            case "xmlrb":

                xmlrb.setSelected(true);
                excelrb.setSelected(false);

                enxmlbtn.setDisable(false);
                frxmlbtn.setDisable(false);
                arxmlbtn.setDisable(false);
                excelbtn.setDisable(true);


                entv.setDisable(false);
                frtv.setDisable(false);
                artv.setDisable(false);
                exceltv.setDisable(true);

                cnvrtxmltoecxel.setDisable(false);
                cnvrtexceltoxml.setDisable(true);

                outputType = ".xslx";

                break;
            case "excelrb":

                xmlrb.setSelected(false);
                excelrb.setSelected(true);

                enxmlbtn.setDisable(true);
                frxmlbtn.setDisable(true);
                arxmlbtn.setDisable(true);
                excelbtn.setDisable(false);

                entv.setDisable(true);
                frtv.setDisable(true);
                artv.setDisable(true);
                exceltv.setDisable(false);

                cnvrtxmltoecxel.setDisable(true);
                cnvrtexceltoxml.setDisable(false);

                outputType = ".xml";
                 

                break;

        }

    }

    /**
     * handler fot he xml to excel converter button click
     * @param ae
     */
    public void pressButton(ActionEvent ae){

        System.out.println("XML 2 EXCEL");

        ArrayList<String> filesList = new ArrayList<>();

        if(Constants.en_on && !enFile.isEmpty()){
            filesList.add(enFile);
        }
        if(Constants.fr_on && !frFile.isEmpty()){
            filesList.add(frFile);
        }
        if(Constants.ar_on && !arFile.isEmpty()){
            filesList.add(arFile);
        }

        if(filesList.size() > 0){
            new XMLToExcel(filesList, outputLoc);

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No .XML/.STRINGS Files selected");
            alert.setHeaderText(null);
            alert.setContentText("You have to choose at least 1 xml/strings file!");
            alert.showAndWait();
        }

        //XMLToExcel.main(null);
    }

    public void browseBtnClick(ActionEvent ae){

        System.out.println("browsing");

        String tgt = ((Button) ae.getTarget()).getId();

        switch (tgt){

            case "enxmlbtn":

                fileChosserDialog( ae, "Choose the .xml/.strings file from english locale", Constants.EN);

                break;
            case "frxmlbtn":

                fileChosserDialog( ae, "Choose the .xml/.strings file from french locale", Constants.FR );

                break;
            case "arxmlbtn":

                fileChosserDialog( ae, "Choose the .xml/.strings file from arabic locale", Constants.AR );

                break;
            case "excelbtn":

                fileChosserDialog( ae, "Choose the source excel file", Constants.EXCEL);

                break;
        }
    }

    public void fileChosserDialog(ActionEvent ae, String type, String filter ){


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(type);

        if(filter.equals(Constants.EXCEL)){
            filterFilesExcel( fileChooser );

        }else{
            filterFilesXml( fileChooser );
        }

        File file = fileChooser.showOpenDialog(((Button) ae.getTarget()).getParent().getScene().getWindow());

        if (file != null) {
            System.out.println(file.getAbsolutePath());
            // openFile(file);
            switch (filter){

                case Constants.EN:
                    Constants.en_on = true;
                    enFile = file.getAbsolutePath();
                    entv.setText(enFile);
                    break;
                case Constants.FR:
                    Constants.fr_on = true;
                    frFile = file.getAbsolutePath();
                    frtv.setText(frFile);
                    break;
                case Constants.AR:
                    Constants.ar_on = true;
                    arFile = file.getAbsolutePath();
                    artv.setText(arFile);
                    break;
                case Constants.EXCEL:
                    Constants.excel_on = true;
                    xlFile = file.getAbsolutePath();
                    exceltv.setText(xlFile);
                    break;
            }
        }else{
            switch (filter){

                case Constants.EN:
                    Constants.en_on = false;
                    enFile = "";
                    entv.setText(enFile);
                    break;
                case Constants.FR:
                    Constants.fr_on = false;
                    frFile = "";
                    frtv.setText(enFile);
                    break;
                case Constants.AR:
                    Constants.ar_on = false;
                    arFile = "";
                    artv.setText(enFile);
                    break;
                case Constants.EXCEL:
                    Constants.excel_on = false;
                    xlFile = "";
                    exceltv.setText(xlFile);
                    break;
            }
        }
    }

    public void pressButton2(ActionEvent ae){

        System.out.println("Testing tracer");

        if(!Constants.excel_on){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Excel file selected");
            alert.setHeaderText(null);
            alert.setContentText("You have to choose an excel file!");
            alert.showAndWait();
        }else {
            new ExcelToXML(xlFile, outputLoc);
            //ExcelToXML.main(null);
        }
    }

    public void saveToLocationPress(ActionEvent ae){
        directoryChosserDialog( ae, outputType );
    }



    public void fileMultipleChosserDialog(ActionEvent ae, String type ){


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(type);
        List<File> list =  fileChooser.showOpenMultipleDialog(((Button) ae.getTarget()).getParent().getScene().getWindow());

        if (list != null) {
            for (File file : list) {
                //openFile(file);
            }
        }
    }

    public void directoryChosserDialog(ActionEvent ae, String type ){

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(type);
        directoryChooser.setInitialDirectory( new File(System.getProperty("user.home")) );
        final File selectedDirectory = directoryChooser.showDialog(((Button) ae.getTarget()).getParent().getScene().getWindow());

        if (selectedDirectory != null) {
            outputLoc = selectedDirectory.getAbsolutePath();
            outputtv.setText(outputLoc);
        }

    }

    private void filterFilesXml(FileChooser fileChooser ){


        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Android (*.xml)", "*.xml"),
                new FileChooser.ExtensionFilter("IOS (*.strings)", "*.strings")
        );

    }

    private void filterFilesExcel(FileChooser fileChooser ){

        fileChooser.getExtensionFilters().addAll(  new FileChooser.ExtensionFilter("Ms excel (*.xlsx)", "*.xlsx"));

    }

    private void openFile(File file) {

        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void  saveFile(ActionEvent ae){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
//        System.out.println(pic.getId());
        File file = fileChooser.showSaveDialog(((Button) ae.getTarget()).getParent().getScene().getWindow());
        if (file != null) {
           /* try {
                ImageIO.write(SwingFXUtils.fromFXImage(pic.getImage(), null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }*/
        }

    }



}
