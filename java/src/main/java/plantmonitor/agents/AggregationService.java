package swim.plantmonitor.agents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import swim.api.SwimLane;
import swim.api.agent.AbstractAgent;
import swim.api.lane.CommandLane;
import swim.api.lane.MapLane;
import swim.api.lane.ValueLane;
import swim.json.Json;
import swim.structure.Value;
import swim.uri.Uri;

import swim.plantmonitor.configUtil.ConfigEnv;

/**
  The Aggregation Service Web Agent keeps track of 
  things that are global to the application such as 
  list of all plants and alerts and application config.
  The application will only have a singe Aggregation Web Agent
  and the agent is started by the Application Plane on app startup.
 */
public class AggregationService extends AbstractAgent {

  /**
    Map Lane to hold list of plants that are being tracked
   */
  @SwimLane("machineList")
  MapLane<String, Value> machineList = this.<String, Value>mapLane();


  /**
    Command Lane to add a new plant to the plant list
    Called by a plant web agent when its created
   */
  @SwimLane("addMachine")
  CommandLane<Value> addMachineCommand = this.<Value>commandLane().onCommand(cubeData -> {
    String plantId = cubeData.get("id").stringValue("none");
    if (plantId != "none") {
      machineList.put(plantId, cubeData);
//      System.out.println("PList: "+machineList.snapshot());
      // String plantNode = String.format("/plant/%1$s", plantId);
      // command(Uri.parse(plantNode), Uri.parse("setConfig"), ConfigEnv.config);
    }  
  });

  @SwimLane("replaceMachine")
  CommandLane<Value> replaceMachineCommand = this.<Value>commandLane().onCommand(cubeData -> {
    String plantId = cubeData.get("id").stringValue("none");
    if (plantId != "none") {
      if (machineList.containsKey(plantId)) {
        System.out.print("REPLACE: ");
        System.out.println(cubeData);
        machineList.put(plantId, cubeData);
      }
    }
  });
}