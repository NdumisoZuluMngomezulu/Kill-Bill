package kill.bill.backend.response;

import java.util.Map;

import kill.bill.backend.state.State;

public class Response {
    private String result;
    private Map<String, Object> data;
    private Map<>String, String> stringData;
    private State state;

    public Response(String result, Map<String, Object> data, Map<String, String> stringData, State state) {
        this.result = result;
        this.data = data;
        this.stringData = stringData;
        this.state = state;
    }

    public Response(String result, Map<String, Object> data, State state) {
        this.result = result;
        this.data = data;
        this.stringData = stringData;
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, String> getStringData() {
        return stringData;
    }

    public State getState() {
        return state;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setStringData(Map<String, String> stringData) {
        this.stringData = stringData;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String toString() {
        return "Response{" +
                "result='" + result + '\'' +
                ", data=" + data +
                ", stringData=" + stringData +
                ", state=" + state +
                '}';
    }

}
