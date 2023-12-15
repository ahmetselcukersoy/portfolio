/*
Name : Ahmet Selcuk Ersoy
Student ID : 2020400087
Date : 25/03/2023

Reading data file and placing data into appropriate Arrays/Arraylists which are identified as "static"
Taking inputs
Checking weather the inputs are appropriate and defining from-destination points
Creating the canvas and map with drawMap function if inputs are appropriate
Determining the route by using FindWay function
Checking weather the mainWay is correct or not
    if not find connection between those stations with FindConnection function
Current station movement with Painting function
*/
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class AhmetSelcuk_Ersoy {

    public static void main(String[] args) throws IOException {
        String[] lines = new String[10];
        String[] linesRgb = new String[10];
        ArrayList<ArrayList<String>> allLineStations = new ArrayList<>();
        ArrayList<ArrayList<String>> allLineStationsCoordinates = new ArrayList<>();
        ArrayList<ArrayList<String>> pastStations = new ArrayList<>();
        ArrayList<ArrayList<String>> breakpointLinesList = new ArrayList<>();
        ArrayList<String> pastStationNames = new ArrayList<>();
        ArrayList<String> starStations = new ArrayList<>();
        ArrayList<String> breakpointList = new ArrayList<>();
        ArrayList<String> wayList = new ArrayList<>();
        int fromLine = -1;
        int destLine = -1;
        int fromStationNum = -1;
        int destStationNum = -1;
        FileInputStream fis = new FileInputStream("coordinates.txt");
        Scanner sc = new Scanner(fis);
        int lineCounter = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] lineSplit = line.split(" ");
            if (lineCounter < 20) {
                if (lineCounter % 2 == 0) {
                    lines[lineCounter / 2] = lineSplit[0];
                    linesRgb[lineCounter / 2] = lineSplit[1];
                } else {
                    ArrayList<String> lineStations = new ArrayList<>();
                    ArrayList<String> lineStationsCoordinates = new ArrayList<>();
                    for (int i = 0; i < lineSplit.length; i++) {
                        if (i % 2 == 0)
                            if (lineSplit[i].startsWith("*")) {
                                lineStations.add(lineSplit[i].substring(1));
                                starStations.add(lineSplit[i].substring(1));
                            } else
                                lineStations.add(lineSplit[i]);

                        else
                            lineStationsCoordinates.add(lineSplit[i]);
                    }
                    allLineStations.add(lineStations);
                    allLineStationsCoordinates.add(lineStationsCoordinates);
                }
            } else {
                breakpointList.add(lineSplit[0]);
                ArrayList<String> tempList = new ArrayList<>(Arrays.asList(lineSplit).subList(1, lineSplit.length));
                breakpointLinesList.add(tempList);
            }
            lineCounter++;
        }

        Scanner myObj = new Scanner(System.in);
        String from = myObj.nextLine();
        String destination = myObj.nextLine();
        myObj.close();

        for (int i = 0; i < 10; i++) {
            int size = allLineStations.get(i).size();
            for (int j = 0; j < size; j++) {
                if (from.equals(allLineStations.get(i).get(j))) {
                    fromLine = i;
                    fromStationNum = j;
                }
                if (destination.equals(allLineStations.get(i).get(j))) {
                    destLine = i;
                    destStationNum = j;
                }
            }
            if ((destLine != -1) && (destLine == fromLine))
                break;
        }
        if ((fromLine == -1) || (destLine == -1)) {
            System.out.println("The station names provided are not present in this map.\n");
            return;
        }
        if (((fromLine == 8) && (destLine != 8)) || ((fromLine != 8) && (destLine == 8))) {
            System.out.println("These two stations are not connected\n");
            return;
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(1024, 482);
        StdDraw.setXscale(0, 1024);
        StdDraw.setYscale(0, 482);
        drawMap(allLineStations, allLineStationsCoordinates, linesRgb, starStations, pastStations);

        wayList.add(fromLine + " " + fromStationNum);

        if (fromLine != destLine) {
            FindWay(fromLine, destLine, destStationNum,lines,allLineStations,breakpointList,
                    breakpointLinesList,wayList);
        } else {
            wayList.add(destLine + " " + destStationNum);
            Painting(fromLine, fromStationNum, destStationNum,allLineStations,allLineStationsCoordinates,
                    pastStationNames,linesRgb,starStations,pastStations);
        }

        ArrayList<String> mainWay = new ArrayList<>(wayList);
        int counter = 0;
        int fromLn = -1;
        int fromSt = -1;
        int destLn = -1;
        int destSt = -1;
        try {
            for (String s : mainWay) {
                String[] stop = s.split(" ");
                if (counter % 2 == 0) {
                    fromLn = Integer.parseInt(stop[0]);
                    fromSt = Integer.parseInt(stop[1]);
                } else {
                    destLn = Integer.parseInt(stop[0]);
                    destSt = Integer.parseInt(stop[1]);
                    if (destLn != fromLn) {
                        int index = (mainWay.indexOf(s));
                        FindConnection(mainWay,fromLn,fromSt,destLn,destSt,index,wayList,breakpointLinesList,
                                destLine,destStationNum,lines,allLineStations,breakpointList);
                    } else if (destSt == fromSt) {
                        mainWay.remove(counter);
                        mainWay.remove(counter-1);

                    }
                }
                if ((destLn == destLine) && (destSt == destStationNum))
                    break;
                counter++;
            }
        } catch (Exception ignored) {
        }

        counter = 0;
        fromLn = -1;
        fromSt = -1;
        destLn = -1;
        destSt = -1;
        System.out.println(mainWay);
        for (String s : mainWay) {
            String[] stop = s.split(" ");
            if (counter % 2 == 0) {
                fromLn = Integer.parseInt(stop[0]);
                fromSt = Integer.parseInt(stop[1]);
            } else {
                destLn = Integer.parseInt(stop[0]);
                destSt= Integer.parseInt(stop[1]);
                Painting(fromLn, fromSt, destSt,allLineStations,allLineStationsCoordinates,pastStationNames,linesRgb,
                        starStations,pastStations);
            }
            if ((destLn == destLine) && (destSt==destStationNum))
                break;
            counter++;
        }

    }
    static void FindConnection(ArrayList<String> mainWay,
                               int fromLn, int fromSt, int destLn, int destSt, int index,
                               ArrayList<String> wayList, ArrayList<ArrayList<String>> breakpointLinesList,
                               int destLine, int destStationNum,String[] lines,ArrayList<ArrayList<String>> allLineStations,
                               ArrayList<String> breakpointList){
        int counter = 0;
        wayList.clear();
        wayList.add(fromLn + " " + fromSt);
        FindWay(fromLn,destLn,destSt,lines,allLineStations,breakpointList,breakpointLinesList,wayList);
        wayList.remove(0);
        wayList.remove(wayList.size() - 1);
        mainWay.addAll(index, wayList);
        try {
            for (String s : mainWay) {
                String[] stop = s.split(" ");
                if (counter % 2 == 0) {
                    fromLn = Integer.parseInt(stop[0]);
                    fromSt = Integer.parseInt(stop[1]);
                } else {
                    destLn = Integer.parseInt(stop[0]);
                    destSt = Integer.parseInt(stop[1]);
                    if (destLn != fromLn) {
                        index = (mainWay.indexOf(s));
                        FindConnection(mainWay,fromLn,fromSt,destLn,destSt,index, wayList,
                                breakpointLinesList,destLine,destStationNum, lines, allLineStations,breakpointList);
                    } else if (destSt == fromSt) {
                        mainWay.remove(counter);
                        mainWay.remove(counter-1);
                    }
                }
                if ((destLn == destLine) && (destSt == destStationNum))
                    break;
                counter++;
            }
        } catch (Exception ignored) {
        }
    }

    static int FindWay(int from, int dest, int destNum,String[] lines,ArrayList<ArrayList<String>> allLineStations,
                       ArrayList<String> breakpointList, ArrayList<ArrayList<String>> breakpointLinesList,
                       ArrayList<String> wayList) {

        String destName = allLineStations.get(dest).get(destNum);
        ArrayList<String> linesList = new ArrayList<>(List.of(lines));
        ArrayList<ArrayList<String>> tempBpLst= new ArrayList<>();
        for (ArrayList<String> linesli : breakpointLinesList) {
            ArrayList<String> tempLst = new ArrayList<>(linesli);
            tempBpLst.add(tempLst);
        }
        for (String station : allLineStations.get(from)) {
            if (breakpointList.contains(station)) {
                int bpIndex = breakpointList.indexOf(station);
                ArrayList<String> bpLines = tempBpLst.get(bpIndex);
                String thisLine = linesList.get(from);
                bpLines.remove(thisLine);
                try {
                    for (String line : bpLines) {
                        int lineIndex = linesList.indexOf(line);
                        int bpIndexFrom = allLineStations.get(from).indexOf(station);
                        int bpIndexTo = allLineStations.get(lineIndex).indexOf(station);
                        if (allLineStations.get(lineIndex).contains(destName)) {
                                wayList.add(from + " " + bpIndexFrom);
                                wayList.add(lineIndex + " " + bpIndexTo);
                                wayList.add(lineIndex + " " + allLineStations.get(lineIndex).indexOf(destName));
                                return 1;
                        } else if (1 == FindWay(lineIndex, dest, destNum, lines, allLineStations,
                                breakpointList, tempBpLst, wayList)) {
                            wayList.add(1, from + " " + bpIndexFrom);
                            wayList.add(2, lineIndex + " " + bpIndexTo);
                            return 1;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return 0;
    }
    static void Painting ( int fromLine, int fromStationNum, int destStationNum, ArrayList<ArrayList<String>> allLineStations,
                           ArrayList<ArrayList<String>> allLineStationsCoordinates, ArrayList<String> pastStationNames,
                           String[] linesRgb, ArrayList<String> starStations, ArrayList<ArrayList<String>> pastStations){
        if (fromStationNum < destStationNum) {
            for (int k = fromStationNum; k < destStationNum + 1; k++) {
                String[] coordinates = allLineStationsCoordinates.get(fromLine).get(k).split(",");
                int currX = Integer.parseInt(coordinates[0]);
                int currY = Integer.parseInt(coordinates[1]);
                String stName = allLineStations.get(fromLine).get(k);
                if (pastStationNames.contains(stName))
                    continue;
                StdDraw.pause(300);
                System.out.println(stName);
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                StdDraw.setPenRadius(0.02);
                StdDraw.line(currX, currY, currX, currY);
                StdDraw.show();
                ArrayList<String> coords = new ArrayList<>(Arrays.asList(coordinates));
                pastStationNames.add(stName);
                pastStations.add(coords);
                drawMap(allLineStations, allLineStationsCoordinates, linesRgb, starStations, pastStations);
            }

        } else {
            for (int k = fromStationNum; k > destStationNum - 1; k--) {
                String[] coordinates = allLineStationsCoordinates.get(fromLine).get(k).split(",");
                int currX = Integer.parseInt(coordinates[0]);
                int currY = Integer.parseInt(coordinates[1]);
                String stName = allLineStations.get(fromLine).get(k);
                if (pastStationNames.contains(stName))
                    continue;
                StdDraw.pause(300);
                System.out.println(stName);
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                StdDraw.setPenRadius(0.02);
                StdDraw.line(currX, currY, currX, currY);
                StdDraw.show();
                ArrayList<String> coords = new ArrayList<>(Arrays.asList(coordinates));
                pastStationNames.add(stName);
                pastStations.add(coords);
                drawMap(allLineStations,allLineStationsCoordinates,linesRgb,starStations,pastStations);
            }
        }
    }
    static void drawMap(ArrayList<ArrayList<String>> allLineStations,
                        ArrayList<ArrayList<String>> allLineStationsCoordinates, String[] linesRgb,
                        ArrayList<String> starStations, ArrayList<ArrayList<String>> pastStations) {
        StdDraw.picture(512, 241, "background.jpg");
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
        for (int i = 0; i < 10; i++) {
            String line = linesRgb[i];
            String[] lineSplit = line.split(",");
            int red = Integer.parseInt(lineSplit[0]);
            int green = Integer.parseInt(lineSplit[1]);
            int blue = Integer.parseInt(lineSplit[2]);
            int lastX = 0;
            int lastY = 0;
            int currX;
            int currY;
            int size = allLineStations.get(i).size();
            for (int j = 0; j < size; j++) {
                String[] coordinates = allLineStationsCoordinates.get(i).get(j).split(",");
                if (lastX == 0) {
                    lastX = Integer.parseInt(coordinates[0]);
                    lastY = Integer.parseInt(coordinates[1]);
                } else {
                    currX = Integer.parseInt(coordinates[0]);
                    currY = Integer.parseInt(coordinates[1]);
                    StdDraw.setPenColor(red, green, blue);
                    StdDraw.setPenRadius(0.012);
                    StdDraw.line(lastX, lastY, currX, currY);
                    StdDraw.setPenRadius(0.01);
                    StdDraw.setPenColor(Color.white);
                    StdDraw.line(lastX, lastY, lastX, lastY);
                    lastX = currX;
                    lastY = currY;
                    StdDraw.setPenColor(Color.white);
                    StdDraw.line(lastX, lastY, lastX, lastY);

                }
            }
        }
        for (int i = 0; i < 10; i++) {
            int size = allLineStations.get(i).size();
            for (int j = 0; j < size; j++) {
                String name = allLineStations.get(i).get(j);
                String[] coordinates = allLineStationsCoordinates.get(i).get(j).split(",");
                int currX = Integer.parseInt(coordinates[0]);
                int currY = Integer.parseInt(coordinates[1]);
                if (starStations.contains(name)) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(currX, currY + 5, name);
                }

            }
        }
        for (ArrayList<String> pastCoords : pastStations) {
            int pastX = Integer.parseInt(pastCoords.get(0));
            int pastY = Integer.parseInt(pastCoords.get(1));
            StdDraw.setPenRadius(0.01);
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            StdDraw.line(pastX, pastY, pastX, pastY);
        }
    }
}
