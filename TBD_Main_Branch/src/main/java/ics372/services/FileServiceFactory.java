package ics372.services;

import ics372.interfaces.IFileService;

public class FileServiceFactory {
    public IFileService getFileService(String type){
        switch (type){
            case "xml":
                return new XmlService();
            case "json":
                return new GsonService();
            default:
                return null;
        }
    }
}
