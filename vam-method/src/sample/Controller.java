package sample;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Controller {
    @FXML private TextField destinations;
    @FXML private TextField sources;
    TextField[][] matrix;

    int[] demand;
    int[] supply;
    int[][] costs;

    int numRows;
    int numCols;

    boolean[] rowAllocated;
    boolean[] colAllocated;
    int[][] result;

    @FXML
    public void createMatrix() {
        int d = Integer.parseInt(destinations.getText());
        int s = Integer.parseInt(sources.getText());

        matrix = new TextField[s][d];
        costs = new int[s][d];
        demand = new int[d];
        supply = new int[s];
        numRows = s;
        numCols = d;
        rowAllocated = new boolean[s];
        colAllocated = new boolean[d];
        result = new int[s][d];

        GridPane root = createGrid(true);

        for (int i = 0; i < s; i++) {
            root.add(new Label("Supply"), d + 1,0);
            TextField supply = new TextField();
            supply.setId("supply" + i);
            root.add(supply, d + 1, i + 1);
        }

        root.add(new Label("Demand"), 0, s + 2);
        for (int i = 0; i < d; i++) {
            TextField demand = new TextField();
            demand.setId("demand" + i);
            root.add(demand, i + 1, s + 2);
        }

        Button calculate = new Button();
        calculate.setText("Calculate");
        root.add(calculate, d + 1, s + 2);

        Stage stage = (Stage) destinations.getScene().getWindow();
        stage.setTitle("Matrix");
        Scene scene = new Scene(root, 800, 400);
        stage.setScene(scene);
        stage.show();

        calculate.setOnAction(actionEvent -> {
            for (int i = 0; i < s; i++) {
                for (int j = 0; j < d; j++) {
                    costs[i][j] = Integer.parseInt(matrix[i][j].getText());
                }
            }

            for (int i = 0; i < d; i++) {
                TextField tf = (TextField) scene.lookup("#demand" + i);
                demand[i] = Integer.parseInt(tf.getText());
            }

            for (int i = 0; i < s; i++) {
                TextField tf = (TextField) scene.lookup("#supply" + i);
                supply[i] = Integer.parseInt(tf.getText());
            }

            calculate();
        });
    }

    void display(int totalCost) {
        GridPane root = createGrid(false);
        root.add(new Label("Total Cost: " + totalCost), 0, numRows + 1);
        Stage stage = (Stage) matrix[0][0].getScene().getWindow();
        stage.setTitle("Result");
        Scene scene = new Scene(root, 240, 200);
        stage.setScene(scene);
        stage.show();
    }

    GridPane createGrid(boolean isInput) {
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));

        for (int i = 0; i < numCols; i++) {
            root.add(new Label("D"+(i+1)), i + 1, 0);
        }

        for (int i = 0; i < numRows; i++) {
            root.add(new Label("S"+(i+1)), 0, i + 1);

            for (int j = 0; j < numCols; j++) {
                if (isInput) {
                    matrix[i][j] = new TextField();
                    matrix[i][j].setId("cost" + numCols + numRows);
                    root.add(matrix[i][j], j + 1, i + 1);
                } else {
                    root.add(new Label(Integer.toString(result[i][j])), j + 1, i + 1);
                }
            }
        }

        return root;
    }

    void calculate() {
        int supplyLeft = 0;
        for (int j : supply) supplyLeft += j;

        int totalCost = 0;

        while (supplyLeft > 0) {
            int[] rowsDiff = maxDifference(numRows, numCols, true);
            int[] colsDiff = maxDifference(numCols, numRows, false);

            int[] cell = (rowsDiff[2] > colsDiff[2]) ? colsDiff : rowsDiff;
            int row = cell[0];
            int col = cell[1];

            int quantity = Math.min(demand[col], supply[row]);

            demand[col] -= quantity;
            if (demand[col] == 0)
                colAllocated[col] = true;

            supply[row] -= quantity;
            if (supply[row] == 0)
                rowAllocated[row] = true;

            result[row][col] = quantity;
            supplyLeft -= quantity;

            totalCost += quantity * costs[row][col];
        }

        display(totalCost);
    }

    int[] maxDifference(int x, int y, boolean isRow) {
        int maxDiff = -1;
        int minCostIndex = -1;
        int maxDiffIndex = -1;

        for (int i = 0; i < x; i++) {
            if (isRow ? rowAllocated[i] : colAllocated[i])
                continue;

            int firstMin = Integer.MAX_VALUE, secondMin = Integer.MAX_VALUE;
            int firstMinIndex = -1;

            for (int j = 0; j < y; j++) {
                if (isRow ? colAllocated[j] : rowAllocated[j])
                    continue;

                int cost = isRow ? costs[i][j] : costs[j][i];

                if (cost < firstMin) {
                    secondMin = firstMin;
                    firstMin = cost;
                    firstMinIndex = j;
                } else if (cost < secondMin)
                    secondMin = cost;
            }

            if (secondMin - firstMin > maxDiff) {
                maxDiff = secondMin - firstMin;
                maxDiffIndex = i;
                minCostIndex = firstMinIndex;
            }
        }

        return isRow ? new int[] {maxDiffIndex, minCostIndex, maxDiff} : new int[] {minCostIndex, maxDiffIndex, maxDiff};
    }
}
