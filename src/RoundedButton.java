import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

class RoundedButton implements Border {
    private int roundRadius;

    RoundedButton(int roundRadius) {
        this.roundRadius = roundRadius;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int r = roundRadius;
        int w = width - 1;
        int h = height - 1;

        Area round = new Area(new RoundRectangle2D.Float(x, y, w, h, r, r));
        Container parent = c.getParent();
        if (Objects.nonNull(parent)) {
            g2.setPaint(parent.getBackground());
            Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
            corner.subtract(round);
            g2.fill(corner);
        }
        g2.draw(round);
        g2.dispose();
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.roundRadius + 1, this.roundRadius + 1, this.roundRadius + 2, this.roundRadius + 2);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}