package view;

import application.Loader;
import application.SignalType;
import application.States;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import signals.Signal;
import signals.SignalsCalculator;


import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.UnaryOperator;

public class Controller implements Initializable {

    private Loader signalLoader = new Loader();
    private SignalsCalculator calculator;
    private Signal signal;
    private SignalType type;

    String pathToFile= new String();
    String pathfirstSignal, pathsecondSignal;

    List<Label> labelList= Arrays.asList(
            new Label("Amplituda (A):"),
            new Label("Czas początkowy (t1):"),
            new Label("Czas trwania (d):"),
            new Label("Okres podstawowy (T):"),
            new Label("Częstotliwość próbkowania (f):"),
            new Label("Współczynnik wypełnienia (kw):"),
            new Label("Skok jednostkowy (ts):"),
            new Label("Numer pierwszej próbki (n1):"),
            new Label("Numer ostatniej próbki (n2)"),
            new Label("Skok dla próbki nr (ns):"),
            new Label("Prawdopodobieństwo (p):"));

    List <TextField> textFieldsList=new ArrayList<>();

    @FXML
    ChoiceBox menu;
    @FXML
    VBox labels;
    @FXML
    VBox textBoxes;
    @FXML
    Button generateButton;
    @FXML TextField PathToLoadFile, loadedFirstFilePath, loadedSecondFilePath, avg, avg2, avgPow, effVal, variance;

    public String loadFile(TextField tf, boolean ifLoaded) throws IOException {

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll();
        File selectedFile = fc.showOpenDialog(null);
        String path = selectedFile.getAbsolutePath();
        DecimalFormat f = new DecimalFormat("0.###E0");
        DecimalFormat df=new java.text.DecimalFormat(); //tworzymy obiekt DecimalFormat
        df.setMaximumFractionDigits(3); //dla df ustawiamy największą ilość miejsc po przecinku
        df.setMinimumFractionDigits(3);
        if (selectedFile != null) {
            tf.setText(path);
            if (ifLoaded) {

                signal= signalLoader.load(path);
                menu.setValue(signal.getType());
                fillTextFields();
                signal.average();
                signal.absoluteMean();
                signal.averagePower();
                signal.effectiveValue();
                signal.variance();


                avg.setText(String.valueOf(f.format(signal.getAverage())));
                avg2.setText(String.valueOf(df.format(signal.getAbsoluteMean())));
                avgPow.setText(String.valueOf(df.format(signal.getAveragePower())));
                effVal.setText(String.valueOf(df.format(signal.getEffectiveValue())));
                variance.setText(String.valueOf(df.format(signal.getVariance())));
                Stage stage = new Stage();
                Parent root = null;
                FXMLLoader loader = new FXMLLoader();
                States.getInstance().setSignal(signal);
                root = FXMLLoader.load(getClass().getClassLoader().getResource("chart.fxml"));
                Scene scene = new Scene(root);
                stage.setTitle("Loaded signal charts");
                stage.setScene(scene);
                stage.show();
            }
            } else {
                System.out.println("file is not valid");
            }

        return path;
    }

    public void readFile(ActionEvent actionEvent) throws IOException {
       pathToFile=loadFile(PathToLoadFile, true);
    }

    public void loadFirstSignal(ActionEvent actionEvent) throws IOException {
        pathfirstSignal =loadFile(loadedFirstFilePath,false);
    }

    public void loadSecondSignal(ActionEvent actionEvent) throws IOException {
        pathsecondSignal =loadFile(loadedSecondFilePath,false);
    }

    public void saveFile() throws IOException {


        Date date = new Date();
        String saveNameFile = date.toString();
        saveNameFile = saveNameFile.replaceAll("[\\s|:]+","");
        PrintWriter saver = new PrintWriter(saveNameFile);
        saver.println(signal.getType().name());
        saver.println(signal.getAmplitude());
        saver.println(signal.getInitialTime());
        saver.println(signal.getLastTime());
        saver.println(signal.getBasicPeriod());
        saver.println(signal.getFillFactor());
        saver.println(signal.getEntityChange());
        saver.println(signal.getFirstSampleNr());
        saver.println(signal.getLastSampleNr());
        saver.println(signal.getChangeForSample());
        saver.println(signal.getFrequency());
        saver.println(signal.getProbability());

        for (int i = 0; i < signal.getX().size(); i++) {
            saver.println(signal.getX().get(i) + " "+ signal.getY().get(i));
        }
        saver.close();

    }

    public void fillTextFields(){

        switch (type){

            case noiseUniDis:
                textFieldsList.get(0).setText(String.valueOf(signal.getAmplitude()));
                textFieldsList.get(1).setText (String.valueOf(signal.getInitialTime()));
                textFieldsList.get(2).setText (String.valueOf(signal.getLastTime()));
                textFieldsList.get(3).setText (String.valueOf(signal.getFrequency()));
                break;

            case noiseGauss:
                textFieldsList.get(0).setText(String.valueOf(signal.getInitialTime()));
                textFieldsList.get(1).setText(String.valueOf(signal.getLastTime()));
                textFieldsList.get(2).setText(String.valueOf(signal.getFrequency()));
                break;

            case sin:
            case sinOneHalf:
            case sinTwoHalf:
                textFieldsList.get(0).setText(String.valueOf(signal.getAmplitude()));
                textFieldsList.get(1).setText(String.valueOf(signal.getInitialTime()));
                textFieldsList.get(2).setText(String.valueOf(signal.getLastTime()));
                textFieldsList.get(3).setText(String.valueOf(signal.getBasicPeriod()));
                textFieldsList.get(4).setText(String.valueOf(signal.getFrequency()));
                break;

            case rec:
            case recSym:
            case tri:
                textFieldsList.get(0).setText(String.valueOf(signal.getAmplitude()));
                textFieldsList.get(1).setText(String.valueOf(signal.getInitialTime()));
                textFieldsList.get(2).setText(String.valueOf(signal.getLastTime()));
                textFieldsList.get(3).setText(String.valueOf(signal.getBasicPeriod()));
                textFieldsList.get(4).setText(String.valueOf(signal.getFrequency()));
                textFieldsList.get(5).setText(String.valueOf(signal.getFillFactor()));
                break;
            case entityChange:
                textFieldsList.get(0).setText(String.valueOf(signal.getAmplitude()));
                textFieldsList.get(1).setText(String.valueOf(signal.getInitialTime()));
                textFieldsList.get(2).setText(String.valueOf(signal.getLastTime()));
                textFieldsList.get(3).setText(String.valueOf(signal.getFrequency()));
                textFieldsList.get(4).setText(String.valueOf(signal.getEntityChange()));
                break;
            case entityImpulse:

                textFieldsList.get(0).setText(String.valueOf(signal.getFirstSampleNr()));
                textFieldsList.get(1).setText(String.valueOf(signal.getLastSampleNr()));
                textFieldsList.get(2).setText(String.valueOf(signal.getChangeForSample()));
                break;
            case noiseImpulse:
                textFieldsList.get(0).setText(String.valueOf(signal.getFirstSampleNr()));
                textFieldsList.get(1).setText(String.valueOf(signal.getLastSampleNr()));
                textFieldsList.get(2).setText(String.valueOf(signal.getProbability()));
                break;
        }

    }

    public void choose(){

        labels.spacingProperty().setValue(19);
        labels.setPadding(new Insets(0,0,0,20));
        labels.getChildren().clear();
        textBoxes.spacingProperty().setValue(10);
        textBoxes.setPadding(new Insets(0,10,0,10));
        textBoxes.getChildren().clear();
        textFieldsList.clear();
        switch (type){

            case noiseUniDis:
                labels.getChildren().addAll(Arrays.asList(
                        labelList.get(0),
                        labelList.get(1),
                        labelList.get(2),
                        labelList.get(4)));
                break;

            case noiseGauss:
                labels.getChildren().addAll(Arrays.asList(
                        labelList.get(1),
                        labelList.get(2),
                        labelList.get(4)));
                break;

            case sin:
            case sinOneHalf:
            case sinTwoHalf:
                labels.getChildren().addAll(Arrays.asList(
                        labelList.get(0),
                        labelList.get(1),
                        labelList.get(2),
                        labelList.get(3),
                        labelList.get(4)));
                break;

            case rec:
            case recSym:
            case tri:
                labels.getChildren().addAll(Arrays.asList(
                        labelList.get(0),
                        labelList.get(1),
                        labelList.get(2),
                        labelList.get(3),
                        labelList.get(4),
                        labelList.get(5)));

                break;
            case entityChange:
                labels.getChildren().addAll(Arrays.asList(
                        labelList.get(0),
                        labelList.get(1),
                        labelList.get(2),
                        labelList.get(4),
                        labelList.get(6)));

                break;
            case entityImpulse:
                labels.getChildren().addAll(Arrays.asList(
                        labelList.get(7),
                        labelList.get(8),
                        labelList.get(9)));
                break;
            case noiseImpulse:
                labels.getChildren().addAll(Arrays.asList(
                        labelList.get(7),
                        labelList.get(8),
                        labelList.get(10)));
                break;
        }

        for(int i =0;i<labels.getChildren().size();i++){
            TextField tmp=new TextField();
            tmp.setTextFormatter(new TextFormatter<>(filter));
            textFieldsList.add(tmp);
            textBoxes.getChildren().add(tmp);
        }
    }

    public void setParameters(){

        signal=new Signal();
        signal.setType(type);
        switch (type){

            case noiseUniDis:
                signal.setAmplitude(Double.parseDouble(textFieldsList.get(0).getText()));
                signal.setInitialTime(Double.parseDouble(textFieldsList.get(1).getText()));
                signal.setLastTime(Double.parseDouble(textFieldsList.get(2).getText()));
                signal.setFrequency(Double.parseDouble(textFieldsList.get(3).getText()));
                break;

            case noiseGauss:
                signal.setInitialTime(Double.parseDouble(textFieldsList.get(0).getText()));
                signal.setLastTime(Double.parseDouble(textFieldsList.get(1).getText()));
                signal.setFrequency(Double.parseDouble(textFieldsList.get(2).getText()));
                break;

            case sin:
            case sinOneHalf:
            case sinTwoHalf:
                signal.setAmplitude(Double.parseDouble(textFieldsList.get(0).getText()));
                signal.setInitialTime(Double.parseDouble(textFieldsList.get(1).getText()));
                signal.setLastTime(Double.parseDouble(textFieldsList.get(2).getText()));
                signal.setBasicPeriod(Double.parseDouble(textFieldsList.get(3).getText()));
                signal.setFrequency(Double.parseDouble(textFieldsList.get(4).getText()));
                break;

            case rec:
            case recSym:
            case tri:
                signal.setAmplitude(Double.parseDouble(textFieldsList.get(0).getText()));
                signal.setInitialTime(Double.parseDouble(textFieldsList.get(1).getText()));
                signal.setLastTime(Double.parseDouble(textFieldsList.get(2).getText()));
                signal.setBasicPeriod(Double.parseDouble(textFieldsList.get(3).getText()));
                signal.setFrequency(Double.parseDouble(textFieldsList.get(4).getText()));
                signal.setFillFactor(Double.parseDouble(textFieldsList.get(5).getText()));
                break;
            case entityChange:
                signal.setAmplitude(Double.parseDouble(textFieldsList.get(0).getText()));
                signal.setInitialTime(Double.parseDouble(textFieldsList.get(1).getText()));
                signal.setLastTime(Double.parseDouble(textFieldsList.get(2).getText()));
                signal.setFrequency(Double.parseDouble(textFieldsList.get(3).getText()));
                signal.setEntityChange(Double.parseDouble(textFieldsList.get(4).getText()));
                break;
            case entityImpulse:

                signal.setFirstSampleNr(Integer.parseInt(textFieldsList.get(0).getText()));
                signal.setLastSampleNr(Integer.parseInt(textFieldsList.get(1).getText()));
                signal.setChangeForSample(Integer.parseInt(textFieldsList.get(2).getText()));
                break;
            case noiseImpulse:
                signal.setFirstSampleNr(Integer.parseInt(textFieldsList.get(0).getText()));
                signal.setLastSampleNr(Integer.parseInt(textFieldsList.get(1).getText()));
                signal.setProbability(Double.parseDouble(textFieldsList.get(2).getText()));
                break;
        }

        signal.generateSignal();
    }

    @FXML
    public void handleGenerateButton(javafx.event.ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader=new FXMLLoader();
        setParameters();
        States.getInstance().setSignal(signal);
        root = FXMLLoader.load(getClass().getClassLoader().getResource("chart.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Signal charts");
        stage.setScene(scene);
        stage.show();

        DecimalFormat f = new DecimalFormat("0.###E0");
        DecimalFormat df=new java.text.DecimalFormat(); //tworzymy obiekt DecimalFormat
        df.setMaximumFractionDigits(3); //dla df ustawiamy największą ilość miejsc po przecinku
        df.setMinimumFractionDigits(3);

        signal.average();
        signal.absoluteMean();
        signal.averagePower();
        signal.effectiveValue();
        signal.variance();


        avg.setText(String.valueOf(f.format(signal.getAverage())));
        avg2.setText(String.valueOf(df.format(signal.getAbsoluteMean())));
        avgPow.setText(String.valueOf(df.format(signal.getAveragePower())));
        effVal.setText(String.valueOf(df.format(signal.getEffectiveValue())));
        variance.setText(String.valueOf(df.format(signal.getVariance())));
    }

    @FXML
    public void handleAddSignalsButton(ActionEvent actionEvent) throws IOException {

        Signal firstSignal=signalLoader.load(pathfirstSignal);
        Signal secondSignal=signalLoader.load(pathsecondSignal);
        calculator=new SignalsCalculator(firstSignal,secondSignal);
        calculator.addSignals();

        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader=new FXMLLoader();
        States.getInstance().setSignal(calculator.getCalculatedSignal());
        root = FXMLLoader.load(getClass().getClassLoader().getResource("chart.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Final chart");
        stage.setScene(scene);
        stage.show();
    }

    public void handleSubtractSignalsButton(ActionEvent actionEvent) throws IOException {
        calculator=new SignalsCalculator(signalLoader.load(pathfirstSignal),signalLoader.load(pathsecondSignal));
        calculator.subtractSignals();

        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader=new FXMLLoader();
        States.getInstance().setSignal(calculator.getCalculatedSignal());
        root = FXMLLoader.load(getClass().getClassLoader().getResource("chart.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Final chart");
        stage.setScene(scene);
        stage.show();
    }

    public void handleMultiplySignalsButton(ActionEvent actionEvent) throws IOException {
        calculator=new SignalsCalculator(signalLoader.load(pathfirstSignal),signalLoader.load(pathsecondSignal));
        calculator.multiplySignals();

        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader=new FXMLLoader();
        States.getInstance().setSignal(calculator.getCalculatedSignal());
        root = FXMLLoader.load(getClass().getClassLoader().getResource("chart.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Final chart");
        stage.setScene(scene);
        stage.show();
    }

    public void handleDivideSignalsButton(ActionEvent actionEvent) throws IOException {
        calculator=new SignalsCalculator(signalLoader.load(pathfirstSignal),signalLoader.load(pathsecondSignal));
        calculator.divideSignals();

        Stage stage = new Stage();
        Parent root = null;
        FXMLLoader loader=new FXMLLoader();
        States.getInstance().setSignal(calculator.getCalculatedSignal());
        root = FXMLLoader.load(getClass().getClassLoader().getResource("chart.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Final chart");
        stage.setScene(scene);
        stage.show();
    }

    UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {

        @Override
        public TextFormatter.Change apply(TextFormatter.Change t) {

            if (t.isReplaced())
                if(t.getText().matches("[^0-9]"))
                    t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));


            if (t.isAdded()) {
                if (t.getControlText().contains(".")) {
                    if (t.getText().matches("[^0-9]")) {
                        t.setText("");
                    }
                } else if (t.getText().matches("[^0-9.]")) {
                    t.setText("");
                }
            }

            return t;
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        menu.setItems( FXCollections.observableArrayList( SignalType.values()));
        menu.setValue(SignalType.noiseUniDis);
        this.type=(SignalType) menu.getValue();
        choose();
        menu.getSelectionModel().selectedItemProperty().addListener((obs,oldValue,newValue)->{
            this.type=(SignalType) menu.getValue();
            choose();
        });

    }

}