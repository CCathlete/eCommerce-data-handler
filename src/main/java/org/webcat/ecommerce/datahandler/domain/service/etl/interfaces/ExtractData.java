package org.webcat.ecommerce.datahandler.domain.service.etl.interfaces;

import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;

public interface ExtractData {
  RawData extract(Long id);
}