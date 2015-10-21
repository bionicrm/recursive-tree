package me.mazeika.recursivetree;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a canvas that draws recursive trees in Swing.
 */
public class Canvas extends JPanel
{
    // indicates if we only want to draw the slowly drawn tree, in order to
    // visualize it better
    private static final boolean ONLY_DRAW_SLOW_TREE = true;

    // the minimum length of a recursive tree
    private static final int MIN_LENGTH = 5;

    // the speed at which the slowly drawn tree is drawn; represents, in ms, how
    // often a branch should be drawn
    private static final int SLOW_DRAW_SPEED = 50;

    // the current hue of the branch that is being drawn; reset before every use
    private int hue;

    // the starting time of the program
    private long startingTime = System.currentTimeMillis();

    // indicates if we've started drawing our tree slowly
    private boolean startedDrawingSlowly;

    // indicates if, when #paintComponent is called, a tree to a certain depth
    // should be drawn
    private boolean drawToDepth = false;

    // tells the depth to draw to if drawToDepth is `true`
    private int depthToDrawTo;

    // the total draws of a tree to a certain depth; only used by
    // #drawTreeSlowlyWithColors
    private int totalDrawToDepthDraws;

    // indicates the current depth of the recursive call for
    // #drawTreeToDepthWithColors
    private int currentDepth;

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // reset hue
        hue = 0;

        // check if we actually want to draw the 2 trees other than the slowly
        // drawn one
        if (! ONLY_DRAW_SLOW_TREE) {
            // the following draw 2 plain recursive trees
            drawTree(g, new Point(225, 450), -90, 100);
            drawTree(g, new Point(225, 450), 90, 100);

            // the following draw 2 colorful recursive trees
            drawTreeWithColors(g, new Point(675, 450), -90, 100);
            drawTreeWithColors(g, new Point(675, 450), 90, 100);
        }

        // if we should draw to a certain depth in this method call... [1]
        if (drawToDepth) {
            // reset currentDepth
            currentDepth = 0;
            // reset drawToDepth
            drawToDepth = false;

            // reset hue
            hue = 0;

            // draw to a certain depth!
            drawTreeToDepthWithColors(g, new Point(450, 450), -90, 100, depthToDrawTo);
            drawTreeToDepthWithColors(g, new Point(450, 450), 90, 100, depthToDrawTo);
        }

        // if we have not started to draw slowly...
        if (! startedDrawingSlowly) {
            // set that we've started drawing slowly
            startedDrawingSlowly = true;

            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    while (true) {
                        // this is the current time in the program
                        final long runTime = System.currentTimeMillis();

                        // the following will signal us when we can slowly draw
                        // recursive trees from [1]
                        drawTreeSlowlyWithColors(runTime);
                        drawTreeSlowlyWithColors(runTime);
                    }
                }
            }).start();
        }
    }

    /**
     * Recursively draws a tree. The smallest branch length is 5.
     *
     * @param g the graphics
     * @param startPoint the starting point
     * @param angle the angle to get the endpoints with
     * @param length the length of the trunk
     */
    private void drawTree(Graphics g, Point startPoint, int angle, double length)
    {
        if (length >= MIN_LENGTH) {
            final Point endpoint = calculateEndPoint(startPoint, angle, length);

            // draw the line
            g.drawLine(startPoint.x, startPoint.y, endpoint.x, endpoint.y);

            // recursive calls
            drawTree(g, endpoint, angle - 30, length * 0.75);
            drawTree(g, endpoint, angle + 50, length * 0.66);
        }
    }

    /**
     * Recursively draws a tree with colors. The smallest branch length is 5.
     *
     * @param g the graphics
     * @param startPoint the starting point
     * @param angle the angle to get the endpoints with
     * @param length the length of the trunk
     *
     * @see #drawTree(Graphics, Point, int, double)
     */
    private void drawTreeWithColors(Graphics g, Point startPoint, int angle, double length)
    {
        if (length >= MIN_LENGTH) {
            final Point endpoint = calculateEndPoint(startPoint, angle, length);

            // set the color
            g.setColor(getNextColor());

            // draw the line
            g.drawLine(startPoint.x, startPoint.y, endpoint.x, endpoint.y);

            // recursive calls
            drawTreeWithColors(g, endpoint, angle - 30, length * 0.75);
            drawTreeWithColors(g, endpoint, angle + 50, length * 0.66);
        }
    }

    /**
     * Allows slow, colorful trees to be drawn. If, when this method is called,
     * the elapsed time of the program is at a certain point, this will modify
     * certain variables to indicate that a tree to a certain depth should be
     * drawn next time this component is painted.
     *
     * @param runTime the current time in milliseconds
     */
    private void drawTreeSlowlyWithColors(long runTime)
    {
        // runTime - startingTime essentially gives us the elapsed time since
        // the start of the program; here, we're checking if the elapsed time is
        // less than the total draw to depth draws times the speed, where the
        // speed is a number in milliseconds
        if (runTime - startingTime < totalDrawToDepthDraws * SLOW_DRAW_SPEED) {
            return;
        }

        // signal that the next painting of this component should draw a tree to
        // a certain depth
        drawToDepth = true;

        // set the depth that should be drawn to on the next painting of this
        // component to the number of draw to depth draws we've had thus far
        // and then add 1 to that
        depthToDrawTo = totalDrawToDepthDraws++;

        // must invoke on the Swing thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                // we want to repaint our component
                repaint();
            }
        });
    }

    /**
     * Recursively draws a tree with colors up to a certain depth. The smallest
     * branch length is 5.
     *
     * @param g the graphics
     * @param startPoint the starting point
     * @param angle the angle to get the endpoints with
     * @param length the length of the trunk
     * @param depth the depth to draw until
     */
    private void drawTreeToDepthWithColors(Graphics g, Point startPoint, int angle, double length, int depth)
    {
        // if the length is >= the minimum length AND the current depth is less
        // than the depth we want to draw to...
        if (length >= MIN_LENGTH && currentDepth++ <= depth) {
            final Point endpoint = calculateEndPoint(startPoint, angle, length);

            // set the color
            g.setColor(getNextColor());

            // draw the line
            g.drawLine(startPoint.x, startPoint.y, endpoint.x, endpoint.y);

            // recursive calls
            drawTreeToDepthWithColors(g, endpoint, angle - 30, length * 0.75, depth);
            drawTreeToDepthWithColors(g, endpoint, angle + 50, length * 0.66, depth);
        }
    }

    /**
     * Gets the next color to be used by
     * {@link #drawTreeWithColors(Graphics, Point, int, double)} or
     * {@link #drawTreeToDepthWithColors(Graphics, Point, int, double, int)}.
     *
     * @return the color
     */
    private Color getNextColor()
    {
        if (++hue > 359) {
            hue = 0;
        }

        return Color.getHSBColor(hue / 360f, 1, 0.75f);
    }

    /**
     * Calculates the end point given a starting point, angle, and length.
     *
     * @param startPoint the starting point
     * @param angle the angle
     * @param length the length
     *
     * @return the end point
     */
    private Point calculateEndPoint(Point startPoint, int angle, double length)
    {
        // calculate the X endpoint
        final int endX = (int) (Math.cos(Math.toRadians(angle)) * length + startPoint.getX());
        // calculate the Y endpoint
        final int endY = (int) (Math.sin(Math.toRadians(angle)) * length + startPoint.getY());

        return new Point(endX, endY);
    }
}
