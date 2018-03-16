package de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes;

import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.time.TimeProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.Nodes.Interfaces.RobotJointDataReceiver;
import de.uni_hamburg.informatik.tams.steuer.touchtests.Robot.ValueTypes.JointType;
import sensor_msgs.JointState;
import ros_fri_msgs.*;

/**
 * Created by merlin on 26.11.17.
 */

public class C5LwrNode extends org.ros.node.AbstractNodeMain implements RobotJointDataReceiver {
    private ConnectedNode cNode = null;
    private String subscribeTopic;
    private String handPublishTopic;
    private String armPublishTopic;

    Subscriber<sensor_msgs.JointState> jointStateSubsc;
    Publisher<sensor_msgs.JointState> handJointStatePub;
    Publisher<ros_fri_msgs.RMLPositionInputParameters> armJointStatePub;

    List<RobotJointDataReceiver> dataReceivers = new ArrayList<>();



    public C5LwrNode(String subscrbTopic, String handPubTopic, String armPubTopic) {
        this.subscribeTopic = subscrbTopic;
        this.handPublishTopic = handPubTopic;
        this.armPublishTopic = armPubTopic;
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
        cNode = connectedNode;
        handJointStatePub = connectedNode.newPublisher(handPublishTopic, JointState._TYPE);
        armJointStatePub = connectedNode.newPublisher(armPublishTopic, RMLPositionInputParameters._TYPE);

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
                        r.handleJointData(0, data);
                    }
                }
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
        jointStateSubsc.shutdown();
        armJointStatePub.shutdown();
        handJointStatePub.shutdown();
    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {

    }

    private void publishHand(HashMap<String, Double> data) {
        Publisher<JointState> pub = handJointStatePub;
        if(pub == null) {
            return;
        }

        JointState js = pub.newMessage();
        List<String> name = new LinkedList<>();
        name.addAll(data.keySet());
        double[] position = new double[name.size()];



        for(int i = 0; i < name.size(); i++) {
            position[i] = data.get(name.get(i));
        }

        js.getHeader().setStamp(cNode.getCurrentTime());

        js.setName(name);
        js.setPosition(position);

        pub.publish(js);
    }

    private void publishArm(HashMap<String, Double> data)
    {
        Publisher<RMLPositionInputParameters> pub = armJointStatePub;

        RMLPositionInputParameters msg = pub.newMessage();
        List<String> names = new ArrayList<String>(data.keySet());
        Collections.sort(names);

        double[] vect = new double[names.size()];
        double[] vel = new double[names.size()];
        double[] maxVel = new double[names.size()];
        double[] maxAcc = new double[names.size()];

        for (int i = 0; i < vect.length; i++) {
            vect[i] = data.get(names.get(i));
            vel[i] = 0;
            maxVel[i] = 1;
            maxAcc[i] = 0.3;
        }

        msg.setTargetPositionVector(vect);
        msg.setTargetVelocityVector(vel);
        msg.setMaxAccelerationVector(maxAcc);
        msg.setMaxVelocityVector(maxVel);
        pub.publish(msg);
    }

    @Override
    public void handleJointData(int jointType, HashMap<String, Double> data) {
        Publisher<sensor_msgs.JointState> pub = null;

        switch(jointType)
        {
            case JointType.HAND:
                publishHand(data);
                break;

            case JointType.ARM:
                publishArm(data);
                break;
        }
    }
}
