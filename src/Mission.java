public class Mission {
    private String planeModel;
    private String airportOrigin;
    private String airportDestination;
    private long timeOrigin;
    private long deadline;
    public long tempTime;

    public Mission(String planeModel, String airportOrigin, String airportDestination, long timeOrigin, long deadline) {
        this.planeModel = planeModel;
        this.airportOrigin = airportOrigin;
        this.airportDestination = airportDestination;
        this.timeOrigin = timeOrigin;
        this.deadline = deadline;
    }

    // Getters and Setters (you can generate these using your IDE)

    public String getPlaneModel() {
        return planeModel;
    }

    public String getAirportOrigin() {
        return airportOrigin;
    }

    public String getAirportDestination() {
        return airportDestination;
    }

    public long getTimeOrigin() {
        return timeOrigin;
    }

    public long getDeadline() {
        return deadline;
    }
}
