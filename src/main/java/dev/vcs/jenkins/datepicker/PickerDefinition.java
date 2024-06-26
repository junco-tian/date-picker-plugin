package dev.vcs.jenkins.datepicker;

import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Vimalraj Chandra Sekaran (rajvimalc)
 * Email: rajvimalc@gmail.com
 * Created on: 8/8/2019 11:13 AM
 */
public class PickerDefinition extends ParameterDefinition {

    private String type;

    private String defaultFormat;

    private String defaultValue;

    private String value;

    private StringLocalDateValue stringLocalDateValue = null;

    protected PickerDefinition(String name, String description, String type, String defaultValue) {
        super(name, description);

        final PickerType pickerType = PickerType.resolve(type);
        this.type = pickerType.getType();
        this.defaultFormat = pickerType.getFormat();

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        this.stringLocalDateValue = new StringLocalDateValue(defaultValue, defaultFormat);
    }

    private StringLocalDateValue GetlocalDateValue()
    {
        if (this.stringLocalDateValue == null)
        {
            this.stringLocalDateValue = new StringLocalDateValue(defaultValue, defaultFormat);
        }
        return this.stringLocalDateValue;
    }
    /**
     * Called from jelly
     * @return the default value
     */
    public String getDefaultValue() {
        return this.GetlocalDateValue().getStringLocalDate();
    }

    /**
     * Called from jelly
     * @return the value entered, if blank, default value
     */
    public String getValue() {
        return this.GetlocalDateValue().getValue();
    }

    /**
     * Called from jelly
     * @return the picker type
     */
    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public DateParameterValue getDefaultParameterValue() {
        DateParameterValue dateParameterValue = new DateParameterValue(getName(), getValue(), getDescription());
        dateParameterValue.createValueFromDefault(defaultFormat);
        return dateParameterValue;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        DateParameterValue dateParameterValue = req.bindJSON(DateParameterValue.class, jo);
        dateParameterValue.createValueFromJenkins(defaultFormat);
        return dateParameterValue;
    }

    @Override
    public ParameterValue createValue(StaplerRequest staplerRequest) {
        String requestedValue = staplerRequest.getParameter(getName());
        if (isEmpty(requestedValue)) {
            return getDefaultParameterValue();
        }

        DateParameterValue dateParameterValue = new DateParameterValue(getName(), requestedValue, defaultFormat, getDescription());
        dateParameterValue.createValueFromPostRequest(defaultFormat);
        return dateParameterValue;
    }

}
