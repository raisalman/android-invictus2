package net.invictusmanagement.invictuslifestyle.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ResponseListBrivoSmartHome implements Serializable {
    private Integer count;
    private Object previous;
    private Object next;
    private List<BrivoDeviceData> results = new ArrayList<BrivoDeviceData>();

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

    public List<BrivoDeviceData> getResults() {
        return results;
    }

    public void setResults(List<BrivoDeviceData> results) {
        this.results = results;
    }
}


