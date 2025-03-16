package org.webcat.ecommerce.datahandler.domain.service.etl.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.ExtractData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.RawDataRepository;

@Service
/**
 * {@link ExtractDataImpl}
 * <p>
 * A domain service that calls the repository to extract data by ID.
 * </p>
 */
public class ExtractDataImpl
    implements ExtractData
{
  private final RawDataRepository repo;

  ExtractDataImpl(
      RawDataRepository repo)
  {
    this.repo = repo;
  }

  /**
   * Goes over a list of filenames and returns a list of the content of files.
   */
  @Override
  public ArrayList<RawData> extract(
      List<String> fileNames)
  {
    ArrayList<RawData> contentOfFiles =
        new ArrayList<RawData>();
    for (String fileName : fileNames)
    {
      contentOfFiles.add(this.repo
          .findByFileName(fileName));
    }
    return contentOfFiles;
  }
}
