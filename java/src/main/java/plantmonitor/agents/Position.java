package swim.plantmonitor.agents;

import swim.recon.Recon;
import swim.structure.Form;
import swim.structure.Kind;
import swim.structure.Member;
import swim.structure.Tag;

@Tag("position")
public class Position {
    @Member("int")
    private int x = 0;
    @Member("int")
    private int y = 0;

    public Position() {}
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getPositionX() {
        return this.x;
    }

    public int getPositionY() {
        return this.y;
    }

    //@Kind-annotated static field and static accessor method
    @Kind
    private static Form<Position> form;

    public static Form<Position> form() {
        if (form == null) {
            form = Form.forClass(Position.class);
        }
        return form;
    }

    @Override
    public String toString() {
        return Recon.toString(form().mold(this));
    }
}
