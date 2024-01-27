public class Weather {
    private String airfieldName;
    private long time;
    private int weatherCode;

    public Weather(String airfieldName, long time, int weatherCode) {
        this.airfieldName = airfieldName;
        this.time = time;
        this.weatherCode = weatherCode;
    }

    // Getters and Setters (you can generate these using your IDE)

    public String getAirfieldName() {
        return airfieldName;
    }

    public long getTime() {
        return time;
    }

    public int getWeatherCode() {
        return weatherCode;
    }
}
