package com.project.mediaservice.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.MimeType;

/**
 * Converter class to convert MimeType enum to String for database persistence and vice versa.
 */
@Converter
public class MimeTypeConverter implements AttributeConverter<MimeType, String> {

  @Override
  public String convertToDatabaseColumn(MimeType attribute) {
    return attribute.toString();
  }

  @Override
  public MimeType convertToEntityAttribute(String dbData) {
    return MimeType.valueOf(dbData);
  }
}
