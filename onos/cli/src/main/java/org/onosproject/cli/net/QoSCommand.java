package org.onosproject.cli.net;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.behaviour.QueueConfigBehaviour;
import org.apache.karaf.shell.api.action.Argument;
import org.onosproject.net.behaviour.QosConfigBehaviour;
import org.onosproject.net.behaviour.PortConfigBehaviour;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Device;
import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.QueueDescription;
import org.onosproject.net.behaviour.QosDescription;
import org.onosproject.net.behaviour.DefaultQueueDescription;
import org.onosproject.net.behaviour.DefaultQosDescription;
import org.onosproject.net.behaviour.QueueId;
import org.onosproject.net.behaviour.QosId;
import org.onosproject.net.Annotations;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.AnnotationKeys;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;
import org.onlab.osgi.DefaultServiceDirectory;
import org.onlab.util.Bandwidth;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.device.DefaultPortDescription;
import java.util.EnumSet;
import java.util.HashMap;
/**
 * Command to create a queue on a device.
 */
@Service
@Command(scope = "onos", name = "qos",
        description = "Qos Test.")
public class QoSCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "type", description = "the type of qos [add/del/display].", required = true,
            multiValued = false)
    String type = null;

    @Argument(index = 1, name = "controllerid", description = "the CONTROLLER TYPE device id.", required = true,
            multiValued = false)
    String controllerid = null;

    @Argument(index = 2, name = "port name", description = "the port name.", required = true,
            multiValued = false)
    String name = null;

    @Argument(index = 3, name = "port number", description = "the port number.", required = true,
            multiValued = false)
    String portnumber = null;

    @Argument(index = 4, name = "rate", description = "the rate of interface.", required = true,
            multiValued = false)
    String rate = null;

    @Argument(index = 5, name = "burst", description = "the burst of interface.", required = true,
            multiValued = false)
    String burst = null;


    @Override
    protected void doExecute() {
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(controllerid));
        if (device == null) {
            log.error("{} isn't support config.", controllerid);
            return;
        }

        QueueDescription queueDesc = DefaultQueueDescription.builder()
                .queueId(QueueId.queueId(name))
                .maxRate(Bandwidth.bps(Long.parseLong(rate)))
                .minRate(Bandwidth.bps(Long.valueOf(rate)))
                .burst(Long.valueOf(burst))
                .build();

//        PortDescription portDesc = new DefaultPortDescription(
//                PortNumber.portNumber(Long.valueOf(portnumber), name), true);

        HashMap<Long, QueueDescription> queues = new HashMap<>();
        queues.put(0L, queueDesc);
        QosDescription qosDesc = DefaultQosDescription.builder()
                .qosId(QosId.qosId(name))
                .type(QosDescription.Type.HTB)
                .maxRate(Bandwidth.bps(Long.valueOf("100000")))
                .queues(queues)
                .build();

        QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
        QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
        PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);
        if (type.equals("add")) {
            queueConfig.addQueue(queueDesc);
            qosConfig.addQoS(qosDesc);
//            portConfig.applyQoS(portDesc, qosDesc);

        } else if (type.equals("del")) {
            queueConfig.deleteQueue(queueDesc.queueId());
            qosConfig.deleteQoS(qosDesc.qosId());
//            portConfig.removeQoS(portDesc.portNumber());

        } else if (type.equals("display")) {
            queueConfig.getQueues().stream().forEach(q -> {
                print("name=%s, type=%s, dscp=%s, maxRate=%s, " +
                                "minRate=%s, pri=%s, burst=%s", q.queueId(), q.type(),
                        q.dscp(), q.maxRate(), q.minRate(),
                        q.priority(), q.burst());
            });
            qosConfig.getQoses().forEach(q -> {
                print("name=%s, maxRate=%s, cbs=%s, cir=%s, " +
                                "queues=%s, type=%s", q.qosId(), q.maxRate(),
                        q.cbs(), q.cir(), q.queues(), q.type());
            });
        }
    }
}