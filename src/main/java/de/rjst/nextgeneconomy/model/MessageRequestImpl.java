package de.rjst.nextgeneconomy.model;

import de.rjst.nextgeneconomy.setting.NgeMessage;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Map;

@Builder
@Data
public class MessageRequestImpl implements MessageRequest{

    private Locale locale;
    private Map<String, String> placeholders;
    private NgeMessage ngeMessage;

}
