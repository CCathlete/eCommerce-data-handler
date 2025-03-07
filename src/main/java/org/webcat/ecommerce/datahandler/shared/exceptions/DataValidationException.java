package org.webcat.ecommerce.datahandler.shared.exceptions;

public class DataValidationException extends RuntimeException {
  public DataValidationException(String message) {
    super(message);
  }
}