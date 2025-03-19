package ApiService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherAppService {
    public static JsonObject getWeatherData(String locationName, int plusHours) {
        JsonObject weatherData = null;
        JsonArray locationData = getLocationData(locationName);

        assert locationData != null;
        JsonObject location = (JsonObject) locationData.get(0);
        double latitude = (double) location.get("latitude").getAsDouble();
        double longitude = (double) location.get("longitude").getAsDouble();

        String urlLanLonString = "https://api.open-meteo.com/v1/forecast?latitude=" +
                latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try {
            HttpURLConnection connection = fetchApiResponse(urlLanLonString);

            assert connection != null;
            if (connection.getResponseCode() != 200) {
                System.out.println("Can not connect to given Latitude and Longitude Api");
                return null;
            }

            //Retrieving Json
            JsonObject resultJsonObject = retrieveJsonObject(connection);

            // Retrieving timezone
            String timezone = resultJsonObject.get("timezone").getAsString();

            //Retrieving hourly data
            JsonObject hourly = (JsonObject) resultJsonObject.get("hourly");

            //Retrieving time data
            JsonArray time = (JsonArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time, timezone);
            index = index + plusHours;

            //Retrieving temperature data
            JsonArray temperatureData = (JsonArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index).getAsDouble();

            //Retrieving weather code
            JsonArray weatherCode = (JsonArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index).getAsLong());

            //Retrieving humidity info
            JsonArray relativeHumidity = (JsonArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index).getAsLong();

            //Retrieving wing speed
            JsonArray windSpeedData = (JsonArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index).getAsDouble();

            weatherData = new JsonObject();
            weatherData.addProperty("timezone", timezone);
            weatherData.addProperty("temperature", temperature);
            weatherData.addProperty("weather_condition", weatherCondition);
            weatherData.addProperty("humidity", humidity);
            weatherData.addProperty("wind_speed", windSpeed);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return weatherData;
    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "clear";
        } else if (weatherCode <= 3L && weatherCode > 0L) {
            weatherCondition = "cloudy";
        } else if (weatherCode <= 48L && weatherCode >= 45L) {
            weatherCondition = "foggy";
        } else if (weatherCode >= 51 && weatherCode <= 65) {
            weatherCondition = "drizzle";
        } else if ((weatherCode >= 66L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 82L)) {
            weatherCondition = "rain";
        } else if ((weatherCode >= 71L && weatherCode <= 77L) || (85L <= weatherCode && weatherCode <= 86)) {
            weatherCondition = "snow";
        } else if (weatherCode >= 95L && weatherCode <= 99L) {
            weatherCondition = "thunderstorm";
        }
        return weatherCondition;
    }

    private static int findIndexOfCurrentTime(JsonArray timeList, String timeZone) {
        String currentTime = getCurrentTime(timeZone);

        for (int i = 0; i < timeList.size(); i++) {
            String time = timeList.get(i).getAsString();
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0;
    }

    //Change this to read data from Api - Retrieve time zone and depends on that retrieve the time
    public static String getCurrentTime(String timeZone) {
        ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of(timeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return localTime.format(formatter);
    }

    public static JsonArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection connection = fetchApiResponse(urlString);
            assert connection != null;
            if (connection.getResponseCode() != 200) {
                System.out.println("Cam not connect to API");
                return null;
            }

            JsonObject resultJsonObj = retrieveJsonObject(connection);

            return (JsonArray) resultJsonObj.get("results"); //Retrieving all location info
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    //Opening Http connection with a given URL
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Converting results into Json object
    private static JsonObject retrieveJsonObject(HttpURLConnection connection) throws IOException {
        StringBuilder resultJson = new StringBuilder();
        Scanner scanner = new Scanner(connection.getInputStream());
        while (scanner.hasNext()) {
            resultJson.append(scanner.nextLine());
        }
        scanner.close();
        connection.disconnect();

        //Parsing Json results
        JsonParser parser = new JsonParser(); //Deprecated code check here
        return (JsonObject) parser.parse(String.valueOf(resultJson)); //Deprecated function check here
    }

    public static String getCurrentLocationName() {
        String locationName = null;
        try {
            String urlString = "http://ipinfo.io/json";
            var connection = fetchApiResponse(urlString);
            assert connection != null;
            var jsonObject = retrieveJsonObject(connection);

            locationName = jsonObject.get("city").getAsString();
            JsonObject newJsonObject = new JsonObject();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Can not retrieve current location info.");
        }
        return locationName;
    }
}