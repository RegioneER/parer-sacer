package it.eng.parer.serie.dto;

import it.eng.parer.viewEntity.ResultVCalcoloSerieUd;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Bonora_L
 */
public class ResultInputSerieBean {

    String record;
    BigDecimal indexRecord;
    List<ResultVCalcoloSerieUd> resultQueryList;

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public List<ResultVCalcoloSerieUd> getResultQueryList() {
        return resultQueryList;
    }

    public void setResultQueryList(List<ResultVCalcoloSerieUd> resultQueryList) {
        this.resultQueryList = resultQueryList;
    }

    public BigDecimal getIndexRecord() {
        return indexRecord;
    }

    public void setIndexRecord(BigDecimal indexRecord) {
        this.indexRecord = indexRecord;
    }

}
