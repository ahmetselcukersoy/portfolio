import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    private static Map<String, Airport> airportMap;
    private static List<Direction> directions;
    private static Map<String, Map<Long, Weather>> weatherMap;
    private static List<Mission> missions;

    public static void main(String[] args) {
        try {
            airportMap = readAirports(args[0]);
            directions = readDirections(args[1]);
            weatherMap = readWeatherData(args[2]);
            missions = readMissions(args[3]);

            processTask1(args[4]);
            processTask2(args[5]);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Airport> readAirports(String filename) throws IOException {
        airportMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Airport airport = new Airport(parts[0], parts[1], Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
                airportMap.put(airport.getAirportCode(), airport);
            }
        }
        return airportMap;
    }

    private static List<Direction> readDirections(String filename) throws IOException {
        directions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                directions.add(new Direction(parts[0], parts[1]));
            }
        }
        return directions;
    }

    private static Map<String, Map<Long, Weather>> readWeatherData(String filename) throws IOException {
        weatherMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Weather weather = new Weather(parts[0], Long.parseLong(parts[1]), Integer.parseInt(parts[2]));

                weatherMap.computeIfAbsent(weather.getAirfieldName(), k -> new HashMap<>()).put(weather.getTime(), weather);
            }
        }
        return weatherMap;
    }

    private static List<Mission> readMissions(String filename) throws IOException {
        missions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String planeModel = br.readLine(); // Read plane model
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                missions.add(new Mission(planeModel, parts[0], parts[1], Long.parseLong(parts[2]), Long.parseLong(parts[3])));
            }
        }
        return missions;
    }

    private static void processTask1(String outputFilename) throws IOException {
        try (FileWriter writer = new FileWriter(outputFilename)) {
            for (Mission mission : missions) {
                mission.tempTime = mission.getTimeOrigin();
                List<String> path = findPath(mission.getAirportOrigin(), mission.getAirportDestination(), mission,
                        "task1", mission.tempTime);
                double totalCost = calculateTotalCost(path, mission, "task1", mission.tempTime);
                writer.write(formatOutput(path, totalCost) + System.lineSeparator());
            }
        }
    }

    private static void processTask2(String outputFilename) throws IOException {
        try (FileWriter writer = new FileWriter(outputFilename)) {
            for (Mission mission : missions) {
                long tempTime = mission.getTimeOrigin();
                List<String> path = findPath(mission.getAirportOrigin(), mission.getAirportDestination(), mission,
                        "task2", tempTime);
                mission.tempTime = mission.getTimeOrigin();
                double totalCost = calculateTotalCost(path, mission, "task2", mission.tempTime);
                if (totalCost == 99999999) {
                    writer.write("No possible solution." + System.lineSeparator());
                }
                else
                    writer.write(formatOutput(path, totalCost) + System.lineSeparator());
            }
        }
    }

    // find path
    private static List<String> findPath(String origin, String destination,
                                         Mission mission, String task, long time) {
        Map<String, List<String>> graph = buildGraph();
        return dijkstra(graph, origin, destination, mission, task, time);
    }

    // build graph
    private static Map<String, List<String>> buildGraph() {
        Map<String, List<String>> graph = new HashMap<>();
        for (Direction direction : directions) {
            graph.computeIfAbsent(direction.getFrom(), k -> new ArrayList<>()).add(direction.getTo());
        }
        return graph;
    }

    // dijkstra for task1
    private static List<String> dijkstra(Map<String, List<String>> graph, String start, String end,
                                         Mission mission, String task, long time) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        Map<String, Double> costMap = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        Map<String, Integer> parkMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        priorityQueue.add(new Node(start, 0.0));
        costMap.put(start, 0.0);

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            String current = currentNode.getName();
            double currentCost = currentNode.getCost();

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            if (current.equals(end)) {
                List<String> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    String parent = parentMap.get(current);
                    if (parent != null) {
                        int parkCount = parkMap.getOrDefault(Arrays.asList(current, parent).toString(), 0);
                        for (int k = 0; k < parkCount; k++) {
                            path.add("PARK");
                        }
                    }
                    current = parent;
                }
                Collections.reverse(path);
                return path;
            }

            List<String> neighbors = graph.getOrDefault(current, Collections.emptyList());
            for (String neighbor : neighbors) {
                String[] temp = new String[2];
                temp[0] = current;
                temp[1] = neighbor;
                double flightCost = calculateTotalCost(List.of(temp), mission, task, time);

                if (task.equals("task2")) {
                    int parkCounter = 0;
                    double parkingCost = 0.0;
                    long tempTime = time;
                    while (tempTime < mission.getDeadline()) {
                        parkingCost += airportMap.get(current).getParkingCost();
                        parkCounter++;
                        tempTime += 6 * 60 * 60;
                        double flightCostWParking = calculateTotalCost(List.of(temp), mission, task, tempTime);
                        if (flightCostWParking + parkingCost < flightCost) {
                            flightCost = flightCostWParking + parkingCost;
                            parkMap.put(Arrays.asList(current, neighbor).toString(), parkCounter);
                        }
                    }
                }

                double newCost = currentCost + flightCost;

                if (!costMap.containsKey(neighbor) || newCost < costMap.get(neighbor)) {
                    costMap.put(neighbor, newCost);
                    priorityQueue.add(new Node(neighbor, newCost));
                    parentMap.put(neighbor, current);
                }
            }
        }
        return Collections.emptyList(); // No path found
    }

    // calculate total cost
    private static double calculateTotalCost(List<String> path, Mission mission, String task, long time) {
        double totalCost = 0.0;
        String from;
        String to = null;
        int i = 0;
        while (i<path.size()-1) {
            if (!path.get(i).equals("PARK")) {
                from = path.get(i);
                int j;
                for (j = i + 1; j < path.size(); j++) {
                    if (!path.get(j).equals("PARK")) {
                        to = path.get(j);
                        break;
                    }
                    totalCost += airportMap.get(from).getParkingCost();
                    time += 6 * 60 * 60;
                }
                i = j;

                Airport fromAirport = airportMap.get(from);
                Airport toAirport = airportMap.get(to);

                double distance = calculateDistance(fromAirport.getLatitude(), fromAirport.getLongitude(),
                        toAirport.getLatitude(), toAirport.getLongitude());
                double weatherD = calculateWeatherMultiplier(fromAirport.getAirfieldName(), time);
                if (task.equals("task2")) {
                    time += calculateFlightDuration(mission.getPlaneModel(), distance);
                }
                double weatherL = calculateWeatherMultiplier(toAirport.getAirfieldName(), time);

                double flightCost = 300 * weatherD * weatherL + distance;
                totalCost += flightCost;
            }
        }
        if (time > mission.getDeadline())
            return 99999999;
        return totalCost;
    }

    // calculate distance
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the Earth in kilometers
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * R * Math.asin(Math.sqrt(a));
    }

    // calculate weather multiplier with code
    private static double calculateWeatherMultiplier(int weatherCode) {
        int Bw = (weatherCode >> 4) & 1;
        int Br = (weatherCode >> 3) & 1;
        int Bs = (weatherCode >> 2) & 1;
        int Bh = (weatherCode >> 1) & 1;
        int Bb = weatherCode & 1;

        // Calculate weather multiplier
        return (Bw * 1.05 + (1 - Bw)) *
                (Br * 1.05 + (1 - Br)) *
                (Bs * 1.10 + (1 - Bs)) *
                (Bh * 1.15 + (1 - Bh)) *
                (Bb * 1.20 + (1 - Bb));
    }

    // calculate weather multiplier with airfield & time
    private static double calculateWeatherMultiplier(String airfieldName, long time) {
        Map<Long, Weather> timeMap = weatherMap.get(airfieldName);
        if (timeMap != null) {
            Weather weather = timeMap.get(time);
            if (weather != null) {
                int weatherCode = weather.getWeatherCode();
                return calculateWeatherMultiplier(weatherCode);
            }
        }
        return 0;
    }

    // calculate flight duration
    public static int calculateFlightDuration(String planeModel, double distance) {
        int duration = switch (planeModel) {
            case "Carreidas 160" -> (distance <= 175) ? 6 : ((distance <= 350) ? 12 : 18);
            case "Orion III" -> (distance <= 1500) ? 6 : ((distance <= 3000) ? 12 : 18);
            case "Skyfleet S570" -> (distance <= 500) ? 6 : ((distance <= 1000) ? 12 : 18);
            case "T-16 Skyhopper" -> (distance <= 2500) ? 6 : ((distance <= 5000) ? 12 : 18);
            default -> 0;
        };

        return duration*60*60;
    }

    // format output
    private static String formatOutput(List<String> path, double totalCost) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            result.append(path.get(i));
            if (i < path.size() - 1) {
                result.append(" ");
            }
        }
        result.append(" ").append(String.format("%.5f", totalCost));
        return result.toString();
    }
}
