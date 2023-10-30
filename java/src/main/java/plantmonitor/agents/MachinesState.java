package swim.plantmonitor.agents;

import swim.api.SwimLane;
import swim.api.agent.AbstractAgent;
import swim.api.lane.CommandLane;
import swim.api.lane.MapLane;
import swim.api.lane.ValueLane;
import swim.structure.Value;

/**
  The PlantState Web Agent represents a single 
  simulated plant and each plant has a collection 
  of sensors attached to it.
 */
public class MachinesState extends AbstractAgent {

  private Boolean showDebug = true;

  /**
    Value Lane to hold all the static device data for this plant.
    This will include all device info returned from Pelion Connect API 
    from the /v3/devices query done by NodeJS
   */
  @SwimLane("info")
  ValueLane<Value> info = this.<Value>valueLane();

  /**
    Map Lane to hold list of sensors for this plant.
   */
  @SwimLane("sensorList")
  MapLane<String, Value> sensorList = this.<String, Value>mapLane();

  /**
    Command Lane used to create a Plant Web Agent
   */
  @SwimLane("createMachine")
  CommandLane<Value> createMachineCommand = this.<Value>commandLane()
    .onCommand(cubeInfo -> {
      if(this.showDebug) {
        System.out.print("[New MACHINE]:");
        System.out.println(cubeInfo);
      }
      this.info.set(cubeInfo);
      command("/aggregationService", "addMachine", this.info.get());
    });

    @SwimLane("updateMachine")
    CommandLane<Value> updateMachineCommand = this.<Value>commandLane()
            .onCommand(cubeInfo -> {
                if(this.showDebug) {
                    System.out.print("[Updated MACHINE]:");
                    System.out.println(cubeInfo);
                }
                this.info.set(cubeInfo);
                command("/aggregationService", "replaceMachine", this.info.get());
            });

  /**
    Command Lane to add a sensor web agent to the plant's sensor list
   */
  @SwimLane("addCubeSensor")
  CommandLane<Value> addCubeCommand = this.<Value>commandLane()
    .onCommand(cube -> {
      if(this.showDebug) {
        String cubeName = this.info.get().get("name").stringValue("none");
        System.out.print(String.format("[New Sensor for %1$s]:", cubeName));
        System.out.println(cube);
      }

      // put new sensor on sensorList
      this.sensorList.put(cube.get("sensorId").stringValue(""), cube);

    });
}