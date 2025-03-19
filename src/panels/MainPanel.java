package panels;

import ApiService.WeatherAppService;
import ImageService.ImageLoader;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MainPanel extends JPanel {
    private JsonObject weatherData;

    private JTextField searchField;
    private JLabel localTimeField;
    private JLabel weatherIcon;
    private JLabel temperatureInfo;
    private JLabel weatherConditionInfo;
    private JLabel humidityInfo;
    private JLabel windInfo;

    private JButton searchButton;
    private JButton currentLocationButton;

    private JPanel nextHoursInfoPanel;

    public MainPanel() {
        this.setSize(450, 450);
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new BorderLayout());

        JPanel searchPanel = createSearchPanel();
        this.add(searchPanel, BorderLayout.NORTH);

        JPanel weatherInfoPanel = createWeatherInfoPanel();
        this.add(weatherInfoPanel, BorderLayout.CENTER);

        nextHoursInfoPanel = createNextHourInfoPanel();
        this.add(nextHoursInfoPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setPreferredSize(new Dimension(450, 50));
        panel.setBackground(Color.LIGHT_GRAY);

        // Search Field
        searchField = new JTextField(WeatherAppService.getCurrentLocationName());
        searchField.setForeground(Color.BLACK);
        searchField.setBackground(Color.WHITE);
        searchField.setPreferredSize(new Dimension(320, 40));
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search");
                }
            }
        });
        panel.add(searchField);

        // Current Location Button
        currentLocationButton = new JButton(ImageLoader.loadImage("src/assets/current-location.png", 30, 30));
        currentLocationButton.setBackground(Color.GRAY);
        currentLocationButton.setPreferredSize(new Dimension(40, 40));
        currentLocationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        currentLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weatherData = WeatherAppService.getWeatherData(WeatherAppService.getCurrentLocationName(), 0);

                assert weatherData != null;
                getSearchField().setText(WeatherAppService.getCurrentLocationName());

                ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of(weatherData.get("timezone").getAsString()));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                localTimeField.setText(localTime.format(dtf));

                //Setting up weather con image
                String weatherCondition = (String) weatherData.get("weather_condition").getAsString();
                String path = "src/assets/" + weatherCondition + ".png";
                getWeatherIcon().setIcon(ImageLoader.loadImage(path, 120, 120));

                //Setting up temperature
                double temperature = (double) weatherData.get("temperature").getAsDouble();
                temperatureInfo.setText(temperature + " °C");

                //Setting up weather condition info
                weatherConditionInfo.setText(weatherCondition.toUpperCase());

                //Setting up humidity text
                long humidity = (long) weatherData.get("humidity").getAsLong();
                humidityInfo.setText(humidity + " %");

                //Setting up wind speed text
                double windSpeed = (double) weatherData.get("wind_speed").getAsDouble();
                windInfo.setText(windSpeed + " km/h");

                if (nextHoursInfoPanel != null) {
                    MainPanel.this.remove(nextHoursInfoPanel);
                }
                nextHoursInfoPanel = createNextHourInfoPanel();
                MainPanel.this.add(nextHoursInfoPanel, BorderLayout.SOUTH);
                MainPanel.this.revalidate();
                MainPanel.this.repaint();
            }
        });
        panel.add(currentLocationButton);

        // Search Button
        searchButton = new JButton(ImageLoader.loadImage("src/assets/search.png", 30, 30));
        searchButton.setBackground(Color.GRAY);
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchField.getText();
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                weatherData = WeatherAppService.getWeatherData(userInput, 0);
                //Setting up time info
                assert weatherData != null;
                ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of(weatherData.get("timezone").getAsString()));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                localTimeField.setText(localTime.format(dtf));

                //Setting up weather con image
                String weatherCondition = (String) weatherData.get("weather_condition").getAsString();
                String path = "src/assets/" + weatherCondition + ".png"; //Binding path with weather condition
                getWeatherIcon().setIcon(ImageLoader.loadImage(path, 120, 120));

                //Setting up temperature
                double temperature = (double) weatherData.get("temperature").getAsDouble();
                temperatureInfo.setText(temperature + " °C");

                //Setting up weather condition info
                weatherConditionInfo.setText(weatherCondition.toUpperCase());

                //Setting up humidity text
                long humidity = (long) weatherData.get("humidity").getAsLong();
                humidityInfo.setText(humidity + " %");

                //Setting up wind speed text
                double windSpeed = (double) weatherData.get("wind_speed").getAsDouble();
                windInfo.setText(windSpeed + " km/h");

                //!!! Check here it didn't work!!!!
                if (nextHoursInfoPanel != null) {
                    MainPanel.this.remove(nextHoursInfoPanel);
                }
                nextHoursInfoPanel = createNextHourInfoPanel();
                MainPanel.this.add(nextHoursInfoPanel, BorderLayout.SOUTH);
                MainPanel.this.revalidate();
                MainPanel.this.repaint();
            }
        });
        panel.add(searchButton);

        return panel;
    }

    private JPanel createWeatherInfoPanel() {
        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(450, 300));
        panel.setBackground(Color.LIGHT_GRAY);

        weatherData = WeatherAppService.getWeatherData(WeatherAppService.getCurrentLocationName(), 0);
        //Time info - check here one more time
        ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of(weatherData.get("timezone").getAsString()));
        LocalDateTime localDateTime = localTime.toLocalDateTime();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = dtf.format(localDateTime);

        localTimeField = new JLabel(timeString);
        localTimeField.setBounds(196, 15, 100, 30);
        localTimeField.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(localTimeField);

        //Weather Condition Part
        String weatherCondition = weatherData.get("weather_condition").getAsString();
        weatherIcon = new JLabel(ImageLoader.loadImage("src/assets/" + weatherCondition + ".png", 120, 120));
        weatherIcon.setBounds(160, 55, 120, 120);
        panel.add(weatherIcon);

        double temperature = weatherData.get("temperature").getAsDouble();
        temperatureInfo = new JLabel(temperature + " °C");
        temperatureInfo.setBounds(200, 175, 100, 30);
        temperatureInfo.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(temperatureInfo);

        weatherConditionInfo = new JLabel(weatherCondition.toUpperCase());
        weatherConditionInfo.setBounds(160, 205, 150, 30);
        weatherConditionInfo.setFont(new Font("Arial", Font.PLAIN, 30));
        panel.add(weatherConditionInfo);

        //Humidity Part
        JLabel humidityIcon = new JLabel(ImageLoader.loadImage("src/assets/humidity.png", 50, 50));
        humidityIcon.setBounds(30, 250, 50, 50);
        panel.add(humidityIcon);

        JLabel humidityText = new JLabel("Humidity");
        humidityText.setBounds(90, 235, 100, 50);
        humidityText.setFont(new Font("Arial", Font.PLAIN, 17));
        panel.add(humidityText);

        long humidity = weatherData.get("humidity").getAsLong();
        humidityInfo = new JLabel(humidity + " %");
        humidityInfo.setBounds(90, 265, 50, 30);
        humidityInfo.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(humidityInfo);

        //Wind Part
        JLabel windIcon = new JLabel(ImageLoader.loadImage("src/assets/windspeed.png", 50, 50));
        windIcon.setBounds(300, 250, 50, 50);
        panel.add(windIcon);

        JLabel windText = new JLabel("Wind");
        windText.setBounds(360, 235, 100, 50);
        windText.setFont(new Font("Arial", Font.PLAIN, 17));
        panel.add(windText);

        double windSpeed = weatherData.get("wind_speed").getAsDouble();
        windInfo = new JLabel(windSpeed + " km/h");
        windInfo.setBounds(360, 265, 100, 30);
        windInfo.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(windInfo);

        return panel;
    }

    public JPanel createNextHourInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 6, 0, 0));
        panel.setPreferredSize(new Dimension(450, 100));

        //Demo weather info
        for (int i = 1; i < 7; i++) {
            JsonObject nextHoursData = WeatherAppService.getWeatherData(getSearchField().getText(), i);
            assert nextHoursData != null;
            String weatherCondition = (String) nextHoursData.get("weather_condition").getAsString();
            String path = "src/assets/" + weatherCondition + ".png";

            double temperature = (double) nextHoursData.get("temperature").getAsDouble();

            HourlyInfoPanel hourlyInfoPanel = new HourlyInfoPanel((temperature + " °C"), path, ZonedDateTime.now(ZoneId.of(weatherData.get("timezone").getAsString())), i);
            panel.add(hourlyInfoPanel);
        }
        return panel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JLabel getWeatherIcon() {
        return weatherIcon;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getCurrentLocationButton() {
        return currentLocationButton;
    }
}