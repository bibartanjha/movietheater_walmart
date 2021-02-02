import java.io.*;
import java.util.ArrayList;

public class MovieTheater {
    /*Algorithm Description
    To maximimize customer satisfaction, we will start from middle rows (E and F), then go out one row both ways

    To maximize safety, we will start from edges of rows and go inwards, that way we can keep 3 seat buffer while maximizing seats

     */

    static final int numRows = 10;
    static final int numSeatsPerRow = 20;

    static int[] rowsCurrentlyConsidering;
    static int currentRow; //which of the two rowsCurrentlyConsidering we are currently looking at

    static int firstOpenSeatOnLeft; //will be reset for each row
    static int firstOpenSeatOnRight; //will be reset for each row

    static boolean fillingSeatsFromLeft;

    public static boolean noMoreSeats (int requestSize) {
        return ((firstOpenSeatOnLeft > firstOpenSeatOnRight) || (firstOpenSeatOnRight - firstOpenSeatOnLeft < requestSize));
    }


    //returns false if no more rows available
    //also changes rowsCurrentlyConsidering and currentRow, and firstOpenSeatOnLeft and firstOpenSeatOnRight
    public static boolean switchRows () {
        if (currentRow == rowsCurrentlyConsidering[0]) {
            currentRow = rowsCurrentlyConsidering[1]; //switch to the other row we are currently considering
        }

        else { //both rows we are currently considering are filled up
            if (rowsCurrentlyConsidering[0] == 0 && rowsCurrentlyConsidering[1] == numRows - 1) {
                return false; //no more rows in theater
            }

            rowsCurrentlyConsidering[0] --;
            rowsCurrentlyConsidering[1] ++;
            currentRow = rowsCurrentlyConsidering[0];
        }

        firstOpenSeatOnLeft = 1;
        firstOpenSeatOnRight = numSeatsPerRow;

        return true;
    }



    //returns string with either seat assignment, or "no more seats"
    public static String assignSeats (String reservationName, int requestSize) {
        if (noMoreSeats(requestSize)) {
            boolean canBeSeated = switchRows();
            if (!canBeSeated) {
                return "No more seats";
            }
        }

        String seatAssignments = reservationName + " ";
        String rowLetter = Character.toString((char)(currentRow + 'A'));

        if (fillingSeatsFromLeft) {
            for (int i = 0; i < requestSize; i ++) {
                seatAssignments += rowLetter + (firstOpenSeatOnLeft + i) + ",";
            }

            firstOpenSeatOnLeft += requestSize + 3; //buffer
            fillingSeatsFromLeft = false; //fill seats from right side now
        }

        else {
            for (int i = 0; i < requestSize; i ++) {
                seatAssignments += rowLetter + (firstOpenSeatOnRight - i) + ",";
            }

            firstOpenSeatOnRight -= requestSize + 3; //buffer
            fillingSeatsFromLeft = true;
        }

        return seatAssignments;
    }


    public static void initializeValues () {
        rowsCurrentlyConsidering = new int[2];
        rowsCurrentlyConsidering[0] = numRows/2-1;
        rowsCurrentlyConsidering[1] = numRows/2;
        currentRow = rowsCurrentlyConsidering[0];

        firstOpenSeatOnLeft = 1;
        firstOpenSeatOnRight = numSeatsPerRow;
        fillingSeatsFromLeft = true;
    }



    public static void main (String[] args) {
        initializeValues ();

        //String input_file_path = "/Users/jhab1/IdeaProjects/movietheater_walmart/input.txt";
        //String output_file_path = "/Users/jhab1/IdeaProjects/movietheater_walmart/output.txt";
        String input_file_path = args[0];
        String output_file_path = args[1];


        //reading from input file
        BufferedReader reader;
        File outputFile;
        FileWriter outputFileWriter;
        String total_output = "";

        try {
            reader = new BufferedReader(new FileReader(input_file_path));
            outputFile = new File(output_file_path);
            outputFileWriter = new FileWriter(output_file_path);
            String line = reader.readLine();
            while (line != null) {
                String [] request = line.split(" ");
                String newAssignment = assignSeats(request[0], Integer.parseInt(request[1]));
                newAssignment = newAssignment.substring(0, newAssignment.length() - 1); //getting rid of last comma
                total_output += newAssignment + "\n";

                if (newAssignment.equals("No more seats")) {
                    break;
                }
                line = reader.readLine();
            }

            outputFileWriter.write(total_output);
            outputFileWriter.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
