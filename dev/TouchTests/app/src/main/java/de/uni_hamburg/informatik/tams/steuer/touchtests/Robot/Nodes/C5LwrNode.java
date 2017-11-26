package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.Interfaces.RobotJointDataReceiver;
import sensor_msgs.JointState;

/**
 * Created by merlin on 26.11.17.
 */

public class C5LwrNode implements NodeMain, RobotJointDataReceiver {
    private String subscribeTopic;
    private String publishTopic;

    Subscriber<sensor_msgs.JointState> jointStateSubsc;
    Publisher<sensor_msgs.JointState> jointStatePub;

    List<RobotJointDataReceiver> dataReceivers = new ArrayList<>();



    public C5LwrNode(String subscrbTopic, String pubTopic) {
        this.subscribeTopic = subscrbTopic;
        this.publishTopic = pubTopic;
    }

    public void addJointDataListener(RobotJointDataReceiver lstnr) {
        dataReceivers.add(lstnr);
    }

    public void removeJointDataListener(RobotJointDataReceiver lstnr) {
        dataReceivers.remove(lstnr);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ba_android/c5lwrnode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        jointStatePub = connectedNode.newPublisher(publishTopic, JointState._TYPE);
        jointStateSubsc = connectedNode.newSubscriber(subscribeTopic, JointState._TYPE);

        jointStateSubsc.addMessageListener(new MessageListener<JointState>() {

            @Override
            public void onNewMessage(JointState jointState) {
                List<String> names = jointState.getName();
                double[] positions = jointState.getPosition();

                if(names.size() == positions.length) {
                    HashMap<String, Double> data = new HashMap<>();

                    for(int i = 0; i < names.size(); i++) {
                        data.put(names.get(i), positions[i]);
                    }

                    for(RobotJointDataReceiver r : dataReceivers) {
                        r.handleJointData(data);
                    }
                }
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
        jointStateSubsc.shutdown();
        jointStatePub.shutdown();
    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {

    }

    @Override
    public void handleJointData(HashMap<String, Double> data) {
        if(jointStatePub == null) {
            return;
        }

        JointState js = jointStatePub.newMessage();
        List<String> name = new LinkedList<>();
        name.addAll(data.keySet());
        double[] position = new double[name.size()];



        for(int i = 0; i < name.size(); i++) {
            position[i] = data.get(name.get(i));
        }

        js.setName(name);
        js.setPosition(position);

        jointStatePub.publish(js);
    }
}
