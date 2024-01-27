public class Airport {
    private final String airportCode;
    private final String airfieldName;
    private final double latitude;
    private final double longitude;
    private final double parkingCost;

    public Airport(String airportCode, String airfieldName, double latitude, double longitude, double parkingCost) {
        this.airportCode = airportCode;
        this.airfieldName = airfieldName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingCost = parkingCost;
    }

    // Getters and Setters (you can generate these using your IDE)

    public String getAirportCode() {
        return airportCode;
    }

    public String getAirfieldName() {
        return airfieldName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getParkingCost() {
        return parkingCost;
    }
}
