/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chartsinglebar.blankchart;

import chart.blankchart.SeriesSize;
import chartbarmultiple.blankchart.NiceScale;
import chartsinglebar.ModelChartSingel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class BlankPlotChart extends JComponent {

    private List<ModelChartSingel> dataPoints;

    public BlankPlotChatRender getBlankPlotChatRender() {
        return blankPlotChatRender;
    }

    public void setBlankPlotChatRender(BlankPlotChatRender blankPlotChatRender) {
        this.blankPlotChatRender = blankPlotChatRender;
    }

    public void setData(List<ModelChartSingel> dataPoints) {
        if (dataPoints == null) {
            this.dataPoints = List.of(); // atau inisialisasi dengan ArrayList<>(); sesuai kebutuhan
        } else {

            this.dataPoints = dataPoints;

        }
    }

    public double getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(double maxValues) {
        this.maxValues = maxValues;
        niceScale.setMax(maxValues);
        repaint();
    }

    public double getMinValues() {
        return minValues;
    }

    public int getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(int labelCount) {
        this.labelCount = labelCount;
    }

    public String getValuesFormat() {
        return valuesFormat;
    }

    public void setValuesFormat(String valuesFormat) {
        this.valuesFormat = valuesFormat;
        format.applyPattern(valuesFormat);
    }

    private final DecimalFormat format = new DecimalFormat("#,##0.##");
    private NiceScale niceScale;
    private double maxValues;
    private double minValues;
    private int labelCount;
    private String valuesFormat = "#,##0.##";
    private BlankPlotChatRender blankPlotChatRender;

    public BlankPlotChart() {
        setBackground(Color.WHITE);
        setOpaque(false);
//        setForeground(new Color(100, 100, 100));
        setBorder(new EmptyBorder(20, 10, 10, 10));
        init();
    }

    private void init() {
        initValues(0, 10);
    }

    public void initValues(double minValues, double maxValues) {
        this.minValues = minValues;
        this.maxValues = maxValues;
        niceScale = new NiceScale(minValues, maxValues);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        if (niceScale != null) {
            Graphics2D g2 = (Graphics2D) grphcs;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            createSingleBar(g2);
//            createLine(g2);
            createValues(g2);
//            createLabelText(g2);
//            renderSeries(g2);
        }
    }

    private void createSingleBar(Graphics2D g2) {
        if (dataPoints != null && !dataPoints.isEmpty()) {
            Insets insets = getInsets();
            int width = getWidth();
            int height = getHeight();

            // Hitung lebar bar
            int barCount = dataPoints.size();
            int barWidth =0;
            if (barCount < 3) {
                 barWidth = (int) (0.1 * width / barCount); // Menggunakan 1/10 dari lebar total

            }else if(barCount < 5){ 
                                 barWidth = (int) (0.2 * width / barCount); // Menggunakan 1/10 dari lebar total

            }else {
                    
                 barWidth = (int) (0.42 * width / barCount); // Menggunakan 1/10 dari lebar total

            }

            if (barWidth < 1) {
                barWidth = 1; // Pastikan setidaknya ada lebar minimal untuk bar
            }

            int maxValue = getMaxValue();

            for (int i = 0; i < barCount; i++) {
                ModelChartSingel dataPoint = dataPoints.get(i);
                int barHeight = (int) (((double) dataPoint.getValues() / maxValue) * (height - 30));
                barHeight = Math.min(barHeight, height - insets.bottom - insets.top - 15);

                int x = 10 + i * (barWidth + 5) + 30;
                int y = height - 20 - barHeight - 5;

                // Gambar batang
                g2.setColor(Color.blue);
                g2.fillRect(x, y, barWidth, barHeight);

                // Gambar label teks
                g2.setColor(getForeground());
                FontMetrics ft = g2.getFontMetrics();
                Rectangle2D r2 = ft.getStringBounds("#" + (i + 1), g2);
                int textX = x + (barWidth - (int) r2.getWidth()) / 2;
                int textY = height - insets.bottom + (int) r2.getHeight() - 20;
                g2.drawString("#" + (i + 1), textX, textY);
            }
        }
    }

    private int getMaxValue() {
        if (dataPoints != null && !dataPoints.isEmpty()) {
            int maxValue = dataPoints.get(0).getValues();
            for (ModelChartSingel dataPoint : dataPoints) {
                if (dataPoint.getValues() > maxValue) {
                    maxValue = dataPoint.getValues();
                }
            }
            return maxValue;
        }
        // Return some default value or handle the null case as per your requirement
        return 0; // Defaulting to 0, change as necessary
    }

    private void createLine(Graphics2D g2) {
        g2.setColor(new Color(220, 220, 220));
        Insets insets = getInsets();
        double textHeight = getLabelTextHeight(g2);
        double height = getHeight() - (insets.top + insets.bottom) - textHeight;
        double space = height / niceScale.getMaxTicks();
        double locationY = insets.bottom + textHeight;
        double textWidth = getMaxValuesTextWidth(g2);
        double spaceText = 5;
        for (int i = 0; i <= 5; i++) {
            int y = (int) (getHeight() - locationY);
            g2.drawLine((int) (insets.left + textWidth + spaceText), y, (int) getWidth() - insets.right, y);
            locationY += space;
        }

    }

    private void createValues(Graphics2D g2) {
        g2.setColor(getForeground());
        Insets insets = getInsets();
        double textHeight = getLabelTextHeight(g2);
        double height = getHeight() - (insets.top + insets.bottom) - textHeight;
        double maxValue = getMaxValue(); // Dapatkan nilai maksimum dari batang
        int numValues = 5; // Jumlah nilai yang ingin ditampilkan
        double interval = maxValue / (numValues - 1); // Hitung interval antara nilai

        double locationY = insets.bottom + textHeight + 5; // Menambahkan jarak kecil dari sumbu bawah
        FontMetrics ft = g2.getFontMetrics();

        for (int i = 0; i < numValues; i++) {
            String text = String.valueOf((int) (i * interval)); // Hitung nilai berdasarkan interval
            Rectangle2D r2 = ft.getStringBounds(text, g2);
            double stringY = r2.getCenterY() * -1;
            double y = getHeight() - locationY + stringY;
                    g2.setColor(new Color(219, 219, 219));

            g2.drawLine(insets.left + 30, (int) y, getWidth() - insets.right, (int) y);
                    g2.setColor(getForeground());

            g2.drawString(text, insets.left, (int) y);
            locationY += height / (numValues - 1); // Hitung jarak antara nilai
        }
    }

    private double getMaxValuesTextWidth(Graphics2D g2) {
        double width = 0;
        FontMetrics ft = g2.getFontMetrics();
        double valuesCount = niceScale.getNiceMin();
        for (int i = 0; i <= niceScale.getMaxTicks(); i++) {
            String text = format.format(valuesCount);
            Rectangle2D r2 = ft.getStringBounds(text, g2);
            double w = r2.getWidth();
            if (w > width) {
                width = w;
            }
            valuesCount += niceScale.getTickSpacing();
        }
        return width;
    }

    private int getLabelTextHeight(Graphics2D g2) {
        FontMetrics ft = g2.getFontMetrics();
        return ft.getHeight();
    }

    private String getChartText(int index) {
        if (blankPlotChatRender != null) {
            return blankPlotChatRender.getLabelText(index);
        } else {
            return "Label";
        }
    }

    public SeriesSize getRectangle(int index, double height, double space, double startX, double startY) {
        double x = startX + space * index;
        SeriesSize size = new SeriesSize(x, startY + 1, space, height);
        return size;
    }

    public double getSeriesValuesOf(double values, double height) {
        double max = niceScale.getTickSpacing() * niceScale.getMaxTicks();
        double percentValues = values * 100d / max;
        return height * percentValues / 100d;
    }
}
