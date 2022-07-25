package it.eng.tpi.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FilePathNoArk {
    protected String dlFile;

    public FilePathNoArk() {
    }

    public FilePathNoArk(String dlFile) {
        this.dlFile = dlFile;
    }

    public String getDlFile() {
        return dlFile;
    }

    public void setDlFile(String dlFile) {
        this.dlFile = dlFile;
    }

}
