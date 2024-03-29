package view;

import application.States;
import complexSignals.ComplexSignal;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FourierController implements Initializable {

    @FXML
    Pane w1UP,w1DOWN,w2UP,w2DOWN;



    public void createDiscreteChart(List<Double> arguments,List<Double> values, Pane pane){

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("czas");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        //defining a series
        XYChart.Series series = new XYChart.Series();
        XYChart.Series secSeries= new XYChart.Series();
        //populating the series with data
        for(int i =0;i<arguments.size();i++){
            series.getData().add(new XYChart.Data(arguments.get(i), values.get(i)));
        }
        lineChart.getData().add(series);


        lineChart.prefWidthProperty().bind(pane.widthProperty());
        lineChart.prefHeightProperty().bind(pane.heightProperty());
        pane.getChildren().clear();
        pane.getChildren().add(lineChart);
    }

    /*public void createDiscreteChart(List<Double> arguments,List<Double> values, Pane pane){

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final ScatterChart<Number,Number> dotChart = new
                ScatterChart<Number,Number>(xAxis,yAxis);

        dotChart.setLegendVisible(false);

        XYChart.Series series = new XYChart.Series();

        for(int i =0;i<arguments.size();i++){
            series.getData().add(new XYChart.Data(arguments.get(i), values.get(i)));
        }

        dotChart.prefWidthProperty().bind(pane.widthProperty());
        dotChart.prefHeightProperty().bind(pane.heightProperty());
        dotChart.getData().add(series);
        pane.getChildren().clear();
        pane.getChildren().add(dotChart);
    }
*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ComplexSignal complexSignal=States.getInstance().getComplexSignal();
        createDiscreteChart(complexSignal.getAllX(),complexSignal.getAllY(),w1UP);
        createDiscreteChart(complexSignal.getAllX(),complexSignal.getAllYI(),w1DOWN);
        createDiscreteChart(complexSignal.getAllX(),complexSignal.getAllComplexNumbersAbs(),w2UP);
        createDiscreteChart(complexSignal.getAllX(),complexSignal.getAllComplexNumbersArg(),w2DOWN);
    }
}
