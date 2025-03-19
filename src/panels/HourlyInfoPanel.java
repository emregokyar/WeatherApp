package panels;

import ImageService.ImageLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HourlyInfoPanel extends JPanel {

    public HourlyInfoPanel(String temperature, String weatherIconLocation, ZonedDateTime localTime, int plusHour) {
        this.setPreferredSize(new Dimension(75, 100));
        this.setSize(75, 100);
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setBorder(new LineBorder(Color.BLACK, 1));

        //Time Info
        localTime = localTime.plusHours(plusHour);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH");
        JLabel timeInfo = new JLabel(dtf.format(localTime) + ":00");
        timeInfo.setPreferredSize(new Dimension(40, 10));
        this.add(timeInfo);

        //Weather Info
        JLabel weatherIcon = new JLabel(ImageLoader.loadImage(weatherIconLocation, 30, 30));
        weatherIcon.setPreferredSize(new Dimension(30, 30));
        this.add(weatherIcon);

        JLabel temperatureInfo = new JLabel(temperature);
        temperatureInfo.setPreferredSize(new Dimension(40, 10));
        this.add(temperatureInfo);
    }
}
