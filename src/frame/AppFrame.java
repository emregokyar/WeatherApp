package frame;

import ApiService.WeatherAppService;
import com.google.gson.JsonObject;
import panels.DailyInfoPanel;
import panels.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AppFrame extends JFrame {
    private MainPanel mainPanel;
    private JPanel nextDays;
    //private List<DailyInfoPanel> dailyInfoList = new ArrayList<>();

    public AppFrame() throws HeadlessException {
        super("Weather App");
        this.setSize(new Dimension(700, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        //Main Info Panel
        mainPanel = new MainPanel();
        this.add(mainPanel, BorderLayout.WEST);

        nextDays = addDailyWeatherInfo();
        AppFrame.this.add(nextDays, BorderLayout.EAST);

        mainPanel.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nextDays != null) {
                    AppFrame.this.remove(nextDays);
                    revalidate();
                    repaint();
                }
                nextDays = addDailyWeatherInfo();
                AppFrame.this.add(nextDays, BorderLayout.EAST);
            }
        });

        mainPanel.getCurrentLocationButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.getSearchField().setText(WeatherAppService.getCurrentLocationName());
                if (nextDays != null) {
                    AppFrame.this.remove(nextDays);
                    revalidate();
                    repaint();
                }
                nextDays = addDailyWeatherInfo();
                AppFrame.this.add(nextDays, BorderLayout.EAST);
            }
        });

        this.setVisible(true);
    }

    //Panel for displaying info about next days
    public JPanel addDailyWeatherInfo() {
        JPanel nextDays = new JPanel(new GridLayout(6, 1, 0, 0));
        nextDays.setPreferredSize(new Dimension(250, 450));

        for (int i = 1; i < 7; i++) {
            JsonObject next24hoursInfo = WeatherAppService.getWeatherData(mainPanel.getSearchField().getText(), i * 24);
            assert next24hoursInfo != null;
            ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of(next24hoursInfo.get("timezone").getAsString()));

            String weatherCondition = next24hoursInfo.get("weather_condition").getAsString();
            String path = "src/assets/" + weatherCondition + ".png";
            double temperature = next24hoursInfo.get("temperature").getAsDouble();
            long humidity = next24hoursInfo.get("humidity").getAsLong();
            double windSpeed = next24hoursInfo.get("wind_speed").getAsDouble();

            DailyInfoPanel dailyInfo = new DailyInfoPanel(path, (temperature + " °C"), (humidity + " %"), (windSpeed + " km/h"), i, localTime);
            nextDays.add(dailyInfo);
        }

        return nextDays;
    }
}
