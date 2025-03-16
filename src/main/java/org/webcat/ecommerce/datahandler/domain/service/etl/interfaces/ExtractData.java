package org.webcat.ecommerce.datahandler.domain.service.etl.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;

public interface ExtractData
{
  ArrayList<RawData> extract(
      List<String> fileNames);
}
