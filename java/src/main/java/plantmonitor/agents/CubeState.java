package swim.plantmonitor.agents;

import swim.plantmonitor.agents.Cube;
import swim.api.SwimLane;
import swim.api.agent.AbstractAgent;
import swim.api.lane.CommandLane;
import swim.api.lane.MapLane;
import swim.api.lane.ValueLane;
import swim.structure.Form;
import swim.structure.Record;
import swim.structure.Value;

/**
 * The Sensor State Web Agent represents the state of
 * a single sensor attached to a plant.
 */
public class CubeState extends AbstractAgent {

    // max number of records to hold in history lane
    private static final int HISTORY_SIZE = 200;

    // bool used to keep track of if the sensor has registered to its plant
    private boolean isRegistered = false;

    private Boolean showDebug = false;

    /**
     * Value Lane that holds the sensor name
     */
    @SwimLane("name")
    ValueLane<String> name = this.<String>valueLane();

    @SwimLane("cubeSize")
    ValueLane<Float> cubeSize = this.<Float>valueLane();

    @SwimLane("cubeColor")
    ValueLane<String> cubeColor = this.<String>valueLane();

    @SwimLane("cubePositionX")
    ValueLane<Float> cubePositionX = this.<Float>valueLane();

    @SwimLane("cubePositionY")
    ValueLane<Float> cubePositionY = this.<Float>valueLane();

    @SwimLane("cube")
    ValueLane<Cube> cube = this.<Cube>valueLane()
            .didSet((newValue, oldValue) -> {
                System.out.println("foo: " + newValue);
            });

    /**
     * Value Lane that hold the latest value from the sensor (resource)
     */
    @SwimLane("latest")
    ValueLane<Cube> latest = this.<Cube>valueLane()
            .didSet((newValue, oldValue) -> {
                System.out.println(newValue);
//      // create timestamp
//      final long now = System.currentTimeMillis();
//
//      // update history lane with new value and timestamp
//      this.history.put(now, newValue);

            });

    /**
     * Value Lane which hold all the resource information for
     * this sensor which was passed from the Connect API by NodeJS
     */
    @SwimLane("info")
    ValueLane<Value> info = this.<Value>valueLane();

    /**
     * Value Lane which holds the threshold value used when
     * checking if there is an 'alert' on this sensor.
     */
    @SwimLane("threshold")
    ValueLane<Float> threshold = valueLane();


    /**
     * Map Lane which holds the history of this sensor values keyed by timestamp
     */
    @SwimLane("history")
    MapLane<Long, Float> history = this.<Long, Float>mapLane()
            .didUpdate((key, newValue, oldValue) -> {
                if (this.history.size() > HISTORY_SIZE) {
                    this.history.remove(this.history.getIndex(0).getKey());
                }
            });

    @SwimLane("setCube")
    CommandLane<Cube> setCube = this.<Cube>commandLane()
            .onCommand((Cube value) -> {
//                Float fData = new Float(125.0);
//                cubeSize.set(fData);
                System.out.println("CubeValue: " + value);
//                final Value fooVal = (Value) Cube.form().cast(value);

//                System.out.println("casttt: " + fooVal);
//                System.out.println(Cube.form().cast(value));
//                this.cube.set(Cube.form().cast(value));
            });


    /**
     * Command Lane used to set the latest sensor value.
     */
    @SwimLane("setLatest")
    CommandLane<Record> setLatestCommand = this.<Record>commandLane()
            .onCommand((newData) -> {
//              Float newValue = newData.get("sensorData").floatValue();
                System.out.println(newData);
//              latest.set(newData);
//              cubeSize.set(newValue);
                String sensor = this.getProp("sensorId").stringValue();
                switch (sensor) {
                    case "cubeSize":
                        Float newValue = newData.get("cubeSize").floatValue();
                        System.out.println(newValue);
                        cubeSize.set(newValue);
                        break;
                    case "cubeColor":
                        cubeColor.set(newData.get("cubeColor").stringValue());
                        break;
                    case "cubePositionX":
                        cubePositionX.set(newData.get("x").floatValue());
                        break;
                    case "cubePositionY":
                        cubePositionY.set(newData.get("y").floatValue());
                        break;
                    default:
                        break;
                }

                if (this.showDebug) {
                    System.out.print(String.format("[%1$s Sensor Value]:", this.name.get()));
                    System.out.println(newData);
                }
            });

    /**
     * Command Lane used to change the sensor threshold value.
     */
    @SwimLane("setThreshold")
    CommandLane<Float> setThreshold = this.<Float>commandLane()
            .onCommand(newValue -> {
                if (this.showDebug) {
                    System.out.print(String.format("[%1$s Threshold Value]:", this.name.get()));
                    System.out.println(newValue);
                }

                threshold.set(newValue);
            });

    /**
     * Command Lane used to sent the info for the current sensor
     */
    @SwimLane("setInfo")
    CommandLane<Record> setInfoCommand = this.<Record>commandLane()
            .onCommand((newData) -> {
                this.name.set(newData.get("sensorName").stringValue());
                this.info.set(newData);
                if (!isRegistered) {
                    String plantNode = String.format("/machines/%1$s", newData.get("workflowId").stringValue());
                    // System.out.println(plantNode);
                    command(plantNode, "addCubeSensor", newData);
                    this.isRegistered = true;
                }

            });

    @Override
    public void didStart() {
        // set some default values for our lanes
        // this prevents the UI from getting bad values
        // if the sensor has not been updated by NodeJS with real data
//    this.latest.set(0f);
        this.threshold.set(20f);
    }

}