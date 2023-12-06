package hu.tibipi.bumbitrack.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Represents a custom JPanel to render a line graph.
 * This class is a modified version based on an original implementation.
 *
 * Original source: <a href="https://gist.github.com/roooodcastro/6325153?permalink_comment_id=3107524#gistcomment-3107524">
 *     https://gist.github.com/roooodcastro/6325153?permalink_comment_id=3107524#gistcomment-3107524</a>
 *
 * This JPanel displays a line graph with customizable features such as padding, color, stroke, divisions, etc.
 * It renders a line graph based on a list of values provided to the graph.
 * Each instance of LineGraphPanel represents a single line graph.
 *
 * @author Rodrigo, Maritaria
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

    /**
     * Constructs a LineGraphPanel instance with a specified label color.
     *
     * @param labelColor The color used for graph labels.
     */
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

    /**
     * Overrides the paintComponent method to render the graphical representation of the LineGraphPanel.
     * It performs the painting of the graph object within this JPanel.
     *
     * @param graphics The Graphics object used to paint the component.
     */
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

    /**
     * Creates vertical markings along the y-axis of the graph panel to indicate score divisions.
     * Renders grid lines and labels for the y-axis divisions.
     *
     * @param width        The width of the graph panel.
     * @param height       The height of the graph panel.
     * @param minScore     The minimum score value in the dataset.
     * @param scoreRange   The range of scores in the dataset.
     * @param fontMetrics  The FontMetrics object used for text measurement.
     * @param fontHeight   The height of the font used for labels.
     * @param g            The Graphics2D object used for rendering.
     */
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

    /**
     * Creates horizontal markings along the x-axis of the graph panel to indicate data points.
     * Renders grid lines and labels for the x-axis divisions.
     *
     * @param width        The width of the graph panel.
     * @param height       The height of the graph panel.
     * @param fontMetrics  The FontMetrics object used for text measurement.
     * @param fontHeight   The height of the font used for labels.
     * @param g            The Graphics2D object used for rendering.
     */
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

    /**
     * Retrieves the minimum value from the list of scores.
     *
     * @return The minimum score from the list of values. If the list is empty, returns 0.
     */
    private double getMinScore() {
        return values.stream().min(Integer::compareTo).orElse(0);
    }

    /**
     * Retrieves the maximum value from the list of scores.
     *
     * @return The maximum score from the list of values. If the list is empty, returns 0.
     */
    private double getMaxScore() {
        return values.stream().max(Integer::compareTo).orElse(0);
    }
}