package ics372.interfaces;

import ics372.model.Shipment;
import java.util.Collection;

public interface IFileService {
    Collection<Shipment> processInputFile(String file) throws Exception;
}
