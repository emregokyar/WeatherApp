package panels;

import ImageService.ImageLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DailyInfoPanel extends JPanel {

    public DailyInfoPanel(String weatherIconLocation, String temperatureInfo, String humidityInfo, String windInfo, int next24, ZonedDateTime localTime) {
        this.setPreferredSize(new Dimension(250, 75));
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(new LineBorder(Color.BLACK, 1));

        // Day Info next day
        localTime = localTime.plusHours(next24 * 24L);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM");
        JLabel dayInfo = new JLabel(dtf.format(localTime));
        this.add(dayInfo);

        // Weather Info
        JLabel weatherIcon = new JLabel(ImageLoader.loadImage(weatherIconLocation, 30, 30));
        this.add(weatherIcon);

        JLabel temperatureInfoLabel = new JLabel(temperatureInfo);
        this.add(temperatureInfoLabel);

        // Humidity
        JLabel humidityIcon = new JLabel(ImageLoader.loadImage("src/assets/humidity.png", 15, 15));
        this.add(humidityIcon);

        JLabel humidityInfoLabel = new JLabel(humidityInfo);
        this.add(humidityInfoLabel);

        // Wind
        JLabel windIcon = new JLabel(ImageLoader.loadImage("src/assets/windspeed.png", 15, 15));
        this.add(windIcon);

        JLabel windInfoLabel = new JLabel(windInfo);
        this.add(windInfoLabel);

        revalidate();
        repaint();
    }
}