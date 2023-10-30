package swim.plantmonitor.agents;

import swim.recon.Recon;
import swim.structure.*;

@Tag("cube")
public class Cube {
    @Member("i")
    private int cubeSize = 1;

    public Cube() {}

    public Cube(int size) {
        this.cubeSize = size;
    }

    public int getCubeSize() {
        return this.cubeSize;
    }

    @Kind
    private static Form<Cube> form;

    public static Form<Cube> form() {
        if (form == null) {
            form = Form.forClass(Cube.class);
        }
        return  form;
    }

    public Value toValue() {
        return Form.forClass(Cube.class).mold(this).toValue();
    }

    @Override
    public String toString() {
        return Recon.toString(form().mold(this));
    }

}