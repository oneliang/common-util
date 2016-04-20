package com.oneliang.util.json;

public interface JsonString {

    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON 
     * serialization. 
     * 
     * @return A strictly syntactically correct JSON text.
     */
    public String toJsonString();
}
