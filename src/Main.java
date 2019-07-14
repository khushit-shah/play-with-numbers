import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.List;

public class Main extends Canvas implements Runnable {

    private int colorIndex = 0;
    private List<Point> pointList = new LinkedList<>();
    private List<Circle> verticals = new LinkedList<>();
    private List<Circle> horizontals = new LinkedList<>();
    private Color[] col = {Color.red, Color.blue, Color.green, Color.pink, Color.orange};
    private Thread thread;
    private boolean isRunning = false;

    private Main() {
        for (int i = 0; i < 5; i++) {
            verticals.add(new Circle(40, 150 + i * 100, 40, col[i]));
        }
        for (int i = 0; i < 5; i++) {
            horizontals.add(new Circle(150 + i * 100, 40, 40, col[i]));
        }

        new Window("Play With Maths", 840, 620, this);
    }

    public static void main(String[] args) {
        new Main();
    }

    synchronized void start() {
        if (isRunning) return;
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }


    private synchronized void stop() {
        if (!isRunning) return;
        try {
            thread.join();
            isRunning = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            render();
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;

            }
        }
        stop();
    }

    private void tick() {
        for (int i = 0; i < 5; i++) {
            verticals.get(i).update(i + 1);
            horizontals.get(i).update(i + 1);
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!pointList.contains(new Point(horizontals.get(j).px, verticals.get(i).py))) {
                    pointList.add(new Point(horizontals.get(j).px, verticals.get(i).py));
                }
            }
        }
        System.out.println(pointList.size());
        if (pointList.size() >= 6042) {
            pointList.clear();
        }
    }

    private void render() {
        // render stuff!

        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;

        }

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 840, 620);

        {
            g.setColor(col[colorIndex++]);
            colorIndex %= 5;
//            for(int i = 0; i < 5; i ++) {
//                for(int j = 0; j < 5; j ++)
//                    g.fillOval(horizontals.get(j).px,verticals.get(i).py, 5, 5);
//            }
            for (Point p : pointList) {
                g.fillOval(p.x, p.y, 5, 5);
            }
            for (int i = 0; i < 5; i++) {
                verticals.get(i).draw(g);
                horizontals.get(i).draw(g);
            }
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    g.setColor(col[i]);
                    g.drawLine(verticals.get(i).px, verticals.get(i).py, horizontals.get(j).px, verticals.get(i).py);
                    g.setColor(col[j]);
                    g.drawLine(horizontals.get(j).px, horizontals.get(j).py, horizontals.get(j).px, verticals.get(i).py);
                    g.setColor(col[i]);
                    g.fillOval(horizontals.get(j).px - 5, verticals.get(i).py - 5, 10, 10);
                }
            }

        }
        bs.show();
        g.dispose();
        bs.dispose();
    }
}


class Circle {

    int px, py;
    private int radius;
    private int angle;
    private int x, y;
    private Color col;

    Circle(int x, int y, int radius, Color col) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        px = 0;
        py = 0;
        angle = 0;
        this.col = col;
    }

    void update(int an) {
        this.angle += an;
        if (angle > 360)
            angle = angle % 360;
        px = (int) (x + radius * Math.cos(Math.toRadians(angle)));
        py = (int) (y + radius * Math.sin(Math.toRadians(angle)));
    }

    void draw(Graphics g) {
        g.setColor(col);
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);

        g.drawLine(x, y, px, py);
    }
}