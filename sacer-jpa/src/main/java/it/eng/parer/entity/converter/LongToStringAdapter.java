package it.eng.parer.entity.converter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LongToStringAdapter extends XmlAdapter<String, Long> {
    @Override
    public Long unmarshal(String v) {
        return (v == null ? null : Long.valueOf(v));
    }

    @Override
    public String marshal(Long v) {
        return (v == null ? null : v.toString());
    }
}