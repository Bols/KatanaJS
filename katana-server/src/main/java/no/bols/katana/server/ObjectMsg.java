package no.bols.katana.server;

import java.util.HashMap;
import java.util.Map;

public class ObjectMsg {
    public Map<String,Object> values=new HashMap<String, Object>();

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
}
