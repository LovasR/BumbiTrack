package hu.tibipi.bumbitrack.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Rodrigo, Maritaria
 * <a href="https://gist.github.com/roooodcastro/6325153?permalink_comment_id=3107524#gistcomment-3107524">src</a>
 *
 * this is a modified version of the original
 */
public class LineGraphPanel extends JPanel {
    private static final int PADDING = 25;
    private static final int LABEL_PADDING = 50;
    private static final int POINT_WIDTH = 4;
    private static final int NUMBER_Y_DIVISIONS = 10;
    private static final Color lineColor = new Color(44, 102, 230, 180);
    private static final Color pointColor = new Color(100, 100, 100, 180);
    private static final Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke graphStroke = new BasicStroke(2f);
    private final List<Integer> values = new ArrayList<>(10);

    private final Color labelColor;

    public LineGraphPanel(Color labelColor) {
        setPreferredSize(new Dimension(PADDING * 2 + 300, PADDING * 2 + 200));
        this.labelColor = labelColor;
    }

    public void setValues(Collection<Integer> newValues) {
        values.clear();
        addValues(newValues);
    }

    public void addValues(Collection<Integer> newValues) {
        values.addAll(newValues);
        updateUI();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!(graphics instanceof Graphics2D)) {
            graphics.drawString("Graphics is not Graphics2D, unable to render", 0, 0);
            return;
        }
        final Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int length = values.size();
        final int width = getWidth();
        final int height = getHeight();
        final double maxScore = getMaxScore() * 1.01;
        final double minScore = getMinScore() / 1.01;
        final double scoreRange;
        if(maxScore - minScore != 0)
            scoreRange = maxScore - minScore;
        else
            scoreRange = 2;

        // draw white background
        g.setColor(Color.WHITE);
        g.fillRect(
                PADDING + LABEL_PADDING,
                PADDING,
                width - (2 * PADDING) - LABEL_PADDING,
                height - 2 * PADDING - LABEL_PADDING);
        g.setColor(Color.BLACK);

        final FontMetrics fontMetrics = g.getFontMetrics();
        final int fontHeight = fontMetrics.getHeight();

        createVerticalMarkings(width, height, minScore, scoreRange, fontMetrics, fontHeight, g);

        createHorizontalMarkings(width, height, fontMetrics, fontHeight, g);

        // create x and y axes 
        g.drawLine(PADDING + LABEL_PADDING, height - PADDING - LABEL_PADDING, PADDING + LABEL_PADDING, PADDING);
        g.drawLine(
                PADDING + LABEL_PADDING,
                height - PADDING - LABEL_PADDING,
                width - PADDING,
                height - PADDING - LABEL_PADDING);

        final Stroke oldStroke = g.getStroke();
        g.setColor(lineColor);
        g.setStroke(graphStroke);

        final double xScale = ((double) width - (2 * PADDING) - LABEL_PADDING) / (length - 1);
        final double yScale = ((double) height - 2 * PADDING - LABEL_PADDING) / scoreRange;

        final List<Point> graphPoints = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            final int x1 = (int) (i * xScale + PADDING + LABEL_PADDING);
            final int y1 = (int) ((maxScore - values.get(i)) * yScale + PADDING);
            graphPoints.add(new Point(x1, y1));
        }

        for (int i = 0; i < graphPoints.size() - 1; i++) {
            final int x1 = graphPoints.get(i).x;
            final int y1 = graphPoints.get(i).y;
            final int x2 = graphPoints.get(i + 1).x;
            final int y2 = graphPoints.get(i + 1).y;
            g.drawLine(x1, y1, x2, y2);
        }

        boolean drawDots = width > (length * POINT_WIDTH);
        if (drawDots) {
            g.setStroke(oldStroke);
            g.setColor(pointColor);
            for (Point graphPoint : graphPoints) {
                final int x = graphPoint.x - POINT_WIDTH / 2;
                final int y = graphPoint.y - POINT_WIDTH / 2;
                g.fillOval(x, y, POINT_WIDTH, POINT_WIDTH);
            }
        }
    }

    private void createVerticalMarkings(int width, int height, double minScore, double scoreRange, FontMetrics fontMetrics, float fontHeight, Graphics2D g){
        for (int i = 0; i < NUMBER_Y_DIVISIONS + 1; i++) {
            final int x1 = PADDING + LABEL_PADDING;
            final int x2 = POINT_WIDTH + PADDING + LABEL_PADDING;
            final int y = height - ((i * (height - PADDING * 2 - LABEL_PADDING)) / NUMBER_Y_DIVISIONS + PADDING + LABEL_PADDING);
            if (!values.isEmpty()) {
                g.setColor(gridColor);
                g.drawLine(PADDING + LABEL_PADDING + 1 + POINT_WIDTH, y, width - PADDING, y);
                g.setColor(labelColor);
                final int tickValue = (int) (minScore + ((scoreRange * i) / NUMBER_Y_DIVISIONS));
                final String yLabel = tickValue + "";
                final int labelWidth = fontMetrics.stringWidth(yLabel);
                g.drawString(yLabel, x1 - (float) labelWidth - 2, y + (fontHeight / 2) - 3);
            }
            g.drawLine(x1, y, x2, y);
        }
    }

    private void createHorizontalMarkings(int width, int height, FontMetrics fontMetrics, float fontHeight, Graphics2D g){
        int length = values.size();
        if (length > 1) {
            for (int i = 0; i < length; i++) {
                final int x = i * (width - PADDING * 2 - LABEL_PADDING) / (length - 1) + PADDING + LABEL_PADDING;
                final int y1 = height - PADDING - LABEL_PADDING;
                final int y2 = y1 - POINT_WIDTH;
                if ((i % ((int) (length / 20.0) + 1)) == 0) {
                    g.setColor(gridColor);
                    g.drawLine(x, height - PADDING - LABEL_PADDING - 1 - POINT_WIDTH, x, PADDING);
                    g.setColor(labelColor);
                    final String xLabel = i + "";
                    final int labelWidth = fontMetrics.stringWidth(xLabel);
                    g.drawString(xLabel, x - (float) labelWidth / 2, y1 + fontHeight + 3);
                }
                g.drawLine(x, y1, x, y2);
            }
        }
    }

    private double getMinScore() {
        return values.stream().min(Integer::compareTo).orElse(0);
    }

    private double getMaxScore() {
        return values.stream().max(Integer::compareTo).orElse(0);
    }
}