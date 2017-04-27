package fk.retail.ip.proc.internal.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.entity.Message;
import com.google.inject.Inject;
import fk.retail.ip.proc.config.ProcClientConfiguration;
import fk.retail.ip.proc.internal.Constants;
import fk.retail.ip.proc.model.PushToProcRequestWrapper;
import fk.retail.ip.proc.model.PushToProcRequest;
import fk.sp.common.restbus.sender.RestbusMessageSender;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by yogeshwari.k on 18/04/17.
 */
@Slf4j
public class PushToProcClient {

    private final ObjectMapper mapper;
    private final ProcClientConfiguration procClientConfiguration;
    private final RestbusMessageSender restbusMessageSender;

    @Inject
    PushToProcClient(ObjectMapper mapper, ProcClientConfiguration procClientConfiguration, RestbusMessageSender restbusMessageSender) {
        this.mapper = mapper;
        this.procClientConfiguration = procClientConfiguration;
        this.restbusMessageSender = restbusMessageSender;
    }

    public void pushToProc(Map<Long, PushToProcRequest> allRequirements) {
        allRequirements.forEach((id, requirement) -> {
            Message message = getMessageInstance();
            PushToProcRequestWrapper pushToProcRequest = new PushToProcRequestWrapper();
            pushToProcRequest.getPushToProcRequestList().add(requirement);
            try {
                message.setGroupId(id.toString());
                message.setPayload(mapper.writeValueAsString(pushToProcRequest));
                message.setReplyToHttpUri(procClientConfiguration.getCallbackUrl() + id);
                restbusMessageSender.send(message);
            } catch (JsonProcessingException e) {
                log.error("Unable to serialize request object ", e);
            }
        });
    }

    private Message getMessageInstance() {
        Message message = new Message();
        message.addCustomHeaders(Constants.XClientId.toString(), Constants.APP_ID.toString());
        message.setExchangeName(procClientConfiguration.getRequirementQueueName());
        message.setExchangeType("queue");
        message.setHttpMethod("POST");
        message.setHttpUri(procClientConfiguration.getUrl()+procClientConfiguration.getViewPath());
        message.setReplyTo(procClientConfiguration.getRequirementQueueName());
        message.setReplyToHttpMethod("POST");
        message.setAppId(Constants.APP_ID.toString());
        return message;
    }
}
